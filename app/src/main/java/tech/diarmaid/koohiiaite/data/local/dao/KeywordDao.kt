package tech.diarmaid.koohiiaite.data.local.dao

import androidx.room.Dao
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.diarmaid.koohiiaite.data.local.entity.KeywordEntity

@Dao
interface KeywordDao {

    @Query("SELECT * FROM keyword ORDER BY heisig_id")
    fun observeAll(): Flow<List<KeywordEntity>>

    @Query("SELECT * FROM keyword WHERE heisig_id = :heisigId LIMIT 1")
    suspend fun getById(heisigId: Int): KeywordEntity?

    @Query("SELECT * FROM keyword WHERE keyword_text = :text LIMIT 1")
    suspend fun getByText(text: String): KeywordEntity?

    @Query("SELECT * FROM keyword WHERE keyword_text LIKE :text LIMIT 1")
    suspend fun getStartingWith(text: String): KeywordEntity?

    @Query("SELECT * FROM keyword ORDER BY heisig_id")
    suspend fun getAll(): List<KeywordEntity>
}
