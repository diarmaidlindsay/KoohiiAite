package com.diarmaidlindsay.koohii.activity;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Bundle;
import android.os.Handler;
import android.provider.BaseColumns;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.adapter.KanjiListAdapter;
import com.diarmaidlindsay.koohii.database.dao.KeywordDataSource;
import com.diarmaidlindsay.koohii.database.dao.PrimitiveDataSource;
import com.diarmaidlindsay.koohii.model.Keyword;
import com.diarmaidlindsay.koohii.model.Primitive;

import java.util.*;

/**
 *  Koohii Aite uses Heisig Old Edition
 *  Volume 1 : 5th edition or earlier.
    Volume 3 : 1st or 2nd edition.

    This is because I use rikaikun Chrome plugin, which gives me old edition Heisig indexes.
    At some point I should update the application to handle new edition indexes as well.
    And update rikaikun's kanji.dat as well.

    https://docs.google.com/spreadsheets/d/1Z0BUSie8wh0JqlUejezs3EqauJuF-zKEomOQnqm9J08/edit#gid=0
 *
 *  Entry point into the application.
 */
public class KanjiListActivity extends AppCompatActivity {

    private KanjiListAdapter kanjiListAdapter;
    private CursorAdapter suggestionAdapter;
    private MenuItem searchItem;
    private SearchView searchView;
    private TextView result;

    private List<Keyword> allKeywords;
    private List<Primitive> allPrimitives;
    private List<String> suggestionsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_list);

        ListView kanjiList = (ListView) findViewById(R.id.kanjiListView);

        suggestionAdapter = getCursorAdapter();
        kanjiListAdapter = new KanjiListAdapter(this);
        kanjiList.setAdapter(kanjiListAdapter);
        result = (TextView) findViewById(R.id.result);

        PrimitiveDataSource primitiveDataSource = new PrimitiveDataSource(this);
        KeywordDataSource keywordDataSource = new KeywordDataSource(this);
        primitiveDataSource.open();
        keywordDataSource.open();
        allKeywords = keywordDataSource.getAllKeywords();
        allPrimitives = primitiveDataSource.getAllPrimitives();
        suggestionsList = new ArrayList<>();
        keywordDataSource.close();
        primitiveDataSource.close();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_list, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(getTextListener());
        searchView.setOnSuggestionListener(getSuggestionListener());
        searchView.setSuggestionsAdapter(suggestionAdapter);

        return super.onCreateOptionsMenu(menu);
    }

    private CursorAdapter getCursorAdapter()
    {
        final String[] from = new String[] {"keywordPrimitive"};
        final int[] to = new int[] {R.id.suggestion_item};

        return new SimpleCursorAdapter(this,
                R.layout.list_item_suggestion,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    private void populateSuggestions(String query)
    {
//        if(query.length() < 2)
//        {
//            suggestionsList.clear();
//            return;
//        }
        query = query.toLowerCase();
        Set<String> suggestionsSet = new HashSet<>();
        final MatrixCursor cursor = new MatrixCursor(new String[]{ BaseColumns._ID, "keywordPrimitive" });

        //have to search everything if no fallback searches
//        if(suggestionsList.size() == 0) {
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
//        }
        suggestionsList = new ArrayList<>(suggestionsSet);
        Collections.sort(suggestionsList, new SortIgnoreCase());

        for(int i = 0; i < suggestionsList.size(); i++)
        {
            cursor.addRow(new Object[]{i, suggestionsList.get(i)});
        }

        suggestionAdapter.changeCursor(cursor);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * If there's a comma, we're only interested in last part after comma
     * for suggestions
     */
    private String getLastPart(String query)
    {
        if(query.lastIndexOf(",") == -1)
        {
            return query;
        }

        String[] parts = query.split(",");

        return parts[parts.length-1];
    }

    private SearchView.OnQueryTextListener getTextListener()
    {
        return new SearchView.OnQueryTextListener() {
            private String text;

            @Override
            public boolean onQueryTextSubmit(String query) {
                text = query;
                mHandler.removeCallbacks(mFilterTask);
                //Delay after user input to smooth the user experience
                mHandler.postDelayed(mFilterTask, 0);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                text = newText;
                populateSuggestions(getLastPart(text));
                mHandler.removeCallbacks(mFilterTask);
                //Delay after user input to smooth the user experience
                mHandler.postDelayed(mFilterTask, 1000);
                return true;
            }

            Runnable mFilterTask = new Runnable() {

                @Override
                public void run() {
                    kanjiListAdapter.filter(text);
                    result.setText(kanjiListAdapter.getCount() + " items displayed");
                }
            };

            private Handler mHandler = new Handler();
        };
    }

    private SearchView.OnSuggestionListener getSuggestionListener()
    {
        return new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String choice = cursor.getString(1);
                //if there's a comma in the search query, it means multiple terms
                //in that case we don't want to replace the whole query
                String query = searchView.getQuery().toString();
                query = query.replace(getLastPart(query), choice);
                searchView.setQuery(query, false);
//                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String choice = cursor.getString(1);
                String query = searchView.getQuery().toString();
                query = query.replace(getLastPart(query), choice);
                searchView.setQuery(query, false);
//                searchView.clearFocus();
                return true;
            }
        };
    }

    /**
     * Hide soft keyboard then collapse search view.
     */
    public void collapseSearchView()
    {
        //if you don't hide soft keyboard, you get a blank space where the keyboard was momentarily when returning to list view
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        searchItem.collapseActionView();
    }

    public class SortIgnoreCase implements Comparator<Object> {
        public int compare(Object o1, Object o2) {
            String s1 = (String) o1;
            String s2 = (String) o2;
            return s1.toLowerCase().compareTo(s2.toLowerCase());
        }
    }
}
