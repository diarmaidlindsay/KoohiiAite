package tech.diarmaid.koohiiaite.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.diarmaid.koohiiaite.data.local.entity.HeisigKanjiEntity

@Dao
interface HeisigKanjiDao {

    @Query("SELECT * FROM heisig_kanji ORDER BY id")
    fun observeAll(): Flow<List<HeisigKanjiEntity>>

    @Query("SELECT * FROM heisig_kanji WHERE id = :id LIMIT 1")
    suspend fun getById(id: Int): HeisigKanjiEntity?

    @Query("SELECT * FROM heisig_kanji WHERE kanji = :kanji LIMIT 1")
    suspend fun getByKanji(kanji: String): HeisigKanjiEntity?

    @Query("SELECT * FROM heisig_kanji ORDER BY id")
    suspend fun getAll(): List<HeisigKanjiEntity>
}
