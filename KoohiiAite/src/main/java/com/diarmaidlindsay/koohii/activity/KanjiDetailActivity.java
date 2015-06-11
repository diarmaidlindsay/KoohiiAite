package com.diarmaidlindsay.koohii.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.model.HeisigKanji;

public class KanjiDetailActivity extends AppCompatActivity {

    private int heisigId;
    private String kanji;
    private String keyword;

    private TextView textViewHeisigId;
    private TextView textViewKanji;
    private TextView textViewKeyword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_detail);
        Intent i = getIntent();
        heisigId = i.getIntExtra("heisigId", 0);
        kanji = i.getStringExtra("kanji");
        keyword = i.getStringExtra("keyword");

        textViewHeisigId = (TextView) findViewById(R.id.heisig_id_detail);
        textViewKanji = (TextView) findViewById(R.id.kanji_detail);
        textViewKeyword = (TextView) findViewById(R.id.keyword_detail);

        // Load the results into the TextViews
        textViewHeisigId.setText(HeisigKanji.getHeisigIdAsString(heisigId));
        textViewKanji.setText(kanji);
        textViewKeyword.setText(keyword);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_detail, menu);
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
