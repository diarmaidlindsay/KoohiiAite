package com.diarmaidlindsay.koohii.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Use SQLiteAssetHelper to handle the initial copying of the database
 * when application is first loaded
 */
public class DatabaseAssetHelper extends SQLiteAssetHelper {
    private static DatabaseAssetHelper sInstance;

    private static final String DATABASE_NAME = "koohii.sqlite3.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_HEISIG_KANJI = "heisig_kanji";
    public static final String TABLE_HEISIG_PRIMITIVE = "heisig_to_primitive";
    public static final String TABLE_MEANING = "meaning";
    public static final String TABLE_PRIMITIVE = "primitive";
    public static final String TABLE_READING = "reading";
    public static final String TABLE_STORY = "story";
    public static final String TABLE_KEYWORD = "keyword";

    public static synchronized DatabaseAssetHelper getInstance(Context context) {

        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = new DatabaseAssetHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    private DatabaseAssetHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }

}
