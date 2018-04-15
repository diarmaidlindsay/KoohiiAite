package tech.diarmaid.koohiiaite.database.dao;

import android.content.Context;
import android.database.Cursor;
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper;
import tech.diarmaid.koohiiaite.model.SampleWord;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the sample_word table
 */
public class SampleWordDataSource extends CommonDataSource {

    private final String COLUMN_ID = "id";
    private final String COLUMN_HEISIG_ID = "heisig_id";
    private final String COLUMN_KANJI_WORD = "kanji_word";
    private final String COLUMN_HIRAGANA_MEANING = "hiragana_reading";
    private final String COLUMN_ENGLISH_MEANING = "english_meaning";
    private final String COLUMN_CATEGORY = "category";
    private final String COLUMN_FREQUENCY = "frequency";
    private String[] allColumns =
            {COLUMN_ID, COLUMN_HEISIG_ID, COLUMN_KANJI_WORD, COLUMN_HIRAGANA_MEANING,
                    COLUMN_ENGLISH_MEANING, COLUMN_CATEGORY, COLUMN_FREQUENCY};

    public SampleWordDataSource(Context context) {
        super(context);
    }

    public List<SampleWord> getSampleWordsFor(int heisigId)
    {
        List<SampleWord> sampleWords = new ArrayList<>();

        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_SAMPLE_WORD,
                allColumns, COLUMN_HEISIG_ID + " = " + heisigId, null, null, null, null);

        if(cursor.getCount() == 0)
        {
            cursor.close();
            return sampleWords;
        }

        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            sampleWords.add(cursorToSampleWord(cursor));
            cursor.moveToNext();
        }

        cursor.close();

        return sampleWords;
    }

    private SampleWord cursorToSampleWord(Cursor cursor)
    {
        return new SampleWord(cursor.getInt(0), cursor.getInt(1), cursor.getString(2), cursor.getString(3),
                cursor.getString(4), cursor.getString(5), cursor.getInt(6));
    }
}
