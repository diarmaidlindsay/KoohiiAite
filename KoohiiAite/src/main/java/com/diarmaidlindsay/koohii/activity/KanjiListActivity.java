package com.diarmaidlindsay.koohii.activity;

import android.app.SearchManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SearchView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.adapter.KanjiListAdapter;
import com.diarmaidlindsay.koohii.database.dao.KanjiDataSource;

import java.sql.SQLException;


public class KanjiListActivity extends AppCompatActivity {

    KanjiDataSource dataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_list);

        ListView kanjiList = (ListView)findViewById(R.id.kanjiListView);

        dataSource = new KanjiDataSource(this);
        try {
            dataSource.open();
        } catch (SQLException e) {
            Log.e("KanjiListActivity", "Couldn't open database");
        }

        kanjiList.setAdapter(new KanjiListAdapter(dataSource.getAllKanji(), this));

        dataSource.close(); //Open again when needed
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_list, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        //TODO : Create a searchable activity
        // http://developer.android.com/training/search/setup.html
        return true;
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
}
