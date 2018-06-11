package tech.diarmaid.koohiiaite.database.dao

import android.content.Context
import android.database.Cursor
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper
import tech.diarmaid.koohiiaite.model.Reading

/**
 * DAO for reading table
 */
class ReadingDataSource(context: Context) : CommonDataSource(context) {
    private val columnId = "id"
    private val columnHeisigId = "heisig_id"
    private val columnReadingText = "reading_text"
    private val columnType = "type"
    private val allColumns = arrayOf(columnId, columnHeisigId, columnReadingText, columnType)

    /**
     * Return Meanings for a given heisigId and reading type
     *
     * @param heisigId - heisigId
     * @param type - 0 for onyomi, 1 for kunyomi
     * @return - null if no match found
     */
    fun getMeaningForHeisigKanjiId(heisigId: Int, type: Int): Reading? {
        val cursor = database.query(DatabaseAssetHelper.TABLE_READING,
                allColumns, "$columnHeisigId = $heisigId AND $columnType = $type", null, null, null, null)

        if (cursor.count == 0) {
            cursor.close()
            return null
        }

        cursor.moveToFirst()
        val meaning = cursorToReading(cursor)
        cursor.close()
        return meaning
    }

    private fun cursorToReading(cursor: Cursor): Reading {
        return Reading(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3))
    }
}
