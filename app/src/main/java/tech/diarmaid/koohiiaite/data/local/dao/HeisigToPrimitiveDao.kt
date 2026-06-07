package tech.diarmaid.koohiiaite.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.diarmaid.koohiiaite.data.local.entity.HeisigToPrimitiveEntity

@Dao
interface HeisigToPrimitiveDao {

    @Query("SELECT * FROM heisig_to_primitive ORDER BY id")
    fun observeAll(): Flow<List<HeisigToPrimitiveEntity>>

    @Query("SELECT heisig_id FROM heisig_to_primitive WHERE primitive_id IN (:primitiveIds)")
    suspend fun getHeisigIdsForPrimitives(primitiveIds: List<Int>): List<Int>

    @Query("SELECT * FROM heisig_to_primitive ORDER BY id")
    suspend fun getAll(): List<HeisigToPrimitiveEntity>
}
