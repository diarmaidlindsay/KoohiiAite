package com.diarmaidlindsay.koohii.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.diarmaidlindsay.koohii.database.DatabaseAssetHelper;
import com.diarmaidlindsay.koohii.model.Meaning;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for meaning table
 */
public class MeaningDataSource extends CommonDataSource {
    private final String COLUMN_ID = "id";
    private final String COLUMN_HEISIG_ID = "heisig_id";
    private final String COLUMN_MEANING_TEXT = "meaning_text";
    private String[] allColumns = {COLUMN_ID, COLUMN_HEISIG_ID, COLUMN_MEANING_TEXT};

    public MeaningDataSource(Context context) {
        super(context);
    }

    /**
     * Returns Story given an id from heisig_kanji table.
     * This is straightforward since both tables have 1 to 1 relationship
     * on their pks.
     * @param heisigId heisig_kanji id
     * @return null if no results found
     */
    public List<Meaning> getMeaningsForHeisigKanjiId(int heisigId)
    {
        List<Meaning> meanings = new ArrayList<>();
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_MEANING,
                allColumns, COLUMN_HEISIG_ID + " = " + heisigId, null, null, null, null);

        if(cursor.getCount() == 0)
        {
            cursor.close();
            return meanings;
        }

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            meanings.add(cursorToMeaning(cursor));
            cursor.moveToNext();
        }
        cursor.close();
        return meanings;
    }

    private Meaning cursorToMeaning(Cursor cursor)
    {
        return new Meaning(cursor.getInt(0), cursor.getInt(1), cursor.getString(2));
    }
}
