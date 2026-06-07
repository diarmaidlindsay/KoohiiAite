package tech.diarmaid.koohiiaite.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.data.local.entity.ReadingEntity

@Dao
interface ReadingDao {

    @Query("SELECT * FROM reading WHERE heisig_id = :heisigId AND type = :type LIMIT 1")
    suspend fun getByHeisigIdAndType(heisigId: Int, type: Int): ReadingEntity?
}
