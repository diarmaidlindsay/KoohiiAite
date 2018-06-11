package tech.diarmaid.koohiiaite.database.dao

import android.content.Context
import android.database.Cursor
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper
import tech.diarmaid.koohiiaite.model.Meaning
import java.util.*

/**
 * DAO for meaning table
 */
class MeaningDataSource(context: Context) : CommonDataSource(context) {
    private val columnId = "id"
    private val columnHeisigId = "heisig_id"
    private val columnMeaningText = "meaning_text"
    private val allColumns = arrayOf(columnId, columnHeisigId, columnMeaningText)

    /**
     * Returns Story given an id from heisig_kanji table.
     * This is straightforward since both tables have 1 to 1 relationship
     * on their pks.
     * @param heisigId heisig_kanji id
     * @return null if no results found
     */
    fun getMeaningsForHeisigKanjiId(heisigId: Int): List<Meaning> {
        val meanings = ArrayList<Meaning>()
        val cursor = database.query(DatabaseAssetHelper.TABLE_MEANING,
                allColumns, "$columnHeisigId = $heisigId", null, null, null, null)

        if (cursor.count == 0) {
            cursor.close()
            return meanings
        }

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            meanings.add(cursorToMeaning(cursor))
            cursor.moveToNext()
        }
        cursor.close()
        return meanings
    }

    private fun cursorToMeaning(cursor: Cursor): Meaning {
        return Meaning(cursor.getInt(0), cursor.getInt(1), cursor.getString(2))
    }
}
