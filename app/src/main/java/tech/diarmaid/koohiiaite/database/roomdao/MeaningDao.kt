package tech.diarmaid.koohiiaite.database.roomdao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.database.entity.Meaning

@Dao
interface MeaningDao {
    @Query("SELECT * FROM meaning WHERE heisig_id = :heisigId")
    fun getMeaningsForHeisigKanjiId(heisigId: Int): List<Meaning>
}