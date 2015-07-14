package com.diarmaidlindsay.koohii.activity;

import android.app.Activity;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.adapter.ImportStoryAdapter;

import java.io.File;

/**
 * Import CSV and display result of import in a table, so the user can
 * confirm or cancel the changes.
 */
public class ImportStoryActivity extends AppCompatActivity {
    ListView listCSVContent; //the csv file's valid rows
    Button buttonChooseFile; //open file browser
    Button buttonConfirm; //write to database
    Button buttonCancel; //clear table, disable confirm button
    TextView importCount; //display amount of stories to be imported

    ImportStoryAdapter listAdapter;

    final int FILE_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_import_story);

        buttonChooseFile = (Button) findViewById(R.id.button_choose_file);
        buttonConfirm = (Button) findViewById(R.id.button_confirm_import);
        buttonCancel = (Button) findViewById(R.id.button_cancel_import);
        listCSVContent = (ListView) findViewById(R.id.import_story_listView);
        importCount = (TextView) findViewById(R.id.story_import_count);

//        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(v.getContext(), FileChooserActivity.class);
//                i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false);
//                i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false);
//                i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE);
//                i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().getPath());
//
//                startActivityForResult(i, FILE_CODE);
//            }
//        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirm.setEnabled(false);
                buttonCancel.setEnabled(false);
            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buttonConfirm.setEnabled(false);
                buttonCancel.setEnabled(false);
            }
        });

        listAdapter = new ImportStoryAdapter(this);
        listCSVContent.setAdapter(listAdapter);

        buttonCancel.setEnabled(listAdapter.getCount() > 0);
        buttonConfirm.setEnabled(listAdapter.getCount() > 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
//            Uri uri = null;
//            if (data.getBooleanExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)) {
//                ClipData clip = data.getClipData();
//
//                if (clip != null) {
//                    for (int i = 0; i < clip.getItemCount(); i++) {
//                        uri = clip.getItemAt(i).getUri();
//                    }
//                }
//            } else {
//                uri = data.getData();
//            }
//            if(uri != null) {
//                if(listAdapter.readCSVFile(new File(uri.getPath()))) {
//                    if(listAdapter.getCount() > 0) {
//                        listAdapter.notifyDataSetChanged();
//                        buttonConfirm.setEnabled(true);
//                        buttonCancel.setEnabled(true);
//                        Toast.makeText(this, "CSV file import successful", Toast.LENGTH_SHORT).show();
//                    }
//                    importCount.setText(listAdapter.getCount()+" stories found for import.");
//                } else {
//                    Toast.makeText(this, "Failed to read CSV file", Toast.LENGTH_SHORT).show();
//                }
//            }
//        }
    }
}
