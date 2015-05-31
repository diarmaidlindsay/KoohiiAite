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
    DatabaseAssetHelper dbHelper;

    public CommonDataSource(Context context) {
        dbHelper = new DatabaseAssetHelper(context);
    }

    public void open() throws SQLException
    {
        database = dbHelper.getWritableDatabase();
    }

    public void close()
    {
        dbHelper.close();
    }
}
