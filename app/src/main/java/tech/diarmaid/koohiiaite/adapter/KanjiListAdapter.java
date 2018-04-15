package tech.diarmaid.koohiiaite.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import tech.diarmaid.koohiiaite.R;
import tech.diarmaid.koohiiaite.activity.KanjiDetailActivity;
import tech.diarmaid.koohiiaite.activity.KanjiListActivity;
import tech.diarmaid.koohiiaite.database.dao.HeisigKanjiDataSource;
import tech.diarmaid.koohiiaite.database.dao.HeisigToPrimitiveDataSource;
import tech.diarmaid.koohiiaite.database.dao.KeywordDataSource;
import tech.diarmaid.koohiiaite.database.dao.PrimitiveDataSource;
import tech.diarmaid.koohiiaite.database.dao.StoryDataSource;
import tech.diarmaid.koohiiaite.database.dao.UserKeywordDataSource;
import tech.diarmaid.koohiiaite.model.HeisigKanji;
import tech.diarmaid.koohiiaite.model.HeisigToPrimitive;
import tech.diarmaid.koohiiaite.model.Keyword;
import tech.diarmaid.koohiiaite.model.Primitive;
import tech.diarmaid.koohiiaite.utils.ToastUtil;
import tech.diarmaid.koohiiaite.utils.Utils;

/**
 * Adapter for Main Kanji ListView
 */
public class KanjiListAdapter extends BaseAdapter {
    private ViewHolderItem viewHolder;
    private Context mContext;

    private List<HeisigKanji> masterList; //list of all HeisigKanjis
    private List<Keyword> keywordList; //list of all Keywords
    private SparseArray<String> userKeywordMap; //HashMap of User Keywords (id, text)
    private List<Primitive> primitiveList; //list of all Primitives
    //to save memory, only story booleans to indicate whether a list item has a story
    private List<Boolean> storyList;

    private Set<Integer> filteredHeisigKanjiSet = new HashSet<>(); //filtered heisig_ids
    private List<HeisigKanji> filteredHeisigKanjiList = new ArrayList<>(); //filtered HeisigKanjis for display
    private List<HeisigToPrimitive> filteredHeisigToPrimitiveList; //filtered list of Primitives for display

    private LayoutInflater layoutInflater;

    public KanjiListAdapter(Context context, Bundle savedInstanceState) {
        this.mContext = context;
        layoutInflater = LayoutInflater.from(context);
        initialiseDatasets();
        //only perform initial search if we're coming from fresh state
        if (savedInstanceState == null) {
            search("");
        }
    }

    private void initialiseDatasets() {
        KeywordDataSource keywordDataSource = new KeywordDataSource(mContext);
        HeisigKanjiDataSource heisigKanjiDataSource = new HeisigKanjiDataSource(mContext);
        PrimitiveDataSource primitiveDataSource = new PrimitiveDataSource(mContext);

        heisigKanjiDataSource.open();
        keywordDataSource.open();
        primitiveDataSource.open();

        masterList = heisigKanjiDataSource.getAllKanji();
        keywordList = keywordDataSource.getAllKeywords();
        primitiveList = primitiveDataSource.getAllPrimitives();

        heisigKanjiDataSource.close();
        keywordDataSource.close();
        primitiveDataSource.close();

        initialiseUserKeywordsAndStories();
    }

