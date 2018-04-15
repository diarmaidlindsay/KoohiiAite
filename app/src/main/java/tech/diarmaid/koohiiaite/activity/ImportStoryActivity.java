package tech.diarmaid.koohiiaite.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;

import java.io.File;
import java.util.List;
import java.util.Locale;

import tech.diarmaid.koohiiaite.R;
import tech.diarmaid.koohiiaite.adapter.ImportStoryAdapter;
import tech.diarmaid.koohiiaite.interfaces.OnCSVParseCompleted;
import tech.diarmaid.koohiiaite.interfaces.OnDatabaseOperationCompleted;
import tech.diarmaid.koohiiaite.model.CSVEntry;
import tech.diarmaid.koohiiaite.task.ReadCSVTask;
import tech.diarmaid.koohiiaite.utils.ToastUtil;
import tech.diarmaid.koohiiaite.utils.Utils;

/**
 * Import CSV and display result of import in a table, so the user can
 * confirm or cancel the changes.
 */
public class ImportStoryActivity extends AppCompatActivity implements OnDatabaseOperationCompleted, OnCSVParseCompleted {
    static final int ACTIVITY_CODE = 2;
    static final int FILE_CODE = 100;
    //arbitrary int code for storage permission request
    static final int REQUEST_READ_STORAGE = 77;

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

        buttonChooseFile = findViewById(R.id.button_choose_file);
        buttonConfirm = findViewById(R.id.button_confirm_import);
        buttonCancel = findViewById(R.id.button_cancel_import);
        listCSVContent = findViewById(R.id.import_story_listView);
        importCount = findViewById(R.id.story_import_count);
        buttonChooseFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermission();
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

    private void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_READ_STORAGE);

            // The callback method gets the result of the request.
        } else {
            openFileChooser();
        }
    }

    private void openFileChooser() {
        Intent i = new Intent(this, FileChooserActivity.class);
        startActivityForResult(i, FILE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_STORAGE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // http://stackoverflow.com/a/32473449/4653788
                    // Need to restart the process to grant write to SD permissions
                    // Schedule start after 1 second
                    PendingIntent pi = PendingIntent.getActivity(
                            this,
                            0,
                            getIntent(),
                            PendingIntent.FLAG_CANCEL_CURRENT);
                    AlarmManager am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                    am.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pi);

                    // Stop now
                    System.exit(0);
                } else {
                    Toast.makeText(this,
                            "Please grant permission to read External Storage in order to Import CSV",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FILE_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String path = FileUtils.getPath(this, uri);

            if (path != null) {
                //execute task
                ReadCSVTask csvTask = new ReadCSVTask(this, this, listAdapter);
                csvTask.execute(new File(path)); //then go to onParsingCompleted
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
            ToastUtil.makeText(this, affectedIds.size() + " Stories and Keywords updated", Toast.LENGTH_SHORT).show();

        } else {
            ToastUtil.makeText(this, "Failed to write to database", Toast.LENGTH_LONG).show();
            setResult(RESULT_CANCELED);
        }
        resetView();
        finish();
    }

    @Override
    public void onParsingCompleted(List<CSVEntry> parsedEntries) {
        if (parsedEntries.size() > 0) {
            listAdapter.setStories(parsedEntries);
            listAdapter.notifyDataSetChanged();
            buttonConfirm.setEnabled(true);
            buttonCancel.setEnabled(true);
            ToastUtil.makeText(this, "CSV file import successful", Toast.LENGTH_SHORT).show();
            importCount.setText(String.format(Locale.ENGLISH, "%d stories found for import.", listAdapter.getCount()));
        } else {
            ToastUtil.makeText(this, "Failed to read CSV file", Toast.LENGTH_LONG).show();
        }
    }
}
