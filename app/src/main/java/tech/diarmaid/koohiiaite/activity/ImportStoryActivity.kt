package tech.diarmaid.koohiiaite.activity

import android.Manifest
import android.app.Activity
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import com.ipaulpro.afilechooser.FileChooserActivity
import com.ipaulpro.afilechooser.utils.FileUtils
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.adapter.ImportStoryAdapter
import tech.diarmaid.koohiiaite.interfaces.OnCSVParseCompleted
import tech.diarmaid.koohiiaite.interfaces.OnDatabaseOperationCompleted
import tech.diarmaid.koohiiaite.model.CSVEntry
import tech.diarmaid.koohiiaite.task.ReadCSVTask
import tech.diarmaid.koohiiaite.utils.Constants.PERMISSION_REQUEST_READ_STORAGE
import tech.diarmaid.koohiiaite.utils.Constants.REQUEST_CODE_FILE_CHOOSER
import tech.diarmaid.koohiiaite.utils.Utils
import java.io.File
import java.util.*

/**
 * Import CSV and display result of import in a table, so the user can
 * confirm or cancel the changes.
 */
class ImportStoryActivity : AppCompatActivity(), OnDatabaseOperationCompleted, OnCSVParseCompleted {
    private var listCSVContent: ListView? = null //the csv file's valid rows
    private var buttonChooseFile: Button? = null //open file browser
    private var buttonConfirm: Button? = null //write to database
    private var buttonCancel: Button? = null //clear table, disable confirm button
    private var importCount: TextView? = null //display amount of stories to be imported
    private lateinit var listAdapter: ImportStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_import_story)

        buttonChooseFile = findViewById(R.id.button_choose_file)
        buttonConfirm = findViewById(R.id.button_confirm_import)
        buttonCancel = findViewById(R.id.button_cancel_import)
        listCSVContent = findViewById(R.id.import_story_listView)
        importCount = findViewById(R.id.story_import_count)
        buttonChooseFile?.setOnClickListener { checkPermission() }

        buttonCancel?.setOnClickListener {
            resetView()
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        listAdapter = ImportStoryAdapter(this, this)
        buttonConfirm?.setOnClickListener { listAdapter.writeToDatabase() }
        listCSVContent?.adapter = listAdapter
        buttonCancel?.isEnabled = listAdapter.count > 0
        buttonConfirm?.isEnabled = listAdapter.count > 0
    }

    private fun resetView() {
        listAdapter.clearStories()
        buttonConfirm?.isEnabled = false
        buttonCancel?.isEnabled = false
        listAdapter.notifyDataSetChanged()
    }

    private fun checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            // No explanation needed, we can request the permission.
            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_READ_STORAGE)

            // The callback method gets the result of the request.
        } else {
            openFileChooser()
        }
    }

    private fun openFileChooser() {
        val i = Intent(this, FileChooserActivity::class.java)
        startActivityForResult(i, REQUEST_CODE_FILE_CHOOSER)
    }

    override fun onRequestPermissionsResult(requestCode: Int,
                                            permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_READ_STORAGE -> {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // http://stackoverflow.com/a/32473449/4653788
                    // Need to restart the process to grant write to SD permissions
                    // Schedule start after 1 second
                    val pi = PendingIntent.getActivity(
                            this,
                            0,
                            intent,
                            PendingIntent.FLAG_CANCEL_CURRENT)
                    val am = getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    am.set(AlarmManager.RTC, System.currentTimeMillis() + 1000, pi)

                    // Stop now
                    System.exit(0)
                } else {
                    Toast.makeText(this,
                            "Please grant permission to read External Storage in order to Import CSV",
                            Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_CODE_FILE_CHOOSER && resultCode == Activity.RESULT_OK) {
            val uri = data.data
            val path = FileUtils.getPath(this, uri)

            if (path != null) {
                //execute task
                val csvTask = ReadCSVTask(this, this, listAdapter)
                csvTask.execute(File(path)) //then go to onParsingCompleted
            }
        }
    }

    private fun setResult(heisigIds: List<Int>) {
        val returnIntent = intent
        returnIntent.putExtra("heisigIds", Utils.toIntArray(heisigIds))
        setResult(Activity.RESULT_OK, returnIntent)
    }

    override fun onImportCompleted(affectedIds: List<Int>) {
        if (affectedIds.isNotEmpty()) {
            setResult(affectedIds)
//            ToastUtil.makeText(this, affectedIds.size.toString() + " Stories and Keywords updated", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, affectedIds.size.toString() + " Stories and Keywords updated", Toast.LENGTH_SHORT).show()

        } else {
//            ToastUtil.makeText(this, "Failed to write to database", Toast.LENGTH_LONG).show()
            Toast.makeText(this, "Failed to write to database", Toast.LENGTH_LONG).show()
            setResult(Activity.RESULT_CANCELED)
        }
        resetView()
        finish()
    }

    override fun onParsingCompleted(parsedEntries: List<CSVEntry>) {
        if (parsedEntries.isNotEmpty()) {
            listAdapter.setStories(parsedEntries)
            listAdapter.notifyDataSetChanged()
            buttonConfirm?.isEnabled = true
            buttonCancel?.isEnabled = true
//            ToastUtil.makeText(this, "CSV file import successful", Toast.LENGTH_SHORT).show()
            Toast.makeText(this, "CSV file import successful", Toast.LENGTH_SHORT).show()
            importCount?.text = String.format(Locale.getDefault(), "%d stories found for import.", listAdapter.count)
        } else {
//            ToastUtil.makeText(this, "Failed to read CSV file", Toast.LENGTH_LONG).show()
            Toast.makeText(this, "Failed to read CSV file", Toast.LENGTH_LONG).show()
        }
    }
}
