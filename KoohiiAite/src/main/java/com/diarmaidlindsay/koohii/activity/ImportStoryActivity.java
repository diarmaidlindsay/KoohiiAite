package com.diarmaidlindsay.koohii.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.adapter.ImportStoryAdapter;
import com.diarmaidlindsay.koohii.interfaces.OnDatabaseOperationCompleted;
import com.diarmaidlindsay.koohii.utils.Utils;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;
import java.util.List;

/**
 * Import CSV and display result of import in a table, so the user can
 * confirm or cancel the changes.
 */
public class ImportStoryActivity extends AppCompatActivity implements OnDatabaseOperationCompleted {
    static final int ACTIVITY_CODE = 2;
    static final int FILE_CODE = 100;
    ListView listCSVContent; //the csv file's valid rows
    Button buttonChooseFile; //open file browser
    Button buttonConfirm; //write to database
    Button buttonCancel; //clear table, disable confirm button
    TextView importCount; //display amount of stories to be imported
    ImportStoryAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_import_story);

        buttonChooseFile = (Button) findViewById(R.id.button_choose_file);
        buttonConfirm = (Button) findViewById(R.id.button_confirm_import);
        buttonCancel = (Button) findViewById(R.id.button_cancel_import);
        listCSVContent = (ListView) findViewById(R.id.import_story_listView);
        importCount = (TextView) findViewById(R.id.story_import_count);
        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(v.getContext(), FileChooserActivity.class);
                startActivityForResult(i, FILE_CODE);
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetView();
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listAdapter.writeToDatabase();
            }
        });

        listAdapter = new ImportStoryAdapter(this, this);
        listCSVContent.setAdapter(listAdapter);

        buttonCancel.setEnabled(listAdapter.getCount() > 0);
        buttonConfirm.setEnabled(listAdapter.getCount() > 0);
    }

    private void resetView() {
        listAdapter.clearStories();
        buttonConfirm.setEnabled(false);
        buttonCancel.setEnabled(false);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = FileUtils.getPath(this, uri);

            if (path != null) {
                if (listAdapter.readCSVFile(new File(path))) {
                    if (listAdapter.getCount() > 0) {
                        listAdapter.notifyDataSetChanged();
                        buttonConfirm.setEnabled(true);
                        buttonCancel.setEnabled(true);
                        Toast.makeText(this, "CSV file import successful", Toast.LENGTH_SHORT).show();
                    }
                    importCount.setText(listAdapter.getCount() + " stories found for import.");
                } else {
                    Toast.makeText(this, "Failed to read CSV file", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    public void setResult(List<Integer> heisigIds) {
        Intent returnIntent = getIntent();
        returnIntent.putExtra("heisigIds", Utils.toIntArray(heisigIds));
        setResult(RESULT_OK, returnIntent);
    }

    @Override
    public void onImportCompleted(List<Integer> affectedIds) {
        if (affectedIds.size() > 0) {
            setResult(affectedIds);
            Toast.makeText(this, affectedIds.size() + " Stories and Keywords updated", Toast.LENGTH_SHORT).show();

        } else {
            Toast.makeText(this, "Failed to write to database", Toast.LENGTH_SHORT).show();
            setResult(RESULT_CANCELED);
        }
        resetView();
        finish();
    }
}
