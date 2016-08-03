package com.diarmaidlindsay.koohii.fragment;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.activity.KanjiDetailActivity;
import com.diarmaidlindsay.koohii.database.dao.HeisigKanjiDataSource;
import com.diarmaidlindsay.koohii.database.dao.KeywordDataSource;
import com.diarmaidlindsay.koohii.database.dao.StoryDataSource;
import com.diarmaidlindsay.koohii.database.dao.UserKeywordDataSource;
import com.diarmaidlindsay.koohii.model.HeisigKanji;
import com.diarmaidlindsay.koohii.model.Keyword;
import com.diarmaidlindsay.koohii.model.Story;
import com.diarmaidlindsay.koohii.utils.ToastUtil;
import com.diarmaidlindsay.koohii.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * For display of the Story related to the heisig id
 * which was provided. Also allow editing of story.
 * Follow links from stories to other Kanji detail pages.
 */
public class StoryFragment extends Fragment {
    private Button buttonKeyword;
    private int heisigIdInt;
    private String userKeyword;
    private String originalKeyword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_story, container, false);

        TextView textViewHeisigId = (TextView) view.findViewById(R.id.heisig_id_detail);
        TextView textViewKanji = (TextView) view.findViewById(R.id.kanji_detail);
        buttonKeyword = (Button) view.findViewById(R.id.keyword_detail);
        TextView textViewStory = (TextView) view.findViewById(R.id.story_detail);

        Bundle args = getArguments();
        heisigIdInt = args.getInt("heisigId", 0);

        String heisigId = HeisigKanji.getHeisigIdAsString(heisigIdInt);
        String kanji = args.getString("kanji");
        originalKeyword = args.getString("keyword");
        userKeyword = args.getString("userKeyword");
        String storyText = getStoryFromDatabase(heisigIdInt);

        buttonKeyword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getActivity());
                dialog.setContentView(R.layout.dialog_box_keyword);
                dialog.setTitle(R.string.dialog_title_edit_keyword);

                final EditText keywordEditText = (EditText) dialog.findViewById(R.id.keyword_dialog_edittext);
                Button submitButton = (Button) dialog.findViewById(R.id.keyword_dialog_submit_button);
                Button buttonDefault = (Button) dialog.findViewById(R.id.keyword_dialog_default_button);
                Button cancelButton = (Button) dialog.findViewById(R.id.keyword_dialog_cancel_button);

                keywordEditText.setText(userKeyword == null ? originalKeyword : userKeyword);
                keywordEditText.setSelectAllOnFocus(true);
                buttonDefault.setEnabled(userKeyword != null);

                submitButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //only submit if user actually changes the keyword from original
                        if(!keywordEditText.getText().toString().equals(originalKeyword)) {
                            //if userKeyword not null that means there was a previous user keyword entered and we're updating it
                            submitKeyword(heisigIdInt, keywordEditText.getText().toString(), userKeyword != null);
                        }
                        dialog.dismiss();
                    }
                });

                buttonDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteKeyword(heisigIdInt);
                        dialog.dismiss();
                    }
                });

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                dialog.show();
                dialog.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
                        WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
                dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            }
        });

        // Load the results into the TextViews
        textViewHeisigId.setText(heisigId);
        textViewKanji.setText(kanji);

        textViewStory.setText(formatStory(storyText), TextView.BufferType.SPANNABLE);
        textViewStory.setMovementMethod(LinkMovementMethod.getInstance());
        updateWidgets();
        return view;
    }

    /**
     * Replace hash and asterix words with bolded and italisised versions of those words
     * as with kanji.koohii.com
     */
    private SpannableString formatStory(String storyText) {
        KeywordDataSource keywordDao = new KeywordDataSource(getActivity());
        UserKeywordDataSource userKeywordDao = new UserKeywordDataSource(getActivity());
        HeisigKanjiDataSource heisigKanjiDataSource = new HeisigKanjiDataSource(getActivity());
        keywordDao.open();
        userKeywordDao.open();
        heisigKanjiDataSource.open();
        SpannableString formattedStory = new SpannableString(storyText);
        List<StoryFormat> italicSpanStarts = new ArrayList<>();
        List<StoryFormat> italicSpanEnds = new ArrayList<>();
        List<StoryFormat> boldSpanStarts = new ArrayList<>();
        List<StoryFormat> boldSpanEnds = new ArrayList<>();
        //TODO : where there is kanji or number surrounded by curly braces, make it clickable
        List<StoryFormat> braceSpanStarts = new ArrayList<>();
        List<StoryFormat> braceSpanEnds = new ArrayList<>();
        Map<String, Integer> kanjiToHeisigId = new HashMap<>();

        //remove double quotes in stories ("")
        storyText = storyText.replaceAll("\"\"", "\"");

        char[] storyChars = storyText.toCharArray();
        boolean isItalic = false;
        boolean isBold = false;
        int order = 0;
        for (int i = 0; i < storyChars.length; i++) {
            char c = storyChars[i];
            if (c == '*') {
                //found italic
                if (!isItalic) {
                    //italic span start
                    isItalic = true;
                    italicSpanStarts.add(new StoryFormat(i, order++));
                } else {
                    isItalic = false;
                    italicSpanEnds.add(new StoryFormat(i, order++));
                }
            } else if (c == '#') {
                if (!isBold) {
                    isBold = true;
                    boldSpanStarts.add(new StoryFormat(i, order++));
                } else {
                    isBold = false;
                    boldSpanEnds.add(new StoryFormat(i, order++));
                }
            } else if (c == '{') {
                braceSpanStarts.add(new StoryFormat(i, order++));
            } else if (c == '}') {
                /*  subtract last braceSpanStarts from i (which is a brace end)..
                    {不} (kanji)
                    567 = 2 (7-5)
                    order += 1; (the last brace)
                    {675} (heisig_id)
                    56789 = 4 (9-5)
                    order += 3; (7, 5 and the last brace)
                 */
                //everything within the braces will be reduced to a length of 1 (a single kanji) we must account for the missing indices
                int orderMod = i - braceSpanStarts.get(braceSpanStarts.size()-1).index - 1;
                braceSpanEnds.add(new StoryFormat(i, order+=orderMod));
            }
        }

        //if malformatted, return original text
        if ((italicSpanEnds.size() != italicSpanStarts.size()) || boldSpanStarts.size() != boldSpanEnds.size()) {
            return formattedStory;
        }

        //go through all braces and if it contains a heisig_id, replace with the kanji, else remove the brackts
        if(braceSpanStarts.size() > 0) {
            for(int i = 0; i < braceSpanStarts.size(); i++) {
                StoryFormat start = braceSpanStarts.get(i);
                //if it's a heisig_id, look up the kanji
                if(!Utils.isKanji(storyText.charAt(start.index+1))) {
                    Integer heisigId = Integer.parseInt(storyText.substring(start.index+1, braceSpanEnds.get(i).index));
                    HeisigKanji kanji = heisigKanjiDataSource.getKanjiFor(heisigId);
                    kanjiToHeisigId.put(kanji.getKanji(), kanji.getId());
                } else {
                    //it's already a kanji, presumably, so just take away the brackets around it
                    String kanjiString = String.valueOf(storyText.charAt(start.index+1));
                    HeisigKanji kanji = heisigKanjiDataSource.getHeisigFor(kanjiString);
                    kanjiToHeisigId.put(kanji.getKanji(), kanji.getId());
                }
            }
        }

        for(String kanji : kanjiToHeisigId.keySet()) {
            storyText = storyText.replaceFirst(String.valueOf(kanjiToHeisigId.get(kanji)), kanji);
            storyText = storyText.replaceFirst("\\{"+kanji+"\\}", kanji);
        }

        //remove hashes and asterixes, string will shorten, we must take this into account with the indexes
        storyText = storyText.replaceAll("\\*", "");
        storyText = storyText.replaceAll("#", "");

        //any time the keyword is mentioned in the story, mark it as bold
        //we can use plain integer array to hold indexes instead of StoryFormat objects because indexes don't need adjustment
        ArrayList<Integer> keywordSpanStarts = new ArrayList<>();
        Pattern keywordPattern = Pattern.compile(userKeyword == null ? originalKeyword.toLowerCase() : userKeyword.toLowerCase());
        Matcher keywordMatcher = keywordPattern.matcher(storyText.toLowerCase());
        while (keywordMatcher.find()) {
            keywordSpanStarts.add(keywordMatcher.start());
        }

        //any time there is all uppercase words in the story, try to match with a keyword or user keywork and make it clickable if so
        ArrayList<Integer> uppercaseSpanStarts = new ArrayList<>();
        ArrayList<Integer> uppercaseSpanEnds = new ArrayList<>();
        String regEx = "[A-Z]+";
        Pattern uppercasePattern = Pattern.compile(regEx);
        Matcher uppercaseMatcher = uppercasePattern.matcher(storyText);
        while (uppercaseMatcher.find()) {
            uppercaseSpanStarts.add(uppercaseMatcher.start());
            uppercaseSpanEnds.add(uppercaseMatcher.end());
        }

        formattedStory = new SpannableString(storyText);

        //mark italics
        for (int i = 0; i < italicSpanStarts.size(); i++) {
            int indexStarts = italicSpanStarts.get(i).index - italicSpanStarts.get(i).order;
            int indexEnds = italicSpanEnds.get(i).index - italicSpanEnds.get(i).order;
            formattedStory.setSpan(new TextAppearanceSpan(getActivity(), R.style.storyAsterix),
                    indexStarts, indexEnds, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        //mark bolds
        for (int i = 0; i < boldSpanStarts.size(); i++) {
            int indexStarts = boldSpanStarts.get(i).index - boldSpanStarts.get(i).order;
            int indexEnds = boldSpanEnds.get(i).index - boldSpanEnds.get(i).order;
            if(indexEnds - indexStarts < 2) {
                //ignore words of length 1 for example "I"
                continue;
            }
            //if the text to be bolded is a keyword, we'll do that in the next loop instead
            if (!keywordSpanStarts.contains(indexStarts)) {
                formattedStory.setSpan(new TextAppearanceSpan(getActivity(), R.style.storyHash),
                        indexStarts, indexEnds, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        //TODO : Match multiple word keywords first - eg, "SORT OF THING" in 0510
        //make uppercase which match a keyword or user keyword clickable
        for (int i = 0; i < uppercaseSpanStarts.size(); i++) {
            int start = uppercaseSpanStarts.get(i);
            int end = uppercaseSpanEnds.get(i);
            String uppercaseWord = storyText.substring(start, end);
            if(uppercaseWord.length() < 2) {
                //ignore words of length 1 for example "I"
                continue;
            }
            final Keyword matchingKeyword = keywordDao.getKeywordMatching(uppercaseWord);
            Keyword matchingUserKeyword = userKeywordDao.getKeywordMatching(uppercaseWord);
            ClickableSpan span = null;
            //prioritise user keyword
            if (matchingUserKeyword != null) {
                span = getSpanForKeyword(matchingUserKeyword);
            }
            else if (matchingKeyword != null) {
                span = getSpanForKeyword(matchingKeyword);

            }
            if(span != null) {
                formattedStory.setSpan(span, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }

        //make all kanji which were surrounded by braces clickable
        for (StoryFormat braceSpanStart : braceSpanStarts) {
            int kanjiIndex = braceSpanStart.index - braceSpanStart.order;
            String kanji = String.valueOf(storyText.charAt(kanjiIndex));
            int heisigId = kanjiToHeisigId.get(kanji);
            ClickableSpan span = getSpanForHeisigId(heisigId);
            formattedStory.setSpan(span, kanjiIndex, kanjiIndex + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        int keywordLength = userKeyword == null ? originalKeyword.length() : userKeyword.length();
        //mark keyword occurances as bold
        for (Integer start : keywordSpanStarts) {
            if(keywordLength < 2) {
                //ignore words of length 1 for example "I"
                continue;
            }
            formattedStory.setSpan(new TextAppearanceSpan(getActivity(), R.style.storyHash),
                    start, start + keywordLength, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        keywordDao.close();
        userKeywordDao.close();
        heisigKanjiDataSource.close();
        return formattedStory;
    }

    private ClickableSpan getSpanForHeisigId(final int heisigId) {
        return new ClickableSpan() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), KanjiDetailActivity.class);
                intent.putExtra("filteredListIndex", 0);
                intent.putExtra("filteredIdList", new String[] {String.valueOf(heisigId)});
                //TODO : startActivityForResult just like in KanjiListAdapter so that any changes made is passed back to KanjiListActivityq
                getActivity().startActivity(intent);
            }
        };
    }

    private ClickableSpan getSpanForKeyword(final Keyword matchingKeyword) {
        return getSpanForHeisigId(matchingKeyword.getHeisigId());
    }

    private void submitKeyword(int heisigId, String keywordText, boolean update)
    {
        UserKeywordDataSource dataSource = new UserKeywordDataSource(getActivity());
        dataSource.open();
        boolean success = false;
        if(update)
        {
            if(dataSource.updateKeyword(heisigId, keywordText))
                success = true;
        } else {
            if(dataSource.insertKeyword(heisigId, keywordText))
                success = true;
        }

        ToastUtil.makeText(getActivity(), success ? "Keyword Changed" : "Error! Keyword not Changed",
                Toast.LENGTH_SHORT).show();
        if(success) {
            userKeyword = keywordText;
            updateWidgets();
            updateParentActivity();
        }
        dataSource.close();
    }

    private void deleteKeyword(int heisigId)
    {
        UserKeywordDataSource dataSource = new UserKeywordDataSource(getActivity());
        dataSource.open();
        boolean success = false;
        if(dataSource.deleteKeyword(heisigId)) {
            userKeyword = null;
            success = true;
            updateWidgets();
            updateParentActivity();
        }

        ToastUtil.makeText(getActivity(), success ? "Keyword Reset" : "Error! Keyword not Changed",
                Toast.LENGTH_SHORT).show();
        dataSource.close();
    }

    private String getStoryFromDatabase(int heisigId)
    {
        StoryDataSource dataSource = new StoryDataSource(getActivity());
        dataSource.open();
        Story story = dataSource.getStoryForHeisigKanjiId(heisigId);
        dataSource.close();

        return story == null ? "" : story.getStory_text();
    }

    private void updateWidgets()
    {
        //update Keyword button text
        if(userKeyword == null)
        {
            buttonKeyword.setText(originalKeyword);
        } else {
            SpannableString keywordText = new SpannableString(userKeyword + " ("+originalKeyword+")");
            keywordText.setSpan(new TextAppearanceSpan(getActivity(), R.style.GreyItalicSmallText), userKeyword.length(), keywordText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            buttonKeyword.setText(keywordText, TextView.BufferType.SPANNABLE);
        }
    }

    /**
     * Tell the KanjiListActivity that user changed keyword(s) and it should update dataset of its Adapter
     */
    private void updateParentActivity()
    {
        if(getActivity() instanceof KanjiDetailActivity)
        {
            if(userKeyword == null)
            {
                ((KanjiDetailActivity) getActivity()).setResult(heisigIdInt, originalKeyword);
            } else {
                ((KanjiDetailActivity) getActivity()).setResult(heisigIdInt, userKeyword);
            }
        }
    }

    private class StoryFormat {
        int index;
        int order;
        //string will shorten when hashes and asterixes are removed
        //so use the order to adjust the index afterwards

        StoryFormat(int index, int order) {
            this.index = index;
            this.order = order;
        }
    }
}
