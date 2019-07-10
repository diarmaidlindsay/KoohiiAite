package tech.diarmaid.koohiiaite.database.roomdao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.database.entity.HeisigKanji

@Dao
interface HeisigKanjiDao {
    @Query("SELECT * FROM heisig_kanji WHERE id is :theId limit 1")
    fun getKanjiFor(theId: Int): HeisigKanji

    @Query("SELECT * FROM heisig_kanji WHERE kanji is :theKanji limit 1")
    fun getHeisigFor(theKanji: String): HeisigKanji

    @Query("SELECT * FROM heisig_kanji")
    fun allKanji(): List<HeisigKanji>
}