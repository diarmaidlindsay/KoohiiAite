package tech.diarmaid.koohiiaite.database.roomdao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.database.entity.Keyword

@Dao
interface KeywordDao {
    @Query("SELECT * FROM keyword")
    fun allKeywords(): List<Keyword>

    @Query("SELECT * FROM keyword where heisig_id = :heisigId limit 1")
    fun getKeywordFor(heisigId: Int): Keyword?

    @Query("SELECT * FROM keyword where keyword_text = :keywordText limit 1")
    fun getKeywordMatching(keywordText: String): Keyword?

    @Query("SELECT * FROM keyword where keyword_text LIKE :keywordText limit 1")
    fun getKeywordStartingWith(keywordText: String): Keyword?
}