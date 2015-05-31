package com.diarmaidlindsay.koohii.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.diarmaidlindsay.koohii.database.DatabaseAssetHelper;
import com.diarmaidlindsay.koohii.model.Kanji;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for kanji table
 */
public class KanjiDataSource extends CommonDataSource {

    private final String COLUMN_ID = "heisig_id";
    private final String COLUMN_KEYWORD = "keyword";
    private final String COLUMN_KANJI = "kanji";
    private final String COLUMN_JOYO = "joyo";
    private String[] allColumns = {COLUMN_ID, COLUMN_KEYWORD, COLUMN_KANJI, COLUMN_JOYO};

    public KanjiDataSource(Context context) {
        super(context);
    }

    public List<Kanji> getAllKanji() {
        List<Kanji> allKanji = new ArrayList<>();
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_KANJI,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Kanji kanji = cursorToKanji(cursor);
            allKanji.add(kanji);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return allKanji;
    }

    private Kanji cursorToKanji(Cursor cursor) {
        return new Kanji(
                cursor.getInt(0),
                cursor.getString(1),
                cursor.getString(2),
                cursor.getInt(3));
    }
}
