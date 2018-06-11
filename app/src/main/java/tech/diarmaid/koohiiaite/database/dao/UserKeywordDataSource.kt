package tech.diarmaid.koohiiaite.database.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.util.SparseArray

import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper
import tech.diarmaid.koohiiaite.model.Keyword

/**
 * DAO for User Keyword table
 */
class UserKeywordDataSource(context: Context) : CommonDataSource(context) {

    private val columnId = "heisig_id"
    private val columnKeyword = "keyword_text"
    private val allColumns = arrayOf(columnId, columnKeyword)

    val allUserKeywords: SparseArray<String>
        get() {
            val keywordList = SparseArray<String>()
            val orderBy = "$columnId ASC"

            val cursor = database.query(DatabaseAssetHelper.TABLE_USER_KEYWORD,
                    allColumns, null, null, null, null, orderBy)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val keyword = cursorToKeyword(cursor)
                keywordList.put(keyword.heisigId, keyword.keywordText)
                cursor.moveToNext()
            }
            cursor.close()
            return keywordList
        }

    fun getKeywordFor(heisigId: Int): Keyword? {
        var keyword: Keyword? = null
        val cursor = database.query(DatabaseAssetHelper.TABLE_USER_KEYWORD,
                allColumns, "$columnId = $heisigId", null, null, null, null)
        if (cursor.moveToFirst()) {
            keyword = cursorToKeyword(cursor)
        }
        cursor.close()

        return keyword
    }

    fun getKeywordMatching(keywordText: String): Keyword? {
        var keyword: Keyword? = null
        val sql = "SELECT * FROM " + DatabaseAssetHelper.TABLE_USER_KEYWORD + " WHERE " + columnKeyword + " = '" + keywordText + "' COLLATE NOCASE"
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
        val sql = "SELECT * FROM " + DatabaseAssetHelper.TABLE_USER_KEYWORD + " WHERE " + columnKeyword + " LIKE '" + keywordText + "%' COLLATE NOCASE"
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

    /**
     * Returns true if insertion successful
     */
    fun insertKeyword(heisigId: Int, keyword: String): Boolean {
        val values = ContentValues()
        values.put(columnId, heisigId)
        values.put(columnKeyword, keyword)
        //-1 is failed insertion, so return true if not -1
        return database.insert(DatabaseAssetHelper.TABLE_USER_KEYWORD, null, values) != -1L
    }

    /**
     * returns true if insertion successful
     */
    fun insertKeywords(keywords: List<Keyword>): Boolean {
        var success = true
        for (keyword in keywords) {
            if (!updateKeyword(keyword.heisigId, keyword.keywordText)) {
                if (!insertKeyword(keyword.heisigId, keyword.keywordText)) {
                    success = false
                }
            }
        }

        return success
    }

    fun updateKeyword(heisigId: Int, keyword: String): Boolean {
        val values = ContentValues()
        values.put(columnKeyword, keyword)

        // Which row to update, based on the ID
        val selection = "$columnId = ?"
        val selectionArgs = arrayOf(heisigId.toString())

        //0 means no rows affected, so return true if not 0
        return database.update(
                DatabaseAssetHelper.TABLE_USER_KEYWORD,
                values,
                selection,
                selectionArgs) != 0
    }

    fun deleteKeyword(heisigId: Int): Boolean {
        // Define 'where' part of query.
        val selection = "$columnId = ?"
        // Specify arguments in placeholder order.
        val selectionArgs = arrayOf(heisigId.toString())
        //0 is failed deletion, so return true if not 0
        return database.delete(DatabaseAssetHelper.TABLE_USER_KEYWORD, selection, selectionArgs) != 0
    }
}
