package tech.diarmaid.koohiiaite.database

import android.content.Context
import android.util.Log
import androidx.room.Room
import java.io.FileOutputStream
import java.io.IOException

/**
 * https://android.jlelse.eu/room-persistence-library-with-pre-populated-database-5f17ef103d3d
 */
class DatabaseCopier {

    private var roomDatabase: AppDatabase? = null

    init {
        //call method that check if database not exists and copy prepopulated file from assets

    }

    fun init(appContext: Context) {
        copyAttachedDatabase(appContext, DATABASE_NAME)
        roomDatabase = Room.databaseBuilder(appContext,
                AppDatabase::class.java, DATABASE_NAME)
                .build()
    }

    private fun copyAttachedDatabase(context: Context, databaseName: String) {
        val dbPath = context.getDatabasePath(databaseName)

        // If the database already exists, return
        if (dbPath.exists()) {
            return
        }

        // Make sure we have a path to the file
        dbPath.parentFile.mkdirs()

        // Try to copy database file
        try {
            val inputStream = context.assets.open("databases/$databaseName")
            val output = FileOutputStream(dbPath)

            val buffer = ByteArray(8192)
            while (inputStream.read(buffer) > 0) {
                output.write(buffer)
                Log.d("#DB", "writing>>")
            }

            output.flush()
            output.close()
            inputStream.close()
        } catch (e: IOException) {
            Log.d(TAG, "Failed to open file", e)
            e.printStackTrace()
        }

    }

    companion object {
        private val TAG = DatabaseCopier::class.java.simpleName
        private const val DATABASE_NAME = "koohii.sqlite3.db"
    }

}