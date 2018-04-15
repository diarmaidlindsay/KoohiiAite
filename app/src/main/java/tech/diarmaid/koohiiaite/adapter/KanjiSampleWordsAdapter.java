package tech.diarmaid.koohiiaite.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import tech.diarmaid.koohiiaite.R;
import tech.diarmaid.koohiiaite.database.dao.SampleWordDataSource;
import tech.diarmaid.koohiiaite.model.SampleWord;

/**
 * Adapter for the Sample words tab of the Kanji Detail view
 */
public class KanjiSampleWordsAdapter extends BaseAdapter {
    private Context mContext;

    private List<SampleWord> sampleWordList;
    private int heisigId;

    private LayoutInflater layoutInflater;

    static class ViewHolderItem {
        TextView kanji;
        TextView hiragana;
        TextView english;
        TextView category;
        TextView frequency;
    }

    public KanjiSampleWordsAdapter(Context context, Bundle args)
    {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        heisigId = args.getInt("heisigId");
        initialiseDatasets();
    }

    @Override
    public int getCount() {
        return sampleWordList.size();
    }

    @Override
    public Object getItem(int position) {
        return sampleWordList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolderItem viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_sample_word, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.kanji = convertView.findViewById(R.id.sample_kanji);
            viewHolder.hiragana = convertView.findViewById(R.id.sample_hiragana);
            viewHolder.english = convertView.findViewById(R.id.sample_english);
            viewHolder.category = convertView.findViewById(R.id.sample_category);
            viewHolder.frequency = convertView.findViewById(R.id.sample_frequency);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        SampleWord sampleWord = (SampleWord) getItem(position);

        viewHolder.kanji.setText(sampleWord.getKanjiWord());
        viewHolder.hiragana.setText(sampleWord.getHiraganaReading());
        viewHolder.english.setText(sampleWord.getEnglishMeaning());
        viewHolder.category.setText(sampleWord.getCategory());
        viewHolder.frequency.setText(String.valueOf(sampleWord.getFrequency()));

        return convertView;
    }

    private void initialiseDatasets() {
        SampleWordDataSource dataSource = new SampleWordDataSource(mContext);
        dataSource.open();
        sampleWordList = dataSource.getSampleWordsFor(heisigId);
    }
}
