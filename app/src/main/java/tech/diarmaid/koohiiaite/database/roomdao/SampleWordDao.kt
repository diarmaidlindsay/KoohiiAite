package tech.diarmaid.koohiiaite.database.roomdao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.database.entity.SampleWord

@Dao
interface SampleWordDao {
    @Query("SELECT * FROM sample_words WHERE heisig_id = :heisigId")
    fun getSampleWordsFor(heisigId: Int): List<SampleWord>
}