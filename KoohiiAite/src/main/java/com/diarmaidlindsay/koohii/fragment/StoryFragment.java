package com.diarmaidlindsay.koohii.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.database.dao.StoryDataSource;
import com.diarmaidlindsay.koohii.model.HeisigKanji;
import com.diarmaidlindsay.koohii.model.Story;

/**
 * For display of the Story related to the heisig id
 * which was provided. Also allow editing of story.
 * Follow links from stories to other Kanji detail pages.
 */
public class StoryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_story, container, false);

        TextView textViewHeisigId = (TextView) view.findViewById(R.id.heisig_id_detail);
        TextView textViewKanji = (TextView) view.findViewById(R.id.kanji_detail);
        TextView textViewKeyword = (TextView) view.findViewById(R.id.keyword_detail);
        TextView textViewStory = (TextView) view.findViewById(R.id.story_detail);

        Bundle args = getArguments();
        int heisigIdInt = args.getInt("heisigId", 0);

        String heisigId = HeisigKanji.getHeisigIdAsString(heisigIdInt);
        String kanji = args.getString("kanji");
        String keyword = args.getString("keyword");
        String userKeyword = args.getString("userKeyword");
        String storyText = getStoryFromDatabase(heisigIdInt);

        if(userKeyword != null)
        {
            SpannableString keywordText = new SpannableString(userKeyword + " ("+keyword+")");
            keywordText.setSpan(new TextAppearanceSpan(getActivity(), R.style.GreyItalicSmallText), userKeyword.length(), keywordText.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textViewKeyword.setText(keywordText, TextView.BufferType.SPANNABLE);
        } else
        {
            textViewKeyword.setText(keyword);
        }

        // Load the results into the TextViews
        textViewHeisigId.setText(heisigId);
        textViewKanji.setText(kanji);

        textViewStory.setText(storyText);

        return view;
    }

    private String getStoryFromDatabase(int heisigId)
    {
        StoryDataSource dataSource = new StoryDataSource(getActivity());
        dataSource.open();
        Story story = dataSource.getStoryForHeisigKanjiId(heisigId);
        dataSource.close();

        return story == null ? "" : story.getStory_text();
    }
}
