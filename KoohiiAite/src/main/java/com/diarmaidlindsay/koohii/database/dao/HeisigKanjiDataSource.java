package com.diarmaidlindsay.koohii.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.diarmaidlindsay.koohii.database.DatabaseAssetHelper;
import com.diarmaidlindsay.koohii.model.HeisigKanji;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for heisig_kanji table
 */
public class HeisigKanjiDataSource extends CommonDataSource {

    private final String COLUMN_ID = "id";
    private final String COLUMN_KANJI = "kanji";
    private final String COLUMN_JOYO = "joyo";
    private String[] allColumns = {COLUMN_ID, COLUMN_KANJI, COLUMN_JOYO};

    public HeisigKanjiDataSource(Context context) {
        super(context);
    }

    public List<HeisigKanji> getAllKanji() {
        List<HeisigKanji> allKanji = new ArrayList<>();
        String orderBy =  COLUMN_ID + " ASC";
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_HEISIG_KANJI,
                allColumns, null, null, null, null, orderBy);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            HeisigKanji kanji = cursorToKanji(cursor);
            allKanji.add(kanji);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return allKanji;
    }

    public HeisigKanji getKanjiFor(int id)
    {
        HeisigKanji theKanji;
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_HEISIG_KANJI,
                allColumns, COLUMN_ID+" = "+id, null, null, null, null);

        cursor.moveToFirst();
        theKanji = cursorToKanji(cursor);
        cursor.close();
        return theKanji;
    }

    private HeisigKanji cursorToKanji(Cursor cursor) {
        return new HeisigKanji(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getInt(2));
    }
}
