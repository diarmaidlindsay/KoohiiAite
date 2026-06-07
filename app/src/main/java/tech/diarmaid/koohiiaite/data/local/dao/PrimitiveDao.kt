package tech.diarmaid.koohiiaite.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.diarmaid.koohiiaite.data.local.entity.PrimitiveEntity

@Dao
interface PrimitiveDao {

    @Query("SELECT * FROM primitive ORDER BY id")
    fun observeAll(): Flow<List<PrimitiveEntity>>

    @Query("SELECT * FROM primitive ORDER BY id")
    suspend fun getAll(): List<PrimitiveEntity>
}
