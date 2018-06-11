package tech.diarmaid.koohiiaite.task

import android.app.ProgressDialog
import android.content.Context
import android.os.AsyncTask
import tech.diarmaid.koohiiaite.adapter.ImportStoryAdapter
import tech.diarmaid.koohiiaite.interfaces.OnCSVParseCompleted
import tech.diarmaid.koohiiaite.model.CSVEntry
import tech.diarmaid.koohiiaite.utils.CSVLineReader
import tech.diarmaid.koohiiaite.utils.Utils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.lang.ref.WeakReference
import java.util.*

/**
 * Populate adapter table with the given my_stories.csv
 */
class ReadCSVTask(context: Context, private val csvListener: OnCSVParseCompleted, private val importStoryAdapter: ImportStoryAdapter //TODO : Decouple this class and instead pass the method itself, functional programming style
) : AsyncTask<File, Void, List<CSVEntry>>() {
    private var progress: ProgressDialog? = null
    private val context: WeakReference<Context>

    init {
        this.context = WeakReference(context)
    }

    override fun onPreExecute() {
        progress = ProgressDialog.show(context.get(), "Reading CSV",
                "Please wait while parsing CSV", true)
    }

    override fun doInBackground(vararg params: File): List<CSVEntry> {
        val entries = ArrayList<CSVEntry>()

        if (params.size != 1) {
            return entries
        }
        var br: BufferedReader? = null
        val lr: CSVLineReader
        val csvSplitBy = ","
        importStoryAdapter.clearStories()
        try {
            br = BufferedReader(FileReader(params[0]))
            lr = CSVLineReader(br)

            while (true)  {
                val line = lr.readLine() ?: break
                val row = line.split(csvSplitBy.toRegex(), 6).toTypedArray()
                //first row is the column headers, we should ignore
                if (row.size == 6 && Utils.isNumeric(row[0])) {
                    entries.add(CSVEntry(row[0], row[1], row[2], row[3], row[4], row[5]))
                }
            }

            /*
            val reader = BufferedReader(reader)
while (true) {
    val line = reader.readLine() ?: break
    System.out.println(line);
}
             */

        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            if (br != null) {
                try {
                    br.close()
                } catch (e: IOException) {
                    e.printStackTrace()

                }

            }
        }
        return entries
    }

    override fun onPostExecute(parsedEntries: List<CSVEntry>) {
        if (progress!!.isShowing) {
            progress!!.dismiss()
        }
        csvListener.onParsingCompleted(parsedEntries)
    }
}
