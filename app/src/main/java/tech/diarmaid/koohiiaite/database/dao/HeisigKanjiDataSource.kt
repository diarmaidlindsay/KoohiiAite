package tech.diarmaid.koohiiaite.database.dao

import android.content.Context
import android.database.Cursor
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper
import tech.diarmaid.koohiiaite.model.HeisigKanji
import java.util.*

/**
 * DAO for heisig_kanji table
 */
class HeisigKanjiDataSource(context: Context) : CommonDataSource(context) {

    private val columnId = "id"
    private val columnKanji = "kanji"
    private val columnJoyo = "joyo"
    private val allColumns = arrayOf(columnId, columnKanji, columnJoyo)

    // make sure to close the cursor
    val allKanji: List<HeisigKanji>
        get() {
            val allKanji = ArrayList<HeisigKanji>()
            val orderBy = "$columnId ASC"
            val cursor = database.query(DatabaseAssetHelper.TABLE_HEISIG_KANJI,
                    allColumns, null, null, null, null, orderBy)

            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val kanji = cursorToKanji(cursor)
                allKanji.add(kanji)
                cursor.moveToNext()
            }
            cursor.close()
            return allKanji
        }

    fun getKanjiFor(id: Int): HeisigKanji {
        val theKanji: HeisigKanji
        val cursor = database.query(DatabaseAssetHelper.TABLE_HEISIG_KANJI,
                allColumns, "$columnId = $id", null, null, null, null)

        cursor.moveToFirst()
        theKanji = cursorToKanji(cursor)
        cursor.close()
        return theKanji
    }

    fun getHeisigFor(kanji: String): HeisigKanji {
        var theKanji = HeisigKanji()
        val cursor = database.query(DatabaseAssetHelper.TABLE_HEISIG_KANJI,
                allColumns, "$columnKanji = '$kanji'", null, null, null, null)

        if (cursor.moveToFirst()) {
            theKanji = cursorToKanji(cursor)
            cursor.close()
        }

        return theKanji
    }

    private fun cursorToKanji(cursor: Cursor): HeisigKanji {
        return HeisigKanji(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2))
    }
}
