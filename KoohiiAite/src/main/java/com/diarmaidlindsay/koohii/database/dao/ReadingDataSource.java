package com.diarmaidlindsay.koohii.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.diarmaidlindsay.koohii.database.DatabaseAssetHelper;
import com.diarmaidlindsay.koohii.model.Reading;

/**
 * DAO for reading table
 */
public class ReadingDataSource extends CommonDataSource {
    private final String COLUMN_ID = "id";
    private final String COLUMN_HEISIG_ID = "heisig_id";
    private final String COLUMN_READING_TEXT = "reading_text";
    private final String COLUMN_TYPE = "type";
    private String[] allColumns = {COLUMN_ID, COLUMN_HEISIG_ID, COLUMN_READING_TEXT, COLUMN_TYPE};

    public ReadingDataSource(Context context) {
        super(context);
    }

    /**
     * Return Meanings for a given heisig_id and reading type
     *
     * @param heisigId - heisig_id
     * @param type - 0 for onyomi, 1 for kunyomi
     * @return - null if no match found
     */
    public Reading getMeaningForHeisigKanjiId(int heisigId, int type)
    {
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_READING,
                allColumns, COLUMN_HEISIG_ID + " = " + heisigId + " AND " + COLUMN_TYPE + " = " + type,
                null, null, null, null);

        if(cursor.getCount() == 0)
        {
            cursor.close();
            return null;
        }

        cursor.moveToFirst();
        Reading meaning = cursorToReading(cursor);
        cursor.close();
        return meaning;
    }

    private Reading cursorToReading(Cursor cursor)
    {
        return new Reading(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getInt(3));
    }
}
