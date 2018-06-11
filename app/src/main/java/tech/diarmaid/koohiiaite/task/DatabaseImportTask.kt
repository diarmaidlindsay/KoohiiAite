package tech.diarmaid.koohiiaite.task

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import tech.diarmaid.koohiiaite.database.dao.StoryDataSource
import tech.diarmaid.koohiiaite.database.dao.UserKeywordDataSource
import tech.diarmaid.koohiiaite.interfaces.OnDatabaseOperationCompleted
import tech.diarmaid.koohiiaite.model.Keyword
import tech.diarmaid.koohiiaite.model.Story
import java.lang.ref.WeakReference
import java.util.*

/**
 * Write the imported CSV to the device database
 */
class DatabaseImportTask(context: Context, private val databaseListener: OnDatabaseOperationCompleted) : AsyncTask<List<*>, Void, List<Int>>() {
    private var progress: ProgressDialog? = null
    private var userKeywordDataSource: UserKeywordDataSource? = null
    private var storyDataSource: StoryDataSource? = null
    private val context: WeakReference<Context> = WeakReference(context)

    override fun onPreExecute() {
        progress = ProgressDialog.show(context.get(), "Importing Data",
                "Please wait while adding to database", true)
        storyDataSource = context.get()?.let { StoryDataSource(it) }
        userKeywordDataSource = context.get()?.let { UserKeywordDataSource(it) }
    }

    override fun doInBackground(vararg params: List<*>): List<Int> {
        storyDataSource!!.open()
        userKeywordDataSource!!.open()
        var affectedIds: MutableList<Int> = ArrayList()

        for (list in params) {
            if (list.isNotEmpty()) {
                if (list[0] is Story) {
                    if (!storyDataSource!!.insertStories(list as List<Story>)) {
                        affectedIds.clear()
                        break
                    }
                } else if (list[0] is Keyword) {
                    if (!userKeywordDataSource!!.insertKeywords(list as List<Keyword>)) {
                        affectedIds.clear()
                        break
                    }
                } else if (list[0] is Int) {
                    affectedIds = list as MutableList<Int>
                }
            }
        }

        return affectedIds
    }

    override fun onPostExecute(result: List<Int>) {
        storyDataSource!!.close()
        userKeywordDataSource!!.close()
        if (progress!!.isShowing) {
            progress!!.dismiss()
        }
        databaseListener.onImportCompleted(result)
    }
}
