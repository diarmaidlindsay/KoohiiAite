package tech.diarmaid.koohiiaite.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.data.local.entity.SampleWordEntity

@Dao
interface SampleWordDao {

    @Query("SELECT * FROM sample_words WHERE heisig_id = :heisigId ORDER BY frequency")
    suspend fun getByHeisigId(heisigId: Int): List<SampleWordEntity>
}
