package tech.diarmaid.koohiiaite.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper;
import tech.diarmaid.koohiiaite.model.Keyword;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * DAO for User Keyword table
 */
public class UserKeywordDataSource extends CommonDataSource {

    private final String COLUMN_ID = "heisig_id";
    private final String COLUMN_KEYWORD = "keyword_text";
    private String[] allColumns = {COLUMN_ID, COLUMN_KEYWORD};

    public UserKeywordDataSource(Context context) {
        super(context);
    }

    public Map<Integer, String> getAllUserKeywords() {
        Map<Integer, String> keywordList = new HashMap<>();
        String orderBy = COLUMN_ID + " ASC";

        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_USER_KEYWORD,
                allColumns, null, null, null, null, orderBy);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Keyword keyword = cursorToKeyword(cursor);
            keywordList.put(keyword.getHeisigId(), keyword.getKeywordText());
            cursor.moveToNext();
        }
        cursor.close();
        return keywordList;
    }

    public Keyword getKeywordFor(int heisigId) {
        Keyword keyword = null;
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_USER_KEYWORD,
                allColumns, COLUMN_ID + " = " + heisigId, null, null, null, null);
        if (cursor.moveToFirst()) {
            keyword = cursorToKeyword(cursor);
        }
        cursor.close();

        return keyword;
    }

    public Keyword getKeywordMatching(String keywordText) {
        Keyword keyword = null;
        String sql = "SELECT * FROM "+DatabaseAssetHelper.TABLE_USER_KEYWORD+" WHERE "+COLUMN_KEYWORD+" = '" + keywordText + "' COLLATE NOCASE";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            keyword = cursorToKeyword(cursor);
        }
        cursor.close();
        return keyword;
    }

    public Keyword getKeywordStartingWith(String keywordText) {
        Keyword keyword = null;
        String sql = "SELECT * FROM "+DatabaseAssetHelper.TABLE_USER_KEYWORD+" WHERE "+COLUMN_KEYWORD+" LIKE '" + keywordText + "%' COLLATE NOCASE";
        Cursor cursor = database.rawQuery(sql, null);
        cursor.moveToFirst();
        if(cursor.getCount() > 0) {
            keyword = cursorToKeyword(cursor);
        }
        cursor.close();
        return keyword;
    }

    private Keyword cursorToKeyword(Cursor cursor) {
        return new Keyword(
                cursor.getInt(0),
                cursor.getString(1));
    }

    /**
     * Returns true if insertion successful
     */
    public boolean insertKeyword(int heisigId, String keyword) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, heisigId);
        values.put(COLUMN_KEYWORD, keyword);
        //-1 is failed insertion, so return true if not -1
        return database.insert(DatabaseAssetHelper.TABLE_USER_KEYWORD, null, values) != -1;
    }

    /**
     * returns true if insertion successful
     */
    public boolean insertKeywords(List<Keyword> keywords) {
        boolean success = true;
        for (Keyword keyword : keywords) {
            if (!updateKeyword(keyword.getHeisigId(), keyword.getKeywordText())) {
                if (!insertKeyword(keyword.getHeisigId(), keyword.getKeywordText())) {
                    success = false;
                }
            }
        }

        return success;
    }

    public boolean updateKeyword(int heisigId, String keyword) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_KEYWORD, keyword);

        // Which row to update, based on the ID
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(heisigId)};

        //0 means no rows affected, so return true if not 0
        return database.update(
                DatabaseAssetHelper.TABLE_USER_KEYWORD,
                values,
                selection,
                selectionArgs) != 0;
    }

    public boolean deleteKeyword(int heisigId) {
        // Define 'where' part of query.
        String selection = COLUMN_ID + " = ?";
        // Specify arguments in placeholder order.
        String[] selectionArgs = {String.valueOf(heisigId)};
        //0 is failed deletion, so return true if not 0
        return database.delete(DatabaseAssetHelper.TABLE_USER_KEYWORD, selection, selectionArgs) != 0;
    }
}
