package com.diarmaidlindsay.koohii.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.database.dao.HeisigKanjiDataSource;
import com.diarmaidlindsay.koohii.database.dao.KeywordDataSource;
import com.diarmaidlindsay.koohii.model.HeisigKanji;
import com.diarmaidlindsay.koohii.model.Keyword;

import java.sql.SQLException;
import java.util.*;

/**
 * Adapter for Main Kanji ListView
 */
public class KanjiListAdapter extends BaseAdapter {
    private final String LOG = this.getClass().getSimpleName();
    private Context context;

    private List<HeisigKanji> masterList; //list of all HeisigKanjis
    private Set<Integer> filteredSet = new HashSet<>(); //filtered heisig_ids
    private List<HeisigKanji> filteredList = new ArrayList<>(); //filtered HeisigKanjis for display
    private List<Keyword> keywordList; //list of all Keywords

    private LayoutInflater layoutInflater;

    /**
     * http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
     */
    static class ViewHolderItem {
        TextView heisig;
        TextView kanji;
        TextView keyword;
    }

    public KanjiListAdapter(Context context) {
        this.context = context;
        layoutInflater = LayoutInflater.from(context);
        initialiseDatasets();
        filter("");
    }

    private void initialiseDatasets() {
        KeywordDataSource keywordDataSource = new KeywordDataSource(context);
        HeisigKanjiDataSource heisigKanjiDataSource = new HeisigKanjiDataSource(context);
        heisigKanjiDataSource.open();
        keywordDataSource.open();
        masterList = heisigKanjiDataSource.getAllKanji();
        keywordList = keywordDataSource.getAllKeywords();
        heisigKanjiDataSource.close(); //Open again when needed
        keywordDataSource.close(); //Open again when needed
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
        ViewHolderItem viewHolder;

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_kanji, null);
            viewHolder = new ViewHolderItem();
            viewHolder.heisig = (TextView) convertView.findViewById(R.id.heisig_id);
            viewHolder.kanji = (TextView) convertView.findViewById(R.id.kanji_list_item);
            viewHolder.keyword = (TextView) convertView.findViewById(R.id.keyword);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        HeisigKanji theKanji = (HeisigKanji) getItem(position);

        viewHolder.heisig.setText(getHeisigIdAsString(theKanji.getId()));
        viewHolder.kanji.setText(theKanji.getKanji());
        viewHolder.keyword.setText(keywordList.get(theKanji.getId() - 1).getKeywordText());
        int bgColor = theKanji.isJoyo() ?
                Color.parseColor("#abbaab") : Color.parseColor("#FFFFFF");

        convertView.setBackgroundColor(bgColor);
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

    private String getHeisigIdAsString(int heisigId) {
        String prefixZeros = "";

        if (heisigId < 1000) {
            prefixZeros += "0";
            if (heisigId < 100) {
                prefixZeros += "0";
                if (heisigId < 10) {
                    prefixZeros += "0";
                }
            }
        }

        return prefixZeros + heisigId;
    }

    /**
     * Eventually should observe the state of "filter on" checkboxes
     * Only filter on the selected data
     */
    public void filter(String filterText) {
        filterText = filterText.toLowerCase(Locale.getDefault());
        filteredSet.clear();

        if (filterText.length() != 0) {
            filterOnId(filterText);
            filterOnKanji(filterText);
            filterOnKeyword(filterText);
        }

        updateFilteredList();
        notifyDataSetChanged();
    }

    private void updateFilteredList() {
        filteredList.clear();

        if (filteredSet.size() == 0) {
            filteredList.addAll(masterList);
        } else {
            filteredList.addAll(HeisigKanji.getObjects(new ArrayList<>(filteredSet), masterList));
        }
    }

    /**
     * Iterate over masterList
     */
    private void filterOnId(String filterText) {
        for (HeisigKanji kanji : masterList) {
            String id = String.valueOf(kanji.getId());

            if (id.contains(filterText)) {
                filteredSet.add(kanji.getId() - 1);
            }
        }
    }

    /**
     * Iterate over keywordList
     */
    private void filterOnKeyword(String filterText) {
        for (Keyword keyword : keywordList) {
            if (keyword.getKeywordText().toLowerCase(Locale.getDefault()).contains(filterText)) {
                filteredSet.add(keyword.getHeisigId() - 1);
            }
        }
    }

    /**
     * Iterate over masterList
     */
    private void filterOnKanji(String filterText) {
        for (HeisigKanji kanji : masterList) {
            String kanjiChar = kanji.getKanji();

            if (kanjiChar.equals(filterText)) {
                filteredSet.add(kanji.getId() - 1);
            }
        }
    }
}
