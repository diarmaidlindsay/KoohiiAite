package tech.diarmaid.koohiiaite.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.data.local.entity.KanjiFrequencyEntity

@Dao
interface KanjiFrequencyDao {

    @Query("SELECT * FROM kanji_frequency WHERE heisig_id = :heisigId LIMIT 1")
    suspend fun getByHeisigId(heisigId: Int): KanjiFrequencyEntity?
}
