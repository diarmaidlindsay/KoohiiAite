package com.diarmaidlindsay.koohii.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.GridView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.adapter.PrimitiveGridAdapter;

/**
 * View and rename heisig primitives in a grid view
 */
public class PrimitiveListActivity extends AppCompatActivity {
    GridView gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_primitives_list);

        gridView = (GridView) findViewById(R.id.primitive_grid);
        gridView.setAdapter(new PrimitiveGridAdapter(this));
    }
}
