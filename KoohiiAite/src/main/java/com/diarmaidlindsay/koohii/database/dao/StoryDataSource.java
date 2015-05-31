package com.diarmaidlindsay.koohii.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.diarmaidlindsay.koohii.database.DatabaseAssetHelper;
import com.diarmaidlindsay.koohii.model.Story;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for story table
 */
public class StoryDataSource extends CommonDataSource {

    private final String COLUMN_ID = "heisig_id";
    private final String COLUMN_STORY_TEXT = "story_text";
    private final String COLUMN_LAST_EDITED = "last_edited";
    private String[] allColumns = {COLUMN_ID, COLUMN_STORY_TEXT, COLUMN_LAST_EDITED};

    public StoryDataSource(Context context) {
        super(context);
    }

    public List<Story> getAllStories()
    {
        List<Story> allStories = new ArrayList<>();
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_STORY,
                allColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            Story kanji = cursorToStory(cursor);
            allStories.add(kanji);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return allStories;
    }

    public Story cursorToStory(Cursor cursor)
    {
        return new Story(cursor.getInt(0), cursor.getString(1), cursor.getLong(2));
    }
}
