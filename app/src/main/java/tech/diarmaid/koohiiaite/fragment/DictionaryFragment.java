package tech.diarmaid.koohiiaite.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tech.diarmaid.koohiiaite.database.dao.FrequencyDataSource;
import tech.diarmaid.koohiiaite.database.dao.MeaningDataSource;
import tech.diarmaid.koohiiaite.database.dao.ReadingDataSource;
import tech.diarmaid.koohiiaite.R;
import tech.diarmaid.koohiiaite.model.Meaning;
import tech.diarmaid.koohiiaite.model.Reading;

import java.util.List;

/**
 * For display of dictionary derived information
 * about a given kanji related to the heisigId provided
 */
public class DictionaryFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detail_dictionary, container, false);

        TextView textViewKanji = (TextView) view.findViewById(R.id.kanji_detail);
        TextView textViewFrequency = (TextView) view.findViewById(R.id.frequency_detail);
        TextView textViewOnYomi = (TextView) view.findViewById(R.id.onyomi_detail);
        TextView textViewKunYomi = (TextView) view.findViewById(R.id.kunyomi_detail);
        TextView textViewMeanings = (TextView) view.findViewById(R.id.meanings_detail);

        Bundle args = getArguments();
        int heisigIdInt = args.getInt("heisigId", 0);
        String kanji = args.getString("kanji");
        String frequency = getFrequencyFromDatabase(heisigIdInt);
        String onYomi = getReadingFromDatabase(heisigIdInt, 0);
        String kunYomi = getReadingFromDatabase(heisigIdInt, 1);
        String meaning = getMeaningFromDatabase(heisigIdInt);

        // Load the results into the TextViews
        textViewKanji.setText(kanji);
        textViewFrequency.setText(frequency);
        textViewOnYomi.setText(onYomi); //reading
        textViewKunYomi.setText(kunYomi); //reading
        textViewMeanings.setText(meaning); //meaning

        return view;
    }

    public String getReadingFromDatabase(int heisigId, int type)
    {
        ReadingDataSource dataSource = new ReadingDataSource(getActivity());
        dataSource.open();
        Reading reading =
                dataSource.getMeaningForHeisigKanjiId(heisigId, type); //0 for onYomi, 1 for KunYomi
        dataSource.close();
        return reading == null ? "" : reading.getReadingText();
    }

    public String getMeaningFromDatabase(int heisigId)
    {
        StringBuilder sb = new StringBuilder();
        MeaningDataSource dataSource = new MeaningDataSource(getActivity());
        dataSource.open();
        List<Meaning> meanings =
                dataSource.getMeaningsForHeisigKanjiId(heisigId);
        dataSource.close();

        for(Meaning meaning : meanings)
        {
            sb.append(meaning.getMeaningText());
            sb.append(", ");
        }

        String meaningString = sb.toString();
        if(meaningString.endsWith(", "))
        {
            meaningString = meaningString.substring(0, meaningString.length()-2);
        }
        return meaningString;
    }

    public String getFrequencyFromDatabase(int heisigId)
    {
        FrequencyDataSource dataSource = new FrequencyDataSource(getActivity());
        dataSource.open();
        int frequency = dataSource.getFrequencyFor(heisigId);
        dataSource.close();

        return frequency == 999999 ? "-" : String.valueOf(frequency);
    }
}
