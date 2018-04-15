package com.diarmaidlindsay.koohii.fragment;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.adapter.KanjiSampleWordsAdapter;

/**
 * Display contents of sample_words table for given heisig_id
 */
public class SampleWordsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_sample_words, container, false);
        KanjiSampleWordsAdapter adapter = new KanjiSampleWordsAdapter(getActivity(), getArguments());
        ListView listView = (ListView) view.findViewById(R.id.sample_words_list_view);

        listView.setAdapter(adapter);

        return view;
    }
}
