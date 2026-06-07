package tech.diarmaid.koohiiaite.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import tech.diarmaid.koohiiaite.data.local.entity.UserKeywordEntity

@Dao
interface UserKeywordDao {

    @Query("SELECT * FROM user_keyword ORDER BY heisig_id")
    fun observeAll(): Flow<List<UserKeywordEntity>>

    @Query("SELECT * FROM user_keyword WHERE heisig_id = :heisigId LIMIT 1")
    suspend fun getById(heisigId: Int): UserKeywordEntity?

    @Query("SELECT * FROM user_keyword WHERE keyword_text = :text LIMIT 1")
    suspend fun getByText(text: String): UserKeywordEntity?

    @Query("SELECT * FROM user_keyword WHERE keyword_text LIKE :text LIMIT 1")
    suspend fun getStartingWith(text: String): UserKeywordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(keyword: UserKeywordEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(keywords: List<UserKeywordEntity>)

    @Query("DELETE FROM user_keyword WHERE heisig_id = :heisigId")
    suspend fun deleteById(heisigId: Int): Int

    @Query("SELECT * FROM user_keyword ORDER BY heisig_id")
    suspend fun getAll(): List<UserKeywordEntity>
}
