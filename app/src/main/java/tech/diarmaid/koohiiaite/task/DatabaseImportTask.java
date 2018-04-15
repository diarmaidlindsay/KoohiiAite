package tech.diarmaid.koohiiaite.task;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import tech.diarmaid.koohiiaite.database.dao.StoryDataSource;
import tech.diarmaid.koohiiaite.database.dao.UserKeywordDataSource;
import tech.diarmaid.koohiiaite.interfaces.OnDatabaseOperationCompleted;
import tech.diarmaid.koohiiaite.model.Keyword;
import tech.diarmaid.koohiiaite.model.Story;

/**
 * Write the imported CSV to the device database
 */
public class DatabaseImportTask extends AsyncTask<List<?>, Void, List<Integer>> {
    private ProgressDialog progress;
    private UserKeywordDataSource userKeywordDataSource;
    private StoryDataSource storyDataSource;
    private final WeakReference<Context> context;
    private OnDatabaseOperationCompleted databaseListener;

    public DatabaseImportTask(Context context, OnDatabaseOperationCompleted databaseListener) {
        this.context = new WeakReference<>(context);
        this.databaseListener = databaseListener;
    }

    @Override
    protected void onPreExecute() {
        progress = ProgressDialog.show(context.get(), "Importing Data",
                "Please wait while adding to database", true);
        storyDataSource = new StoryDataSource(context.get());
        userKeywordDataSource = new UserKeywordDataSource(context.get());
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
