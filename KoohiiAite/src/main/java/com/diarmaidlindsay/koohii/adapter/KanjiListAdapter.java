package com.diarmaidlindsay.koohii.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.model.Kanji;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Adapter for Main Kanji ListView
 */
public class KanjiListAdapter extends BaseAdapter {

    List<Kanji> masterList;
    List<Kanji> filteredList = new ArrayList<>();

    LayoutInflater layoutInflater;

    public KanjiListAdapter(List<Kanji> kanjiList, Context context)
    {
        this.masterList = kanjiList;
        layoutInflater = LayoutInflater.from(context);
        filter("");
    }

    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
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

        heisig.setText(getHeisigIdAsString(theKanji.getHeisig_id()));
        kanji.setText(theKanji.getKanji());
        keyword.setText(theKanji.getKeyword());

        // Listen for ListView Item Click
//        view.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View arg0) {
//                // Send single item click data to SingleItemView Class
//                Intent intent = new Intent(mContext, SingleItemView.class);
//                // Pass all data rank
//                intent.putExtra("rank",(worldpopulationlist.get(position).getRank()));
//                // Pass all data country
//                intent.putExtra("country",(worldpopulationlist.get(position).getCountry()));
//                // Pass all data population
//                intent.putExtra("population",(worldpopulationlist.get(position).getPopulation()));
//                // Pass all data flag
//                // Start SingleItemView Class
//                mContext.startActivity(intent);
//            }
//        });

        return convertView;
    }

    private String getHeisigIdAsString(int heisigId)
    {
        String prefixZeros = "";

        if(heisigId < 1000)
        {
            prefixZeros += "0";
            if(heisigId < 100)
            {
                prefixZeros += "0";
                if(heisigId < 10)
                {
                    prefixZeros += "0";
                }
            }
        }

        return prefixZeros + heisigId;
    }

    public void filter(String filterText)
    {
        filterText = filterText.toLowerCase(Locale.getDefault());
        filteredList.clear();
        if (filterText.length() == 0) {
            filteredList.addAll(masterList);
        }
        else
        {
            for (Kanji kanji : masterList)
            {
                String id = String.valueOf(kanji.getHeisig_id());
                String kanjiChar = kanji.getKanji();
                String keyword = kanji.getKeyword();

                if (keyword.toLowerCase(Locale.getDefault()).contains(filterText)
                        || id.equals(filterText)
                        || kanjiChar.equals(filterText))
                {
                    filteredList.add(kanji);
                }
            }
        }
        notifyDataSetChanged();
    }
}
