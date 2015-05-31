package com.diarmaidlindsay.koohii.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Use SQLiteAssetHelper to handle the initial copying of the database
 * when application is first loaded
 */
public class DatabaseAssetHelper extends SQLiteAssetHelper {
    private static final String DATABASE_NAME = "koohii.sqlite3.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_KANJI = "kanji";
    public static final String TABLE_STORY = "story";

    public DatabaseAssetHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

}
