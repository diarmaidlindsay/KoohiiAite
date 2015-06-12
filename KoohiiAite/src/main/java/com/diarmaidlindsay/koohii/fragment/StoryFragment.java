package com.diarmaidlindsay.koohii.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.model.HeisigKanji;

/**
 * For display of the Story related to the heisig id
 * which was provided. Also allow editing of story.
 * Follow links from stories to other Kanji detail pages.
 */
public class StoryFragment extends Fragment {
    private TextView textViewHeisigId;
    private TextView textViewKanji;
    private TextView textViewKeyword;

    @Override
    public void setArguments(Bundle args) {
        super.setArguments(args);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_story, container, false);

        textViewHeisigId = (TextView) view.findViewById(R.id.heisig_id_detail);
        textViewKanji = (TextView) view.findViewById(R.id.kanji_detail);
        textViewKeyword = (TextView) view.findViewById(R.id.keyword_detail);

        Bundle args = getArguments();
        String heisigId = HeisigKanji.getHeisigIdAsString(args.getInt("heisigId", 0));
        String kanji = args.getString("kanji");
        String keyword = args.getString("keyword");

        // Load the results into the TextViews
        textViewHeisigId.setText(heisigId);
        textViewKanji.setText(kanji);
        textViewKeyword.setText(keyword);

        return view;
    }
}
