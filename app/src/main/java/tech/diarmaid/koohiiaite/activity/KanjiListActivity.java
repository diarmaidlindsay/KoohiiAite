package tech.diarmaid.koohiiaite.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Locale;

import tech.diarmaid.koohiiaite.R;
import tech.diarmaid.koohiiaite.adapter.KanjiListAdapter;
import tech.diarmaid.koohiiaite.adapter.KanjiListFilterAdapter;
import tech.diarmaid.koohiiaite.adapter.SuggestionsAdapter;
import tech.diarmaid.koohiiaite.widget.KanjiSearchView;
import tech.diarmaid.koohiiaite.widget.OnSpinnerEventsListener;
import tech.diarmaid.koohiiaite.widget.SpinnerFilter;

/**
 *  Koohii Aite uses Heisig Old Edition
 *  Volume 1 : 5th edition or earlier.
    Volume 3 : 1st or 2nd edition.

    This is because I use rikaikun Chrome plugin, which gives me old edition Heisig indexes.
    At some point I should update the application to handle new edition indexes as well.
    And update rikaikun's kanji.dat as well.

    https://docs.google.com/spreadsheets/d/1Z0BUSie8wh0JqlUejezs3EqauJuF-zKEomOQnqm9J08/edit#gid=0
 *
 *  Entry point into the application.
 */
public class KanjiListActivity extends AppCompatActivity {

    private KanjiListAdapter kanjiListAdapter;
    private KanjiListFilterAdapter kanjiListFilterAdapter;
    private SuggestionsAdapter suggestionAdapter;
    private KanjiSearchView searchView;
    private SpinnerFilter spinnerFilter;
    private OnSpinnerEventsListener spinnerListener;
    private TextView result;
    private TextView joyoFilterState;
    private TextView keywordFilterState;
    private TextView storyFilterState;
    private ListView kanjiList;

    private Bundle savedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if onCreate was called after rotation, we need saved bundle for options menu
        this.savedInstanceState = savedInstanceState;

        setContentView(R.layout.activity_kanji_list);

        kanjiList = findViewById(R.id.kanjiListView);
        suggestionAdapter = getCursorAdapter();
        String[] spinnerValues = {"n/a, Yes, No"};
        kanjiListFilterAdapter = new KanjiListFilterAdapter(this, R.id.filter_spinner, spinnerValues);
        kanjiListAdapter = new KanjiListAdapter(this, savedInstanceState);
        kanjiList.setAdapter(kanjiListAdapter);
        result = findViewById(R.id.result);
        joyoFilterState = findViewById(R.id.joyo_filter_state);
        keywordFilterState = findViewById(R.id.keyword_filter_state);
        storyFilterState = findViewById(R.id.story_filter_state);
        //created here because must be re-created if list activity is destroyed
        spinnerListener = new OnSpinnerEventsListener() {
            //if filter values changed, we should perform a search with new values
            boolean changed;

            Runnable mFilterTask = new Runnable() {

                @Override
                public void run() {
                    kanjiListAdapter.search(searchView.getQuery().toString());
                    result.setText(String.format(Locale.ENGLISH, "%d items displayed", kanjiListAdapter.getCount()));
                }
            };
            private Handler mHandler = new Handler();

            @Override
            public void notifyContentsChange() {
                changed = true;
            }

            @Override
            public void onSpinnerOpened() {
                changed = false;
            }

            @Override
            public void onSpinnerClosed() {
                if (changed) {
                    mHandler.postDelayed(mFilterTask, 0);
                }
            }
        };
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        // mSpin is our custom Spinner
        if (spinnerFilter != null && spinnerFilter.hasBeenOpened() && hasFocus) {
            spinnerFilter.performClosedEvent();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_list, menu);

        MenuItem filterItem = menu.findItem(R.id.filter_spinner);
        spinnerFilter = (SpinnerFilter) filterItem.getActionView();
        spinnerFilter.setAdapter(kanjiListFilterAdapter);
        spinnerFilter.setSpinnerEventsListener(spinnerListener);

