package com.diarmaidlindsay.koohii.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.diarmaidlindsay.koohii.database.DatabaseAssetHelper;

import java.util.HashMap;
import java.util.Map;

/**
 * DAO for kanji_frequency table
 */
public class FrequencyDataSource extends CommonDataSource {

    private final String COLUMN_ID = "heisig_id";
    private final String COLUMN_FREQUENCY = "frequency";
    private String[] allColumns = {COLUMN_ID, COLUMN_FREQUENCY};

    public FrequencyDataSource(Context context) {
        super(context);
    }

    public Map<Integer, Integer> getAllKanjiFrequency()
    {
        Map<Integer, Integer> allFrequency = new HashMap<>();

        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_FREQUENCY,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Frequency freq = cursorToFrequency(cursor);
            allFrequency.put(freq.id, freq.frequency);
            cursor.moveToNext();
        }
        cursor.close();

        return allFrequency;
    }

    public Integer getFrequencyFor(int heisigId)
    {
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_FREQUENCY,
                allColumns, COLUMN_ID + " = " + heisigId, null, null, null, null);

        if(cursor.getCount() == 0)
        {
            cursor.close();
            return -10; //should not happen!
        }

        cursor.moveToFirst();
        int frequency = cursor.getInt(1);
        cursor.close();

        return frequency;
    }

    public Frequency cursorToFrequency(Cursor cursor)
    {
        return new Frequency(cursor.getInt(0), cursor.getInt(1));
    }

    private class Frequency
    {
        int id;
        int frequency;

        public Frequency(int id, int frequency) {
            this.id = id;
            this.frequency = frequency;
        }
    }
}
