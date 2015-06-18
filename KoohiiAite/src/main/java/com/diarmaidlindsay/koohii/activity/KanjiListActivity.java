package com.diarmaidlindsay.koohii.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.ListView;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.adapter.KanjiListAdapter;

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

    private KanjiListAdapter adapter;
    private MenuItem searchItem;
    private SearchView searchView;
    private TextView result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_list);

        ListView kanjiList = (ListView) findViewById(R.id.kanjiListView);

        adapter = new KanjiListAdapter(this);
        kanjiList.setAdapter(adapter);
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

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private SearchView.OnQueryTextListener getTextListener()
    {
        return new SearchView.OnQueryTextListener() {
            private String text;

            @Override
            public boolean onQueryTextSubmit(String query) {
                //don't do anything when search submitted, it's handled by text changed event
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                text = newText;
                mHandler.removeCallbacks(mFilterTask);
                //Delay after user input to smooth the user experience
                mHandler.postDelayed(mFilterTask, 1000);
                return true;
            }

            Runnable mFilterTask = new Runnable() {

                @Override
                public void run() {
                    adapter.filter(text);
                    result.setText(adapter.getCount() + " items displayed");
                }
            };

            private Handler mHandler = new Handler();
        };
    }

    /**
     * Hide soft keyboard then collapse search view.
     */
    public void collapseSearchView()
    {
        //if you don't hide soft keyboard, you get a blank space where the keyboard was momentarily when returning to list view
        InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputManager.hideSoftInputFromWindow(searchView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        searchItem.collapseActionView();
    }
}
