package tech.diarmaid.koohiiaite.database.dao

import android.content.Context
import android.database.Cursor
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper
import tech.diarmaid.koohiiaite.model.Keyword
import java.util.*

/**
 * DAO for Keyword table
 */
class KeywordDataSource(context: Context) : CommonDataSource(context) {

    private val columnId = "heisig_id"
    private val columnKeyword = "keyword_text"
    private val allColumns = arrayOf(columnId, columnKeyword)

    val allKeywords: List<Keyword>
        get() {
            val keywordList = ArrayList<Keyword>()
            val orderBy = "$columnId ASC"

            val cursor = database.query(DatabaseAssetHelper.TABLE_KEYWORD,
                    allColumns, null, null, null, null, orderBy)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                keywordList.add(cursorToKeyword(cursor))
                cursor.moveToNext()
            }
            cursor.close()
            return keywordList
        }

    fun getKeywordFor(heisigId: Int): Keyword {
        val keyword: Keyword
        val cursor = database.query(DatabaseAssetHelper.TABLE_KEYWORD,
                allColumns, "$columnId = $heisigId", null, null, null, null)
        cursor.moveToFirst()
        keyword = cursorToKeyword(cursor)
        cursor.close()

        return keyword
    }

    fun getKeywordMatching(keywordText: String): Keyword? {
        var keyword: Keyword? = null
        val sql = "SELECT * FROM " + DatabaseAssetHelper.TABLE_KEYWORD + " WHERE " + columnKeyword + " = '" + keywordText + "' COLLATE NOCASE"
        val cursor = database.rawQuery(sql, null)
        cursor.moveToFirst()
        if (cursor.count > 0) {
            keyword = cursorToKeyword(cursor)
        }
        cursor.close()
        return keyword
    }

    fun getKeywordStartingWith(keywordText: String): Keyword? {
        var keyword: Keyword? = null
        val sql = "SELECT * FROM " + DatabaseAssetHelper.TABLE_KEYWORD + " WHERE " + columnKeyword + " LIKE '" + keywordText + "%' COLLATE NOCASE"
        val cursor = database.rawQuery(sql, null)
        cursor.moveToFirst()
        if (cursor.count > 0) {
            keyword = cursorToKeyword(cursor)
        }
        cursor.close()
        return keyword
    }

    private fun cursorToKeyword(cursor: Cursor): Keyword {
        return Keyword(
                cursor.getInt(0),
                cursor.getString(1))
    }
}
