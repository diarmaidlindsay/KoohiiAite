package com.diarmaidlindsay.koohii.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.model.Kanji;

import java.util.List;

/**
 * Adapter for Main Kanji ListView
 */
public class KanjiListAdapter extends BaseAdapter {

    List<Kanji> kanjiList;
    LayoutInflater layoutInflater;

    public KanjiListAdapter(List<Kanji> kanjiList, Context context)
    {
        this.kanjiList = kanjiList;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return kanjiList.size();
    }

    @Override
    public Object getItem(int position) {
        return kanjiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Kanji theKanji = (Kanji) getItem(position);
        if(convertView == null)
        {
            convertView = layoutInflater.inflate(R.layout.list_item_kanji, null);
        }
        TextView heisig = (TextView)convertView.findViewById(R.id.heisig_id);
        TextView kanji =  (TextView)convertView.findViewById(R.id.kanji_list_item);
        TextView keyword = (TextView)convertView.findViewById(R.id.keyword);

        heisig.setText(String.valueOf(theKanji.getHeisig_id()));
        kanji.setText(theKanji.getKanji());
        keyword.setText(theKanji.getKeyword());
        return convertView;
    }

}
