package tech.diarmaid.koohiiaite.database.roomdao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tech.diarmaid.koohiiaite.database.entity.Story
import java.util.*

@Dao
abstract class StoryDao {
    @Query("SELECT * FROM story")
    abstract fun allStories(): List<Story>

    fun getStoryFlags(size: Int): List<Boolean> {
        val storyFlags = ArrayList(Collections.nCopies(size, false))

        for (story in allStories()) {
            val index = story.heisigId - 1
            storyFlags[index] = true
        }

        return storyFlags
    }

    @Query("SELECT * FROM story WHERE id = :id")
    abstract fun getStoryForHeisigKanjiId(id: Int): Story?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insertStories(vararg stories: Story)
}