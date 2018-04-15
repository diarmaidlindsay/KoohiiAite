package tech.diarmaid.koohiiaite.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tech.diarmaid.koohiiaite.adapter.ImportStoryAdapter;
import tech.diarmaid.koohiiaite.interfaces.OnCSVParseCompleted;
import tech.diarmaid.koohiiaite.model.CSVEntry;
import tech.diarmaid.koohiiaite.utils.CSVLineReader;
import tech.diarmaid.koohiiaite.utils.Utils;

/**
 * Populate adapter table with the given my_stories.csv
 */
public class ReadCSVTask extends AsyncTask<File, Void, List<CSVEntry>> {
    private ProgressDialog progress;
    private final WeakReference<Context> context;
    private OnCSVParseCompleted csvListener;
    private ImportStoryAdapter importStoryAdapter; //TODO : Decouple this class and instead pass the method itself, functional programming style

    public ReadCSVTask(Context context, OnCSVParseCompleted csvListener, ImportStoryAdapter importStoryAdapter) {
        this.context = new WeakReference<>(context);
        this.csvListener = csvListener;
        this.importStoryAdapter = importStoryAdapter;
    }

    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(context.get(), "Reading CSV",
                "Please wait while parsing CSV", true);
    }

    @Override
    protected List<CSVEntry> doInBackground(File... params) {
        List<CSVEntry> entries = new ArrayList<>();

        if (params.length != 1) {
            return entries;
        }
        BufferedReader br = null;
        CSVLineReader lr;
        String line;
        String csvSplitBy = ",";
        importStoryAdapter.clearStories();
        try {
            br = new BufferedReader(new FileReader(params[0]));
            lr = new CSVLineReader(br);

            while ((line = lr.readLine()) != null) {
                String[] row = line.split(csvSplitBy, 6);
                //first row is the column headers, we should ignore
                if (row.length == 6 && Utils.isNumeric(row[0])) {
                    entries.add(new CSVEntry(row[0], row[1], row[2], row[3], row[4], row[5]));
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    //noinspection ReturnInsideFinallyBlock
                }
            }
        }
        return entries;
    }

    @Override
    protected void onPostExecute(List<CSVEntry> parsedEntries) {
        if (progress.isShowing()) {
            progress.dismiss();
        }
        csvListener.onParsingCompleted(parsedEntries);
    }
}