        // we want to be able to filter the search results!
        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        MenuItem searchItem = menu.findItem(R.id.kanji_list_search);
        searchView = (KanjiSearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(getTextListener());
        searchView.setOnSuggestionListener(getSuggestionListener());
        searchView.setSuggestionsAdapter(suggestionAdapter);

        //if device rotated, restore values here!
        if(savedInstanceState != null) {
            kanjiListFilterAdapter.setJoyoFilter(savedInstanceState.getInt("joyoFilter", 0));
            kanjiListFilterAdapter.setKeywordFilter(savedInstanceState.getInt("keywordFilter", 0));
            kanjiListFilterAdapter.setStoryFilter(savedInstanceState.getInt("storyFilter", 0));
            String query = savedInstanceState.getString("searchQuery");
            //trigger a search with the onTextChanged Listener
            searchView.setQuery(query, true);
        }

        notifyFilterChanged();

        return super.onCreateOptionsMenu(menu);
    }

    private SuggestionsAdapter getCursorAdapter()
    {
        final String[] from = new String[] {"keywordPrimitive"};
        final int[] to = new int[] {R.id.suggestion_item};

        return new SuggestionsAdapter(this,
                R.layout.list_item_suggestion,
                null,
                from,
                to,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id)
        {
            case R.id.action_settings :
                return true;
            case R.id.action_primitives :
                showPrimitives();
                return true;
            case R.id.action_import_story :
                importStory();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPrimitives() {
        startActivity(new Intent(this, PrimitiveListActivity.class));
    }

    private void importStory() {
        startActivityForResult(new Intent(this, ImportStoryActivity.class), 2);
    }

    /**
     * If there's a comma, we're only interested in last part after comma
     * for suggestions
     */
    private String getLastPart(String query)
    {
        if(query.lastIndexOf(",") == -1)
        {
            return query;
        }
        else if(query.endsWith(","))
        {
            return "";
        }

        String[] parts = query.split(",");

        return parts[parts.length-1];
    }

    private SearchView.OnQueryTextListener getTextListener()
    {
        return new SearchView.OnQueryTextListener() {
            private String text;
            Runnable mFilterTask = new Runnable() {

                @Override
                public void run() {
                    kanjiListAdapter.search(text);
                    result.setText(String.format(Locale.ENGLISH,"%d items displayed", kanjiListAdapter.getCount()));
                }
            };
            private Handler mHandler = new Handler();

            @Override
            public boolean onQueryTextSubmit(String query) {
                text = query;
                mHandler.removeCallbacks(mFilterTask);
                mHandler.postDelayed(mFilterTask, 0);
                hideKeyboard();
                kanjiList.requestFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                text = newText;
                suggestionAdapter.populateSuggestions(getLastPart(text));
                mHandler.removeCallbacks(mFilterTask);
                //Delay after user input to smooth the user experience
                mHandler.postDelayed(mFilterTask, 1000);
                return true;
            }
        };
    }

    private SearchView.OnSuggestionListener getSuggestionListener()
    {
        return new SearchView.OnSuggestionListener() {
            @Override
            public boolean onSuggestionSelect(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String choice = cursor.getString(1);
                //if there's a comma in the search query, it means multiple terms
                //in that case we don't want to replace the whole query
                String query = searchView.getQuery().toString();
                query = query.replace(getLastPart(query), choice);
                searchView.setQuery(query, false);
                return true;
            }

            @Override
            public boolean onSuggestionClick(int position) {
                Cursor cursor = (Cursor) searchView.getSuggestionsAdapter().getItem(position);
                String choice = cursor.getString(1);
                String query = searchView.getQuery().toString();
                query = query.replace(getLastPart(query), choice);
                searchView.setQuery(query, false);
                return true;
            }
        };
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK)
        {
            if (requestCode == KanjiDetailActivity.ACTIVITY_CODE) { //Kanji Detail Activity
                int[] heisigIds = data.getIntArrayExtra("heisigIds");
                String[] keywords = data.getStringArrayExtra("keywords");

                //user didn't modify anything
                if(heisigIds == null || keywords == null)
                {
                    return;
                } else if(heisigIds.length != keywords.length)
                {
                    Log.e("KanjiListActivity", "Array of Keyword ids and texts aren't same size!");
                    return;
                }

                //user modified kanji(s) in the detail view, so update indicators
                for(int i = 0; i < heisigIds.length; i++) {
                    kanjiListAdapter.updateKeyword(heisigIds[i], keywords[i]);
                    kanjiListAdapter.updateIndicatorVisibilityWithId(heisigIds[i]); //change to 0-indexed
                }

                kanjiListAdapter.notifyDataSetChanged();
                //if we return from detail view, and something has changed, resubmit the search query by triggering the filter's onClosed listener
                spinnerListener.onSpinnerClosed();
            } else if (requestCode == ImportStoryActivity.ACTIVITY_CODE) { //Import Stories Activity
                int[] heisigIds = data.getIntArrayExtra("heisigIds");
                kanjiListAdapter.initialiseUserKeywordsAndStories();

                for (int heisigId : heisigIds) {
                    kanjiListAdapter.updateIndicatorVisibilityWithId(heisigId);
                }

                kanjiListAdapter.notifyDataSetChanged();
                spinnerListener.onSpinnerClosed(); //trigger list view refresh
            }
        }
    }

    public void hideKeyboard()
    {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Allow list adapter to observe the joyo filter state
     */
    public KanjiListFilterAdapter.FilterState getJoyoFilter() {
        return kanjiListFilterAdapter.getJoyoFilter();
    }

    /**
     * Allow list adapter to observe the keyword filter state
     */
    public KanjiListFilterAdapter.FilterState getKeywordFilter() {
        return kanjiListFilterAdapter.getKeywordFilter();
    }

    /**
     * Allow list adapter to observe the story filter state
     */
    public KanjiListFilterAdapter.FilterState getStoryFilter() {
        return kanjiListFilterAdapter.getStoryFilter();
    }

    public void notifyFilterChanged() {
        spinnerListener.notifyContentsChange();
        updateFilterIndicators();
    }

    /**
     * The textual indicators above the kanji list, describing the state of the filter toggles
     */
    private void updateFilterIndicators() {
        final String joyoFilter = getString(R.string.filter_state_joyo);
        final String keywordFilter = getString(R.string.filter_state_keyword);
        final String storyFilter = getString(R.string.filter_state_story);

        final String UNSET = "  ";
        final String YES = "O";
        final String NO = "X";

        switch (getJoyoFilter()) {
            case UNSET:
                joyoFilterState.setText(String.format(joyoFilter, UNSET));
                break;
            case YES:
                joyoFilterState.setText(String.format(joyoFilter, YES));
                break;
            case NO:
                joyoFilterState.setText(String.format(joyoFilter, NO));
                break;
        }

        switch (getKeywordFilter()) {
            case UNSET:
                keywordFilterState.setText(String.format(keywordFilter, UNSET));
                break;
            case YES:
                keywordFilterState.setText(String.format(keywordFilter, YES));
                break;
            case NO:
                keywordFilterState.setText(String.format(keywordFilter, NO));
                break;
        }

        switch (getStoryFilter()) {
            case UNSET:
                storyFilterState.setText(String.format(storyFilter, UNSET));
                break;
            case YES:
                storyFilterState.setText(String.format(storyFilter, YES));
                break;
            case NO:
                storyFilterState.setText(String.format(storyFilter, NO));
                break;
        }
    }

    /**
     * When rotation changes, we need to save...
     * Search query
     * Filters
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("searchQuery", searchView.getQuery().toString());
        outState.putInt("joyoFilter", kanjiListFilterAdapter.getJoyoFilter().getStateNum());
        outState.putInt("keywordFilter", kanjiListFilterAdapter.getKeywordFilter().getStateNum());
        outState.putInt("storyFilter", kanjiListFilterAdapter.getStoryFilter().getStateNum());
        super.onSaveInstanceState(outState);
    }
}