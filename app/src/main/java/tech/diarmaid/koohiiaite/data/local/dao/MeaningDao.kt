package tech.diarmaid.koohiiaite.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.data.local.entity.MeaningEntity

@Dao
interface MeaningDao {

    @Query("SELECT * FROM meaning WHERE heisig_id = :heisigId")
    suspend fun getByHeisigId(heisigId: Int): List<MeaningEntity>
}
