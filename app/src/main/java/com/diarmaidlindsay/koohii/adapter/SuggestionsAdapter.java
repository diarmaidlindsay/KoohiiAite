package com.diarmaidlindsay.koohii.adapter;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.provider.BaseColumns;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.widget.TextView;

import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.database.dao.KeywordDataSource;
import com.diarmaidlindsay.koohii.database.dao.PrimitiveDataSource;
import com.diarmaidlindsay.koohii.model.Keyword;
import com.diarmaidlindsay.koohii.model.Primitive;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

/**
 * For the suggestions list of the searchview inside the main kanji list activity
 */
public class SuggestionsAdapter extends SimpleCursorAdapter {
    private List<Keyword> allKeywords;
    private List<Primitive> allPrimitives;
    private List<String> suggestionsList;
    private String previousQuery;
    private String queryNow; //should never become null

    public SuggestionsAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        PrimitiveDataSource primitiveDataSource = new PrimitiveDataSource(context);
        KeywordDataSource keywordDataSource = new KeywordDataSource(context);
        primitiveDataSource.open();
        keywordDataSource.open();
        allKeywords = keywordDataSource.getAllKeywords();
        allPrimitives = primitiveDataSource.getAllPrimitives();
        suggestionsList = new ArrayList<>();
        keywordDataSource.close();
        primitiveDataSource.close();
    }

    @Override
    public void setViewText(TextView v, String text) {
        v.setText(highlight(text, queryNow));
    }

    private CharSequence highlight(String originalText, String search) {
        if(search.length() < 2) {
            return originalText;
        }
        // ignore case and accents
        // the same thing should have been done for the search text
        String normalizedText = Normalizer
                .normalize(originalText, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase(Locale.ENGLISH);
        int start = normalizedText.indexOf(search.toLowerCase(Locale.ENGLISH));
        if (start < 0) {
            // not found, nothing to to
            return originalText;
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            Spannable highlighted = new SpannableString(originalText);
            while (start >= 0) {
                int spanStart = Math.min(start, originalText.length());
                int spanEnd = Math.min(start + search.length(),
                        originalText.length());
                highlighted.setSpan(new ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.ka_neutral)),
                        spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                start = normalizedText.indexOf(search, spanEnd);
            }
            return highlighted;
        }
    }

    public void populateSuggestions(String query)
    {
        final MatrixCursor cursor = new MatrixCursor(new String[]{ BaseColumns._ID, "keywordPrimitive" });

        //if there is a comma, only get suggestions for last part of query string
        if(query.contains(",")) {
            String[] queries = query.split(",");
            query = queries[queries.length-1];
        }
        query = query.toLowerCase().trim();
        queryNow = query;

        if(query.length() < 2) {
            suggestionsList.clear();
            previousQuery = null;
            changeCursor(cursor);
            return;
        }
        Set<String> suggestionsSet = new HashSet<>();

        //some text was deleted so we should fall back to suggest from all primitives and keywords
        if(previousQuery == null || query.length() < previousQuery.length()) {
            for (Primitive primitive : allPrimitives) {
                if (primitive.getPrimitiveText().toLowerCase().contains(query)) {
                    suggestionsSet.add(primitive.getPrimitiveText());
                }
            }

            for (Keyword keyword : allKeywords) {
                if (keyword.getKeywordText().toLowerCase().contains(query)) {
                    suggestionsSet.add(keyword.getKeywordText());
                }
            }
        }
        //search the subset of results from the previous query
        else
        {
            for(String text : suggestionsList) {
                if(text.toLowerCase().contains(query)) {
                    suggestionsSet.add(text);
                }
            }
        }
        suggestionsList = new ArrayList<>(suggestionsSet);
        Collections.sort(suggestionsList, new SortIgnoreCase());

        for(int i = 0; i < suggestionsList.size(); i++) {
            cursor.addRow(new Object[]{i, suggestionsList.get(i)});
        }
        previousQuery = query;
        changeCursor(cursor);
    }

    public class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }
}
