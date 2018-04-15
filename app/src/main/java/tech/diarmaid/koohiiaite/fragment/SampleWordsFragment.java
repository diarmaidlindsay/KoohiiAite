package tech.diarmaid.koohiiaite.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import tech.diarmaid.koohiiaite.R;
import tech.diarmaid.koohiiaite.adapter.KanjiSampleWordsAdapter;

/**
 * Display contents of sample_words table for given heisig_id
 */
public class SampleWordsFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_sample_words, container, false);
        KanjiSampleWordsAdapter adapter = new KanjiSampleWordsAdapter(getActivity(), getArguments());
        ListView listView = view.findViewById(R.id.sample_words_list_view);

        listView.setAdapter(adapter);

        return view;
    }
}
