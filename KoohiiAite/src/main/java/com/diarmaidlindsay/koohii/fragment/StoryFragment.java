package com.diarmaidlindsay.koohii.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
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
import com.diarmaidlindsay.koohii.database.dao.StoryDataSource;
import com.diarmaidlindsay.koohii.database.dao.UserKeywordDataSource;
import com.diarmaidlindsay.koohii.model.HeisigKanji;
import com.diarmaidlindsay.koohii.model.Story;

/**
 * For display of the Story related to the heisig id
 * which was provided. Also allow editing of story.
 * Follow links from stories to other Kanji detail pages.
 */
public class StoryFragment extends Fragment {
    private Button buttonKeyword;
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
        final int heisigIdInt = args.getInt("heisigId", 0);

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
                        //if userKeyword not null that means there was a previous user keyword entered and we're updating it
                        submitKeyword(heisigIdInt, keywordEditText.getText().toString(), userKeyword != null, originalKeyword);
                        dialog.dismiss();
                    }
                });

                buttonDefault.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteKeyword(heisigIdInt, originalKeyword);
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

        textViewStory.setText(storyText);
        updateWidgets();
        return view;
    }

    private void submitKeyword(int heisigId, String keywordText, boolean update, String originalKeyword)
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

        Toast.makeText(getActivity(), success ? "Keyword Changed" : "Error! Keyword not Changed",
                Toast.LENGTH_SHORT).show();
        if(success) {
            userKeyword = keywordText;
            updateWidgets();
        }
        dataSource.close();
    }

    private void deleteKeyword(int heisigId, String originalKeyword)
    {
        UserKeywordDataSource dataSource = new UserKeywordDataSource(getActivity());
        dataSource.open();
        boolean success = false;
        if(dataSource.deleteKeyword(heisigId)) {
            userKeyword = null;
            success = true;
            updateWidgets();
        }

        Toast.makeText(getActivity(), success ? "Keyword Reset" : "Error! Keyword not Changed",
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
}
