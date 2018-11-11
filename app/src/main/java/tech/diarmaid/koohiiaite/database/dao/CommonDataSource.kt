package tech.diarmaid.koohiiaite.database.dao

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper

/**
 * Common DAO methods
 */
abstract class CommonDataSource internal constructor(context: Context) : AutoCloseable {
    internal lateinit var database: SQLiteDatabase
    private val dbHelper: DatabaseAssetHelper = DatabaseAssetHelper.getInstance(context)

    fun open() {
        database = dbHelper.writableDatabase
    }

    override fun close() {
        dbHelper.close()
    }
}
