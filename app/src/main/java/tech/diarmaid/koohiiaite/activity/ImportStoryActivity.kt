package tech.diarmaid.koohiiaite.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_import_story.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.adapter.ImportStoryAdapter
import tech.diarmaid.koohiiaite.database.AppDatabase
import tech.diarmaid.koohiiaite.database.entity.Story
import tech.diarmaid.koohiiaite.database.entity.UserKeyword
import tech.diarmaid.koohiiaite.interfaces.OnCSVParseCompleted
import tech.diarmaid.koohiiaite.interfaces.OnDatabaseOperationCompleted
import tech.diarmaid.koohiiaite.model.CSVEntry
import tech.diarmaid.koohiiaite.utils.CSVLineReader
import tech.diarmaid.koohiiaite.utils.Constants.REQUEST_CODE_FILE_CHOOSER
import tech.diarmaid.koohiiaite.utils.Utils
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Import CSV and display result of import in a table, so the user can
 * confirm or cancel the changes.
 */
class ImportStoryActivity : AppCompatActivity(), OnDatabaseOperationCompleted, OnCSVParseCompleted, CoroutineScope {
    private lateinit var listAdapter: ImportStoryAdapter
    private val userKeywordDataSource = AppDatabase.getDatabase(this).userKeywordDao()
    private val storyDataSource = AppDatabase.getDatabase(this).storyDao()
    private val keywordDataSource = AppDatabase.getDatabase(this).keywordDao()
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_import_story)

        story_import_button_choose_file.setOnClickListener { openFileChooser() }

        story_import_button_cancel_import.setOnClickListener {
            resetView()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        listAdapter = ImportStoryAdapter(this)
        story_import_button_confirm_import.setOnClickListener { writeToDatabase(this) }
        import_story_listView.adapter = listAdapter
        story_import_button_cancel_import.isEnabled = listAdapter.count > 0
        story_import_button_confirm_import.isEnabled = listAdapter.count > 0
    }

    private fun resetView() {
        listAdapter.clearStories()
        story_import_button_confirm_import.isEnabled = false
        story_import_button_cancel_import.isEnabled = false
        listAdapter.notifyDataSetChanged()
    }

    private fun openFileChooser() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "text/comma-separated-values"
        }

        startActivityForResult(intent, REQUEST_CODE_FILE_CHOOSER)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_FILE_CHOOSER && resultCode == Activity.RESULT_OK) {
            readCSV(listAdapter, data?.data, this)
        }
    }

    private fun readCSV(importStoryAdapter: ImportStoryAdapter, uri: Uri?, csvListener: OnCSVParseCompleted) {
        story_import_status.text = getText(R.string.progress_status_csv)
        setOperationInProgress(true)
        launch(Dispatchers.IO) {
            val entries = ArrayList<CSVEntry>()
            val csvSplitBy = ","
            importStoryAdapter.clearStories()
            uri.let { it ->
                contentResolver?.openInputStream(it as Uri).use { inputStream ->
                    BufferedReader(InputStreamReader(inputStream)).use { reader ->
                        val csvLineReader = CSVLineReader(reader)
                        while (true) {
                            val line = csvLineReader.readLine() ?: break
                            val row = line.split(csvSplitBy.toRegex(), 6).toTypedArray()
                            //first row is the column headers, we should ignore
                            if (row.size == 6 && Utils.isNumeric(row[0])) {
                                entries.add(CSVEntry(row[0], row[1], row[2], row[5]))
                            }
                        }
                    }
                }
            }

            launch(Dispatchers.Main) {
                csvListener.onParsingCompleted(entries)
            }
        }
    }

    private fun writeToDatabase(databaseListener: OnDatabaseOperationCompleted) {
        keywordDataSource.allKeywords().observe(this@ImportStoryActivity, androidx.lifecycle.Observer { originalKeywords ->
            val newStories = ArrayList<Story>()
            val newKeywords = ArrayList<UserKeyword>()
            val affectedIds = ArrayList<Int>()
            //should be 3007
            val lastHeisigId = originalKeywords[originalKeywords.size - 1].heisigId

            for (entry in listAdapter.importedStories) {
                val id = Integer.parseInt(entry.id)

                if (id < 1 || id > lastHeisigId) {
                    Log.d(this.javaClass.simpleName, "Skipped id $id because its not in the standard Heisig ID set")
                    continue
                }
                affectedIds.add(id)
                //only add keyword if it differs from original one
                if (originalKeywords[id - 1].keywordText != entry.keyword) {
                    val keyword = UserKeyword(id, entry.keyword)
                    newKeywords.add(keyword)
                }
                val story = Story(id, entry.story)
                newStories.add(story)
            }

            launch(Dispatchers.Main) {
                story_import_status.text = getText(R.string.progress_status_database)
                setOperationInProgress(true)
            }

            launch(Dispatchers.IO) {
                storyDataSource.insertStories(*newStories.toTypedArray())
                userKeywordDataSource.insertKeywords(*newKeywords.toTypedArray())
            }.invokeOnCompletion {
                launch(Dispatchers.Main) {
                    databaseListener.onImportCompleted(affectedIds)
                }
            }
        })
    }

    private fun setResult(heisigIds: List<Int>) {
        val returnIntent = intent
        returnIntent.putExtra("heisigIds", Utils.toIntArray(heisigIds))
        setResult(Activity.RESULT_OK, returnIntent)
    }

    private fun setOperationInProgress(progress: Boolean) {
        story_import_button_cancel_import.visibility = if (progress) View.GONE else View.VISIBLE
        story_import_button_choose_file.visibility = if (progress) View.GONE else View.VISIBLE
        story_import_button_confirm_import.visibility = if (progress) View.GONE else View.VISIBLE
        story_import_progress_bar.visibility = if (progress) View.VISIBLE else View.GONE
    }

    override fun onImportCompleted(affectedIds: List<Int>) {
        setOperationInProgress(false)
        if (affectedIds.isNotEmpty()) {
            setResult(affectedIds)
            Toast.makeText(this, affectedIds.size.toString() + " Stories and Keywords updated", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Failed to write to database", Toast.LENGTH_SHORT).show()
            setResult(Activity.RESULT_CANCELED)
        }
        resetView()
        finish()
    }

    override fun onParsingCompleted(parsedEntries: List<CSVEntry>) {
        setOperationInProgress(false)
        if (parsedEntries.isNotEmpty()) {
            listAdapter.setStories(parsedEntries)
            listAdapter.notifyDataSetChanged()
            story_import_button_confirm_import.isEnabled = true
            story_import_button_cancel_import.isEnabled = true
            Toast.makeText(this, "CSV file import successful", Toast.LENGTH_SHORT).show()
            story_import_progress_bar.visibility = View.GONE
            story_import_status.text = String.format(Locale.getDefault(), "%d stories found for import.", listAdapter.count)
        } else {
            Toast.makeText(this, "Failed to read CSV file", Toast.LENGTH_SHORT).show()
        }
    }
}
