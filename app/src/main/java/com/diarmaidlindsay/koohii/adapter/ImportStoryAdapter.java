package com.diarmaidlindsay.koohii.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.database.dao.KeywordDataSource;
import com.diarmaidlindsay.koohii.database.dao.StoryDataSource;
import com.diarmaidlindsay.koohii.database.dao.UserKeywordDataSource;
import com.diarmaidlindsay.koohii.interfaces.OnCSVParseCompleted;
import com.diarmaidlindsay.koohii.interfaces.OnDatabaseOperationCompleted;
import com.diarmaidlindsay.koohii.model.CSVEntry;
import com.diarmaidlindsay.koohii.model.Keyword;
import com.diarmaidlindsay.koohii.model.Story;
import com.diarmaidlindsay.koohii.utils.Utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Adapter for the List View in the Activity which displays importedStories CSV
 */
public class ImportStoryAdapter extends BaseAdapter {
    private OnDatabaseOperationCompleted databaseListener;
    private OnCSVParseCompleted csvListener;

    private final String TAG = ImportStoryAdapter.class.getName();

    private List<CSVEntry> importedStories = new ArrayList<>();
    private ViewHolderItem viewHolder;
    private Context mContext;
    private LayoutInflater layoutInflater;

    public ImportStoryAdapter(Context context, OnDatabaseOperationCompleted databaseListener, OnCSVParseCompleted csvListener) {
        this.mContext = context;
        this.databaseListener = databaseListener;
        this.csvListener = csvListener;
        layoutInflater = LayoutInflater.from(context);
    }

    public void setStories(List<CSVEntry> stories) {
        importedStories.addAll(stories);
    }

    @Override
    public int getCount() {
        return importedStories.size();
    }

    @Override
    public Object getItem(int position) {
        return importedStories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_import_story, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.id = (TextView) convertView.findViewById(R.id.csv_id);
            viewHolder.kanji = (TextView) convertView.findViewById(R.id.csv_kanji);
            viewHolder.keyword = (TextView) convertView.findViewById(R.id.csv_keyword);
            viewHolder.story = (TextView) convertView.findViewById(R.id.csv_story);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolderItem) convertView.getTag();
        }

        CSVEntry entry = (CSVEntry) getItem(position);

        viewHolder.id.setText(entry.id);
        viewHolder.kanji.setText(entry.kanji);
        viewHolder.keyword.setText(entry.keyword);
        viewHolder.story.setText(entry.story);

        return convertView;
    }

    public void clearStories() {
        importedStories.clear();
    }

    public void writeToDatabase() {
        KeywordDataSource keywordDataSource = new KeywordDataSource(mContext);
        keywordDataSource.open();
        List<Keyword> originalKeywords = keywordDataSource.getAllKeywords();
        keywordDataSource.close();

        final List<Story> newStories = new ArrayList<>();
        final List<Keyword> newKeywords = new ArrayList<>();
        final List<Integer> affectedIds = new ArrayList<>();
        //should be 3007
        final int LAST_HEISIG_ID = originalKeywords.get(originalKeywords.size()-1).getHeisigId();

        for (CSVEntry entry : importedStories) {
            int id = Integer.parseInt(entry.id);

            if(id < 1 || id > LAST_HEISIG_ID) {
                Log.d(TAG, "Skipped id "+id+" because its not in the standard Heisig ID set");
                continue;
            }
            affectedIds.add(id);
            //only add keyword if it differs from original one
            if (!originalKeywords.get(id - 1).getKeywordText().equals(entry.keyword)) {
                Keyword keyword = new Keyword(id, entry.keyword);
                newKeywords.add(keyword);
            }
            Story story = new Story(id, entry.story);
            newStories.add(story);
        }

        DatabaseImportTask importTask = new DatabaseImportTask();
        importTask.execute(newKeywords, newStories, affectedIds); //then go to onImportCompleted
    }

    static class ViewHolderItem {
        TextView id;
        TextView kanji;
        TextView keyword;
        TextView story;
    }

    /**
     * Write the imported CSV to the device database
     */
    private class DatabaseImportTask extends AsyncTask<List<?>, Void, List<Integer>> {
        ProgressDialog progress;
        UserKeywordDataSource userKeywordDataSource;
        StoryDataSource storyDataSource;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(mContext, "Importing Data",
                    "Please wait while adding to database", true);
            storyDataSource = new StoryDataSource(mContext);
            userKeywordDataSource = new UserKeywordDataSource(mContext);
        }

        @SuppressWarnings("unchecked")
        @Override
        protected List<Integer> doInBackground(List... params) {
            storyDataSource.open();
            userKeywordDataSource.open();
            List<Integer> affectedIds = new ArrayList<>();

            for (List list : params) {
                if (list.size() > 0) {
                    if (list.get(0) instanceof Story) {
                        if (!storyDataSource.insertStories(list)) {
                            affectedIds.clear();
                            break;
                        }
                    } else if (list.get(0) instanceof Keyword) {
                        if (!userKeywordDataSource.insertKeywords(list)) {
                            affectedIds.clear();
                            break;
                        }
                    } else if (list.get(0) instanceof Integer) {
                        affectedIds = list;
                    }
                }
            }

            return affectedIds;
        }

        @Override
        protected void onPostExecute(List<Integer> result) {
            storyDataSource.close();
            userKeywordDataSource.close();
            if (progress.isShowing()) {
                progress.dismiss();
            }
            databaseListener.onImportCompleted(result);
        }
    }

    /**
     * Populate adapter table with the given my_stories.csv
     */
    public class ReadCSVTask extends AsyncTask<File, Void, List<CSVEntry>> {
        ProgressDialog progress;

        @Override
        protected void onPreExecute() {
            progress = ProgressDialog.show(mContext, "Reading CSV",
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
            clearStories();
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

    public class CSVLineReader {
        private BufferedReader br;
        CSVLineReader(BufferedReader br) {
            this.br = br;
            try {
                //skip first line (header row)
                br.readLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String readLine() throws IOException {
            //read the first character
            int i = br.read();
            //if it's the end of the stream then return null
            if (i < 0) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            //add first character to newly created StringBuffer
            sb.append((char)i);
            //if the character which was added was not a newline character...
            if (i != '\r' && i != '\n') {
                //read and keep adding next character (and continue reading)
                while (0 <= (i = br.read())) {
                    //Terminate line if "\r is encountered
                    if (i == '\r' && sb.charAt(sb.length()-1) == '"') {
                        sb.append("\r\n");
                        return sb.toString();
                    } else {
                        sb.append((char)i);
                    }
                }
            }

            return sb.toString();
        }
    }
}
