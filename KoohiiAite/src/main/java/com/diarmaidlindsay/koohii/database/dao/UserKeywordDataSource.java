package com.diarmaidlindsay.koohii.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.diarmaidlindsay.koohii.database.DatabaseAssetHelper;
import com.diarmaidlindsay.koohii.model.Keyword;

import java.util.HashMap;
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

    public Map<Integer, String> getAllUserKeywords()
    {
        Map<Integer, String> keywordList = new HashMap<>();
        String orderBy =  COLUMN_ID + " ASC";

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

    public Keyword getKeywordFor(int heisigId)
    {
        Keyword keyword = null;
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_USER_KEYWORD,
                allColumns, COLUMN_ID+ " = "+ heisigId, null, null, null, null);
        if(cursor.moveToFirst())
        {
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
}
