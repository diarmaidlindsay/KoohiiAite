package tech.diarmaid.koohiiaite.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import tech.diarmaid.koohiiaite.R;
import tech.diarmaid.koohiiaite.database.dao.KeywordDataSource;
import tech.diarmaid.koohiiaite.interfaces.OnDatabaseOperationCompleted;
import tech.diarmaid.koohiiaite.model.CSVEntry;
import tech.diarmaid.koohiiaite.model.Keyword;
import tech.diarmaid.koohiiaite.model.Story;
import tech.diarmaid.koohiiaite.task.DatabaseImportTask;

/**
 * Adapter for the List View in the Activity which displays importedStories CSV
 */
public class ImportStoryAdapter extends BaseAdapter {
    private OnDatabaseOperationCompleted databaseListener;

    private final String TAG = ImportStoryAdapter.class.getName();

    private List<CSVEntry> importedStories = new ArrayList<>();
    private Context mContext;
    private LayoutInflater layoutInflater;

    public ImportStoryAdapter(Context context, OnDatabaseOperationCompleted databaseListener) {
        this.mContext = context;
        this.databaseListener = databaseListener;
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
        ViewHolderItem viewHolder;
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_import_story, parent, false);
            viewHolder = new ViewHolderItem();
            viewHolder.id = convertView.findViewById(R.id.csv_id);
            viewHolder.kanji = convertView.findViewById(R.id.csv_kanji);
            viewHolder.keyword = convertView.findViewById(R.id.csv_keyword);
            viewHolder.story = convertView.findViewById(R.id.csv_story);

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

        DatabaseImportTask importTask = new DatabaseImportTask(mContext, databaseListener);
        importTask.execute(newKeywords, newStories, affectedIds); //then go to onImportCompleted
    }

    static class ViewHolderItem {
        TextView id;
        TextView kanji;
        TextView keyword;
        TextView story;
    }
}
