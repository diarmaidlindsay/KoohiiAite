package tech.diarmaid.koohiiaite.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import tech.diarmaid.koohiiaite.data.local.entity.StoryEntity

@Dao
interface StoryDao {

    @Query("SELECT * FROM story ORDER BY id")
    suspend fun getAll(): List<StoryEntity>

    @Query("SELECT * FROM story WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): StoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(stories: List<StoryEntity>)
}
