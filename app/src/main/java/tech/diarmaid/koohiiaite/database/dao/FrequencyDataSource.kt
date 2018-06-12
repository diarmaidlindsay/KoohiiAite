package tech.diarmaid.koohiiaite.database.dao

import android.content.Context
import android.database.Cursor
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper

/**
 * DAO for kanji_frequency table
 */
class FrequencyDataSource(context: Context) : CommonDataSource(context) {

    private val columnId = "heisig_id"
    private val columnFrequency = "frequency"
    private val allColumns = arrayOf(columnId, columnFrequency)

    fun getFrequencyFor(heisigId: Int): Int? {
        val cursor = database.query(DatabaseAssetHelper.TABLE_FREQUENCY,
                allColumns, "$columnId = $heisigId", null, null, null, null)
        var frequency = 9999
        if (cursor != null) {
            if (cursor.count == 0) {
                cursor.close()
                return frequency //should not happen!
            }

            cursor.moveToFirst()
            frequency = cursor.getInt(1)
            cursor.close()
        }

        return frequency
    }

    private fun cursorToFrequency(cursor: Cursor): Frequency {
        return Frequency(cursor.getInt(0), cursor.getInt(1))
    }

    private inner class Frequency(internal var id: Int, internal var frequency: Int)
}
