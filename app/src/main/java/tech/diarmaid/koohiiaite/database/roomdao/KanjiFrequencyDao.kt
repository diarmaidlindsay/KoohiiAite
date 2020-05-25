package tech.diarmaid.koohiiaite.database.roomdao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.database.entity.KanjiFrequency

@Dao
interface KanjiFrequencyDao {
    @Query("SELECT * FROM kanji_frequency WHERE heisig_id is :heisigId limit 1")
    fun getFrequencyFor(heisigId: Int): List<KanjiFrequency>
}