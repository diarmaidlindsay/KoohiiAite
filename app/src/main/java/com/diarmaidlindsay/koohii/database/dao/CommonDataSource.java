package com.diarmaidlindsay.koohii.database.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.diarmaidlindsay.koohii.database.DatabaseAssetHelper;

import java.sql.SQLException;

/**
 * Common DAO methods
 */
public abstract class CommonDataSource {
    SQLiteDatabase database;
    private final DatabaseAssetHelper dbHelper;

    CommonDataSource(Context context) {
        dbHelper = DatabaseAssetHelper.getInstance(context);
    }

    public void open()
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }
}
