package com.diarmaidlindsay.koohii.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.adapter.KanjiListAdapter;
import com.diarmaidlindsay.koohii.adapter.SuggestionsAdapter;

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
    private SuggestionsAdapter suggestionAdapter;
    private MenuItem searchItem;
    private SearchView searchView;
    private TextView result;
    private ListView kanjiList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_list);

        kanjiList = (ListView) findViewById(R.id.kanjiListView);
        suggestionAdapter = getCursorAdapter();
        kanjiListAdapter = new KanjiListAdapter(this);
        kanjiList.setAdapter(kanjiListAdapter);
        result = (TextView) findViewById(R.id.result);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_list, menu);

        SearchManager searchManager = (SearchManager)
                getSystemService(Context.SEARCH_SERVICE);
        searchItem = menu.findItem(R.id.search);
        searchView = (SearchView) searchItem.getActionView();
        searchView.setSearchableInfo(searchManager.
                getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(getTextListener());
        searchView.setOnSuggestionListener(getSuggestionListener());
        searchView.setSuggestionsAdapter(suggestionAdapter);

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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showPrimitives() {
        startActivity(new Intent(this, PrimitiveListActivity.class));
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

            Runnable mFilterTask = new Runnable() {

                @Override
                public void run() {
                    kanjiListAdapter.filter(text);
                    result.setText(kanjiListAdapter.getCount() + " items displayed");
                }
            };

            private Handler mHandler = new Handler();
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
            int heisigId = data.getIntExtra("heisigId", -1);
            String keyword = data.getStringExtra("keyword");

            kanjiListAdapter.updateKeyword(heisigId, keyword);
            kanjiListAdapter.notifyDataSetChanged();
        }
    }

    public void hideKeyboard()
    {
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }
}
