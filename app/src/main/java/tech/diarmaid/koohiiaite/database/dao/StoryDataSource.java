package tech.diarmaid.koohiiaite.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper;
import tech.diarmaid.koohiiaite.model.Story;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * DAO for story table
 */
public class StoryDataSource extends CommonDataSource {

    private final String COLUMN_ID = "id";
    private final String COLUMN_STORY_TEXT = "story_text";
    private final String COLUMN_LAST_EDITED = "last_edited";
    private String[] allColumns = {COLUMN_ID, COLUMN_STORY_TEXT, COLUMN_LAST_EDITED};

    public StoryDataSource(Context context) {
        super(context);
    }

    public List<Story> getAllStories() {
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

    /**
     * In the Kanji list view we don't have to display the stories.
     * We just need to know which kanji have stories written or not.
     * List size should be equal to total i
     */
    public List<Boolean> getStoryFlags(int size) {
        List<Boolean> storyFlags = new ArrayList<>(Collections.nCopies(size, false));
        List<Story> allStories = getAllStories();

        for (Story story : allStories) {
            int index = story.getHeisig_id() - 1;
            storyFlags.set(index, true);
        }

        return storyFlags;
    }

    /**
     * Returns Story given an id from heisig_kanji table.
     * This is straightforward since both tables have 1 to 1 relationship
     * on their pks.
     *
     * @param id heisig_kanji id
     * @return null if no results found
     */
    public Story getStoryForHeisigKanjiId(int id) {
        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_STORY,
                allColumns, COLUMN_ID + " = " + id, null, null, null, null);

        if (cursor.getCount() == 0) {
            cursor.close();
            return null;
        }

        cursor.moveToFirst();
        Story story = cursorToStory(cursor);
        cursor.close();
        return story;
    }

    private Story cursorToStory(Cursor cursor) {
        return new Story(cursor.getInt(0), cursor.getString(1), cursor.getLong(2));
    }

    /**
     * Returns true if insertion successful
     */
    public boolean insertStory(int heisigId, String story, long lastEdited) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, heisigId);
        values.put(COLUMN_STORY_TEXT, story);
        values.put(COLUMN_LAST_EDITED, lastEdited);
        //-1 is failed insertion, so return true if not -1
        return database.insert(DatabaseAssetHelper.TABLE_STORY, null, values) != -1;
    }

    /**
     * returns true if insertion successful
     */
    public boolean insertStories(List<Story> stories) {
        boolean success = true;
        for (Story story : stories) {
            //attempt to update existing story, if that fails, insert instead
            if (!updateStory(story.getHeisig_id(), story.getStory_text(), story.getLast_edited())) {
                if (!insertStory(story.getHeisig_id(), story.getStory_text(), story.getLast_edited())) {
                    success = false;
                }
            }
        }

        return success;
    }

    /**
     * Return true if update successful, ie, there was an existing story
     */
    public boolean updateStory(int heisigId, String storyText, long lastEdited) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_STORY_TEXT, storyText);
        values.put(COLUMN_LAST_EDITED, lastEdited);

        // Which row to update, based on the ID
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(heisigId)};

        //0 means no rows affected, so return true if not 0
        return database.update(
                DatabaseAssetHelper.TABLE_STORY,
                values,
                selection,
                selectionArgs) != 0;
    }
}
