package tech.diarmaid.koohiiaite.database.dao

import android.content.Context
import android.database.Cursor
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper
import tech.diarmaid.koohiiaite.model.SampleWord
import java.util.*

/**
 * DAO for the sample_word table
 */
class SampleWordDataSource(context: Context) : CommonDataSource(context) {

    private val columnId = "id"
    private val columnHeisigId = "heisig_id"
    private val columnKanjiWord = "kanji_word"
    private val columnHiraganaMeaning = "hiragana_reading"
    private val columnEnglishMeaning = "english_meaning"
    private val columnCategory = "category"
    private val columnFrequency = "frequency"
    private val allColumns = arrayOf(columnId, columnHeisigId, columnKanjiWord, columnHiraganaMeaning, columnEnglishMeaning, columnCategory, columnFrequency)

    fun getSampleWordsFor(heisigId: Int): List<SampleWord> {
        val sampleWords = ArrayList<SampleWord>()

        val cursor = database.query(DatabaseAssetHelper.TABLE_SAMPLE_WORD,
                allColumns, "$columnHeisigId = $heisigId", null, null, null, null)

        if (cursor.count == 0) {
            cursor.close()
            return sampleWords
        }

        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            sampleWords.add(cursorToSampleWord(cursor))
            cursor.moveToNext()
        }

        cursor.close()

        return sampleWords
    }

    private fun cursorToSampleWord(cursor: Cursor): SampleWord {
        return SampleWord(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5), cursor.getInt(6))
    }
}
