package tech.diarmaid.koohiiaite.database.dao

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper
import tech.diarmaid.koohiiaite.model.Story
import java.util.*

/**
 * DAO for story table
 */
class StoryDataSource(context: Context) : CommonDataSource(context) {

    private val columnId = "id"
    private val columnStoryText = "story_text"
    private val columnLastEdited = "last_edited"
    private val allColumns = arrayOf(columnId, columnStoryText, columnLastEdited)

    // make sure to close the cursor
    private val allStories: List<Story>
        get() {
            val allStories = ArrayList<Story>()
            val cursor = database.query(DatabaseAssetHelper.TABLE_STORY,
                    allColumns, null, null, null, null, null)

            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                val kanji = cursorToStory(cursor)
                allStories.add(kanji)
                cursor.moveToNext()
            }
            cursor.close()
            return allStories
        }

    /**
     * In the Kanji list view we don't have to display the stories.
     * We just need to know which kanji have stories written or not.
     * List size should be equal to total i
     */
    fun getStoryFlags(size: Int): List<Boolean> {
        val storyFlags = ArrayList(Collections.nCopies(size, false))
        val allStories = allStories

        for (story in allStories) {
            val index = story.heisigId - 1
            storyFlags[index] = true
        }

        return storyFlags
    }

    /**
     * Returns Story given an id from heisig_kanji table.
     * This is straightforward since both tables have 1 to 1 relationship
     * on their pks.
     *
     * @param id heisig_kanji id
     * @return null if no results found
     */
    fun getStoryForHeisigKanjiId(id: Int): Story? {
        val cursor = database.query(DatabaseAssetHelper.TABLE_STORY,
                allColumns, "$columnId = $id", null, null, null, null)

        if (cursor.count == 0) {
            cursor.close()
            return null
        }

        cursor.moveToFirst()
        val story = cursorToStory(cursor)
        cursor.close()
        return story
    }

    private fun cursorToStory(cursor: Cursor): Story {
        return Story(cursor.getInt(0), cursor.getString(1), cursor.getLong(2))
    }

    /**
     * Returns true if insertion successful
     */
    private fun insertStory(heisigId: Int, story: String, lastEdited: Long): Boolean {
        val values = ContentValues()
        values.put(columnId, heisigId)
        values.put(columnStoryText, story)
        values.put(columnLastEdited, lastEdited)
        //-1 is failed insertion, so return true if not -1
        return database.insert(DatabaseAssetHelper.TABLE_STORY, null, values) != -1L
    }

    /**
     * returns true if insertion successful
     */
    fun insertStories(stories: List<Story>): Boolean {
        var success = true
        for (story in stories) {
            //attempt to update existing story, if that fails, insert instead
            if (!updateStory(story.heisigId, story.storyText, story.lastEdited)) {
                if (!insertStory(story.heisigId, story.storyText, story.lastEdited)) {
                    success = false
                }
            }
        }

        return success
    }

    /**
     * Return true if update successful, ie, there was an existing story
     */
    private fun updateStory(heisigId: Int, storyText: String, lastEdited: Long): Boolean {
        val values = ContentValues()
        values.put(columnStoryText, storyText)
        values.put(columnLastEdited, lastEdited)

        // Which row to update, based on the ID
        val selection = "$columnId = ?"
        val selectionArgs = arrayOf(heisigId.toString())

        //0 means no rows affected, so return true if not 0
        return database.update(
                DatabaseAssetHelper.TABLE_STORY,
                values,
                selection,
                selectionArgs) != 0
    }
}
