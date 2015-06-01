package com.diarmaidlindsay.koohii.activity;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.adapter.KanjiListAdapter;
import com.diarmaidlindsay.koohii.database.dao.KanjiDataSource;

import java.sql.SQLException;
import java.util.Locale;


public class KanjiListActivity extends AppCompatActivity {

    KanjiDataSource dataSource;
    KanjiListAdapter adapter;
    EditText editsearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_list);

        ListView kanjiList = (ListView) findViewById(R.id.kanjiListView);

        try {
            dataSource = new KanjiDataSource(this);
            dataSource.open();
            adapter = new KanjiListAdapter(dataSource.getAllKanji(), this);
            kanjiList.setAdapter(adapter);
            dataSource.close(); //Open again when needed

            editsearch = (EditText) findViewById(R.id.search);
            editsearch.addTextChangedListener(getTextWatcher());
        } catch (SQLException e) {
            Log.e("KanjiListActivity", "Couldn't open database");
            //TODO : Inform the user
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_list, menu);
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

    private TextWatcher getTextWatcher()
    {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = editsearch.getText().toString();
                adapter.filter(text);
            }
        };
    }
}
