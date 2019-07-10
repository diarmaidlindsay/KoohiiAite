package tech.diarmaid.koohiiaite.database.roomdao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.database.entity.Reading

@Dao
interface ReadingDao {
    @Query("SELECT * FROM reading WHERE heisig_id = :heisigId AND type = :type")
    fun getMeaningForHeisigKanjiId(heisigId: Int, type: Int): Reading?
}