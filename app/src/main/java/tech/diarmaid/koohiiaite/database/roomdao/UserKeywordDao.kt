package tech.diarmaid.koohiiaite.database.roomdao

import androidx.room.*
import tech.diarmaid.koohiiaite.database.entity.UserKeyword

@Dao
interface UserKeywordDao {
    @Query("SELECT * FROM user_keyword")
    fun allUserKeywords(): List<UserKeyword>

    @Query("SELECT * FROM user_keyword where heisig_id = :heisigId limit 1")
    fun getKeywordFor(heisigId: Int): UserKeyword?

    @Query("SELECT * FROM user_keyword where keyword_text = :keywordText limit 1")
    fun getKeywordMatching(keywordText: String): UserKeyword?

    @Query("SELECT * FROM user_keyword where keyword_text LIKE :keywordText limit 1")
    fun getKeywordStartingWith(keywordText: String): UserKeyword?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertKeywords(vararg keywords: UserKeyword)

    @Update
    fun updateKeyword(keyword: UserKeyword)

    @Query("DELETE FROM user_keyword WHERE heisig_id = :heisigId")
    fun deleteKeyword(heisigId: Int): Int
}