    /**
     * Called after a CSV import
     */
    public void initialiseUserKeywordsAndStories() {
        UserKeywordDataSource userKeywordDataSource = new UserKeywordDataSource(mContext);
        userKeywordDataSource.open();
        userKeywordMap = userKeywordDataSource.getAllUserKeywords();
        userKeywordDataSource.close();
        StoryDataSource storyDataSource = new StoryDataSource(mContext);
        storyDataSource.open();
        storyList = storyDataSource.getStoryFlags(masterList.size());
        storyDataSource.close();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_kanji, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.heisig = convertView.findViewById(R.id.heisig_id_list_item);
            viewHolder.kanji = convertView.findViewById(R.id.kanji_list_item);
            viewHolder.keyword = convertView.findViewById(R.id.keyword_list_item);
            viewHolder.primitives = convertView.findViewById(R.id.primitives_list_item);
            viewHolder.joyoIndicator = convertView.findViewById(R.id.list_indicator_joyo);
            viewHolder.storyIndicator = convertView.findViewById(R.id.list_indicator_story);
            viewHolder.keywordIndicator = convertView.findViewById(R.id.list_indicator_keyword);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        HeisigKanji theKanji = (HeisigKanji) getItem(position);

        final int heisigId = theKanji.getId();

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

        final String kanji = theKanji.getKanji();
        String userKeyword = userKeywordMap.get(heisigId);
        //if user entered their own keyword, use it, else use default keyword
        final String keyword = userKeyword == null ?
                keywordList.get(heisigId - 1).getKeywordText() //convert to 0-index
                : userKeyword;

        viewHolder.heisig.setText(HeisigKanji.getHeisigIdAsString(heisigId));
        viewHolder.kanji.setText(kanji);
        viewHolder.keyword.setText(keyword);
        viewHolder.primitives.setText(primitiveText.toString());
        // Listen for ListView Item Click
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                //collapse search box and hide keyboard
                if (mContext instanceof KanjiListActivity) {
                    ((KanjiListActivity) mContext).hideKeyboard();
                    Intent intent = new Intent(mContext, KanjiDetailActivity.class);
                    intent.putExtra("filteredListIndex", position);
                    //put all the filtered heisig ids for next/prev navigation
                    intent.putExtra("filteredIdList", HeisigKanji.getIds1Indexed(filteredHeisigKanjiList));
                    ((KanjiListActivity) mContext).startActivityForResult(intent, KanjiDetailActivity.ACTIVITY_CODE);
                } else {
                    ToastUtil.makeText(mContext, "Context was not KanjiListActivity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //populate the indicators, to indicate if kanji is joyo, has custom keyword or story written
        updateIndicatorVisibilityAtPos(position);

        return convertView;
    }

    /**
     * Search the Kanji list with the given search string
     */
    public void search(String searchString) {
        searchString = searchString.toLowerCase(Locale.getDefault());
        filteredHeisigKanjiSet.clear();

        if (searchString.length() > 0) {
            if (Utils.isNumeric(searchString)) {
                filterOnId(searchString);
            } else if (Utils.isKanji(searchString.charAt(0))) {
                filterOnKanji(searchString);
            } else {
                filterOnKeyword(searchString);
                filterOnUserKeyword(searchString);
                filterOnPrimitives(searchString);
            }
        }

        updateFilteredList(searchString);
        applyFilters();
        updatePrimitiveList();
        notifyDataSetChanged();
    }

    private void updateFilteredList(String filterText) {
        filteredHeisigKanjiList.clear();

        if (filterText.length() == 0) {
            //if nothing is entered in search, display all
            filteredHeisigKanjiList.addAll(masterList);

        } else {
            filteredHeisigKanjiList.addAll(HeisigKanji.getHeisigKanjiMatchingIds(
                    new ArrayList<>(filteredHeisigKanjiSet), masterList));
        }
    }

    /**
     * Called every time there is a filter operation.
     */
    private void updatePrimitiveList() {
        if (filteredHeisigKanjiList.size() > 0) {
            HeisigToPrimitiveDataSource heisigToPrimitiveDataSource = new HeisigToPrimitiveDataSource(mContext);
            heisigToPrimitiveDataSource.open();
            String[] heisigIds = HeisigKanji.getIds1Indexed(filteredHeisigKanjiList);
            filteredHeisigToPrimitiveList =
                    heisigToPrimitiveDataSource.getHeisigToPrimitiveMatching(heisigIds);
            heisigToPrimitiveDataSource.close();
        }
    }

    /**
     * Iterate over masterList
     */
    private void filterOnId(String filterText) {
        //only run if string is number
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
     * Iterate over user keywords
     */
    private void filterOnUserKeyword(String filterText) {
        int mapSize = userKeywordMap.size();
        for (int i = 0; i < mapSize; i++) {
            int kanjiIndex = userKeywordMap.keyAt(i);
            String userKeyword = userKeywordMap.get(kanjiIndex);
            if (userKeyword.toLowerCase(Locale.getDefault()).contains(filterText)) {
                filteredHeisigKanjiSet.add(kanjiIndex);
            }
        }
    }

    /**
     * Iterate over masterList
     */
    private void filterOnKanji(String filterText) {
        for(char kanjiCharacterFilter : filterText.toCharArray()) {
            for (HeisigKanji kanji : masterList) {
                String kanjiChar = kanji.getKanji();

                if (kanjiChar.equals(String.valueOf(kanjiCharacterFilter))) {
                    filteredHeisigKanjiSet.add(kanji.getId());
                    break;
                }
            }
        }
    }

    /**
     * If no comma, fuzzy search on 1 primitive string.
     * If comma, exact match on 1 or more primitive strings.
     * Can't do multiple fuzzy matches because would return too many results and difficult
     * to collate effectively and remove duplicates without intersection method.
     * Maybe fancy SQL could solve this?
     */
    private void filterOnPrimitives(String filterText) {
        if (filterText.length() > 0) {
            HeisigToPrimitiveDataSource dataSource = new HeisigToPrimitiveDataSource(mContext);
            List<Integer> heisigIds;

            //ie sun,moon
            if (filterText.contains(",")) {
                //if comma seperated primitive list, we're searching for many exact matches
                String[] primitiveSearchStrings = filterText.split(",");
                List<Integer> primitiveMatches = new ArrayList<>();
                //find primitive id which exactly matches string input
                for (String primitiveString : primitiveSearchStrings) {
                    primitiveString = primitiveString.trim();
                    int primitiveIdMatched =
                            Primitive.getPrimitiveIdWhichMatches(primitiveString, primitiveList, true);
                    if (primitiveIdMatched == -1) {
                        //no exact match found for primitive string
                        return;
                    }
                    primitiveMatches.add(primitiveIdMatched);
                }

                List<List<Integer>> heisigIdsForPrimitiveIds = new ArrayList<>();

                //we've found the primitive ids matching the strings the user entered
                //which kanjis contain all these primitives?
                dataSource.open();

                for (Integer match : primitiveMatches) {
                    heisigIdsForPrimitiveIds.add(
                            dataSource.getHeisigIdsMatching(new Integer[]{match}));
                }
                dataSource.close();
                //only retain the intersection of these matches, kanji which contain ALL these primitives
                heisigIds = intersection(heisigIdsForPrimitiveIds);
            } else {
                //if no comma, assume we're fuzzy searching for 1 primitive
                //ie night -> night, nightbreak
                List<Integer> primitiveIds =
                        Primitive.getPrimitiveIdsContaining(filterText, primitiveList, true);
                dataSource.open();
                heisigIds =
                        dataSource.getHeisigIdsMatching(primitiveIds.toArray(new Integer[primitiveIds.size()]));
                dataSource.close();
            }

            if (heisigIds.size() == 0) {
                return;
            }

            filteredHeisigKanjiSet.addAll(heisigIds);
        }
    }

    /**
     * Apply the filter selections from the dropdown in the title bar.
     * If all 3 filters match, the kanji should be displayed, else it should be
     * removed from the filteredHeisigKanjiList.
     */
    private void applyFilters() {
        if (mContext instanceof KanjiListActivity) {
            KanjiListFilterAdapter.FilterState joyoFilter =
                    ((KanjiListActivity) mContext).getJoyoFilter();
            KanjiListFilterAdapter.FilterState storyFilter =
                    ((KanjiListActivity) mContext).getStoryFilter();
            KanjiListFilterAdapter.FilterState keywordFilter =
                    ((KanjiListActivity) mContext).getKeywordFilter();

            List<HeisigKanji> filteredOutKanji = new ArrayList<>();

            for (HeisigKanji heisigKanji : filteredHeisigKanjiList) {

                boolean joyoMatch = isJoyoMatch(joyoFilter, heisigKanji);
                boolean storyMatch = isStoryMatch(storyFilter, heisigKanji);
                boolean keywordMatch = isKeywordMatch(keywordFilter, heisigKanji);

                if (!joyoMatch || !storyMatch || !keywordMatch) {
                    filteredOutKanji.add(heisigKanji);
                }
            }

            filteredHeisigKanjiList.removeAll(filteredOutKanji);
        }
    }

    /**
     * Does the heisig kanji match the user's JOYO filter choice?
     * Return true if it matches, else return false, meaning it should be filtered out.
     */
    private boolean isJoyoMatch(KanjiListFilterAdapter.FilterState state, HeisigKanji heisigKanji) {
        //If Kanji IS Joyo but user specified NOT joyo, or kanji NOT joyo but user specified IS joyo, it should be removed
        return !((heisigKanji.isJoyo() && state == KanjiListFilterAdapter.FilterState.NO) ||
                !heisigKanji.isJoyo() && state == KanjiListFilterAdapter.FilterState.YES);
    }

    /**
     * Does the heisig kanji match the user's KEYWORD filter choice?
     * Return true if it matches, else return false, meaning it should be filtered out.
     */
    private boolean isKeywordMatch(KanjiListFilterAdapter.FilterState state, HeisigKanji heisigKanji) {
        final int heisigId = heisigKanji.getId();

        return !((userKeywordMap.get(heisigId) != null && state == KanjiListFilterAdapter.FilterState.NO) ||
                userKeywordMap.get(heisigId) == null && state == KanjiListFilterAdapter.FilterState.YES);
    }

    /**
     * Does the heisig kanji match the user's STORY filter choice?
     * Return true if it matches, else return false, meaning it should be filtered out.
     */
    private boolean isStoryMatch(KanjiListFilterAdapter.FilterState state, HeisigKanji heisigKanji) {
        final int heisigId = heisigKanji.getId();

        return !((storyList.get(heisigId - 1) && state == KanjiListFilterAdapter.FilterState.NO) ||
                !storyList.get(heisigId - 1) && state == KanjiListFilterAdapter.FilterState.YES);
    }

    private List<Integer> intersection(List<List<Integer>> matches) {
        List<Integer> intersection = new ArrayList<>();
        if (matches.size() > 0) {
            intersection.addAll(matches.get(0));

            for (List<Integer> list : matches) {
                intersection.retainAll(list);
            }
        }

        return intersection;
    }

    public void updateKeyword(int heisigId, String keywordText) {
        //if the "updated" keyword is equal to the orignal, assume a revert to default and remove from user map
        if (keywordList.get(heisigId - 1).getKeywordText().equals(keywordText)) {
            userKeywordMap.remove(heisigId);
        } else {
            userKeywordMap.put(heisigId, keywordText);
        }
    }

    /**
     * If we have the position in the filtered array, we can use this method
     * to update the indicators for
     * JOYO, KEYWORD and STORY for the given position in the list adaptor
     */
    private void updateIndicatorVisibilityAtPos(int position) {
        HeisigKanji theKanji = (HeisigKanji) getItem(position);
        updateIndicatorVisibilityWithId(theKanji.getId());
    }

    /**
     * Update the indicator based on a given heisig id, after these kanji
     * were modified in detail view
     */
    public void updateIndicatorVisibilityWithId(int heisigId) {
        if (viewHolder != null) {
            HeisigKanji theKanji = masterList.get(heisigId - 1); //convert to 0-indexed

            if (theKanji.isJoyo()) {
                viewHolder.joyoIndicator.setVisibility(View.VISIBLE);
            } else {
                viewHolder.joyoIndicator.setVisibility(View.INVISIBLE);
            }

            if (userKeywordMap.get(heisigId) != null) {
                viewHolder.keywordIndicator.setVisibility(View.VISIBLE);
            } else {
                viewHolder.keywordIndicator.setVisibility(View.INVISIBLE);
            }

            if (storyList.get(heisigId - 1)) {
                viewHolder.storyIndicator.setVisibility(View.VISIBLE);
            } else {
                viewHolder.storyIndicator.setVisibility(View.INVISIBLE);
            }
        }
    }

    /**
     * This prevents wasted cpu cycles by only inflating the view once
     * http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
     */
    private static class ViewHolderItem {
        TextView heisig;
        TextView kanji;
        TextView keyword;
        TextView primitives;
        TextView joyoIndicator;
        TextView storyIndicator;
        TextView keywordIndicator;
    }
}
