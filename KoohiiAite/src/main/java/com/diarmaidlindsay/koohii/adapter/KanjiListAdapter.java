package com.diarmaidlindsay.koohii.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.database.dao.HeisigKanjiDataSource;
import com.diarmaidlindsay.koohii.database.dao.HeisigToPrimitiveDataSource;
import com.diarmaidlindsay.koohii.database.dao.KeywordDataSource;
import com.diarmaidlindsay.koohii.database.dao.PrimitiveDataSource;
import com.diarmaidlindsay.koohii.model.HeisigKanji;
import com.diarmaidlindsay.koohii.model.HeisigToPrimitive;
import com.diarmaidlindsay.koohii.model.Keyword;
import com.diarmaidlindsay.koohii.model.Primitive;

import java.util.*;

/**
 * Adapter for Main Kanji ListView
 */
public class KanjiListAdapter extends BaseAdapter {
    private ViewHolderItem viewHolder;
    private Context context;

    private List<HeisigKanji> masterList; //list of all HeisigKanjis
    private List<Keyword> keywordList; //list of all Keywords
    private List<Primitive> primitiveList; //list of all Primitives

    private Set<Integer> filteredHeisigKanjiSet = new HashSet<>(); //filtered heisig_ids
    private List<HeisigKanji> filteredHeisigKanjiList = new ArrayList<>(); //filtered HeisigKanjis for display
    private List<HeisigToPrimitive> filteredHeisigToPrimitiveList; //filtered list of Primitives for display

    private LayoutInflater layoutInflater;

    /**
     * http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
     */
    static class ViewHolderItem {
        TextView heisig;
        TextView kanji;
        TextView keyword;
        TextView primitives;
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
        PrimitiveDataSource primitiveDataSource = new PrimitiveDataSource(context);
        heisigKanjiDataSource.open();
        keywordDataSource.open();
        primitiveDataSource.open();
        masterList = heisigKanjiDataSource.getAllKanji();
        keywordList = keywordDataSource.getAllKeywords();
        primitiveList = primitiveDataSource.getAllPrimitives();
        heisigKanjiDataSource.close(); //Open again when needed
        keywordDataSource.close(); //Open again when needed
        primitiveDataSource.close(); //Open again when needed
    }

    @Override
    public int getCount() {
        return filteredHeisigKanjiList.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredHeisigKanjiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_kanji, null);
            viewHolder = new ViewHolderItem();
            viewHolder.heisig = (TextView) convertView.findViewById(R.id.heisig_id_list_item);
            viewHolder.kanji = (TextView) convertView.findViewById(R.id.kanji_list_item);
            viewHolder.keyword = (TextView) convertView.findViewById(R.id.keyword_list_item);
            viewHolder.primitives = (TextView) convertView.findViewById(R.id.primitives_list_item);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        HeisigKanji theKanji = (HeisigKanji) getItem(position);

        int heisigId = theKanji.getId();

        List<Integer> primitiveIds =
                HeisigToPrimitive.getPrimitiveIdsForHeisigId(filteredHeisigToPrimitiveList, heisigId);
        List<String> primitiveStrings =
                Primitive.getPrimitiveText(primitiveList, primitiveIds);
        StringBuilder primitiveText = new StringBuilder();

        for (int i = 0; i < primitiveStrings.size(); i++) {
            String primitive = primitiveStrings.get(i);
            primitiveText.append(primitive);
            //append comma except last
            if (i < primitiveStrings.size() - 1) {
                primitiveText.append(", ");
            }
        }

        viewHolder.heisig.setText(HeisigKanji.getHeisigIdAsString(heisigId));
        viewHolder.kanji.setText(theKanji.getKanji());
        viewHolder.keyword.setText(keywordList.get(heisigId - 1).getKeywordText());
        viewHolder.primitives.setText(primitiveText.toString());
        int bgColor = theKanji.isJoyo() ?
                Color.parseColor("#F5D76E") : Color.parseColor("#FFFFFF");

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

    /**
     * Eventually should observe the state of "filter on" checkboxes
     * Only filter on the selected data
     */
    public void filter(String filterText) {
        filterText = filterText.toLowerCase(Locale.getDefault());
        filteredHeisigKanjiSet.clear();

        if (filterText.length() != 0) {
            filterOnId(filterText);
            filterOnKanji(filterText);
            filterOnKeyword(filterText);
            filterOnPrimitives(filterText);
        }

        updateFilteredList();
        updatePrimitiveList();
        notifyDataSetChanged();
    }

    private void updateFilteredList() {
        filteredHeisigKanjiList.clear();

        if (filteredHeisigKanjiSet.size() == 0) {
            filteredHeisigKanjiList.addAll(masterList);

        } else {
            filteredHeisigKanjiList.addAll(HeisigKanji.getHeisigKanjiMatchingIds(new ArrayList<>(filteredHeisigKanjiSet), masterList));
        }
    }

    /**
     * Called every time there is a filter operation.
     * Is it better to just cache them all?
     */
    private void updatePrimitiveList() {
        HeisigToPrimitiveDataSource heisigToPrimitiveDataSource = new HeisigToPrimitiveDataSource(context);
        heisigToPrimitiveDataSource.open();
        String[] heisigIds = HeisigKanji.getIds1Indexed(filteredHeisigKanjiList);
        filteredHeisigToPrimitiveList =
                heisigToPrimitiveDataSource.getHeisigToPrimitiveMatching(heisigIds);
        heisigToPrimitiveDataSource.close();
    }

    /**
     * Iterate over masterList
     */
    private void filterOnId(String filterText) {
        for (HeisigKanji kanji : masterList) {
            String id = String.valueOf(kanji.getId());

            if (id.contains(filterText)) {
                filteredHeisigKanjiSet.add(kanji.getId());
            }
        }
    }

    /**
     * Iterate over keywordList
     */
    private void filterOnKeyword(String filterText) {
        for (Keyword keyword : keywordList) {
            if (keyword.getKeywordText().toLowerCase(Locale.getDefault()).contains(filterText)) {
                filteredHeisigKanjiSet.add(keyword.getHeisigId());
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
                filteredHeisigKanjiSet.add(kanji.getId());
            }
        }
    }

    private void filterOnPrimitives(String filterText)
    {
        if(filterText.length()>0)
        {
            List<Integer> primitiveIds =
                Primitive.getPrimitiveIdsContaining(filterText, primitiveList);

            HeisigToPrimitiveDataSource dataSource = new HeisigToPrimitiveDataSource(context);
            dataSource.open();
            List<Integer> heisigIds =
                    dataSource.getHeisigIdsMatching(primitiveIds.toArray(new Integer[primitiveIds.size()]));
            dataSource.close();

            filteredHeisigKanjiSet.addAll(heisigIds);
        }
    }
}
