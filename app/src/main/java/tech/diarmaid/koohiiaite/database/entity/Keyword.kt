package tech.diarmaid.koohiiaite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represent an entry in the keyword and user_keyword table
 */
@Entity(tableName = "keyword",
        foreignKeys = [ForeignKey(entity = HeisigKanji::class, parentColumns = ["id"], childColumns = ["heisig_id"])])
data class Keyword(
        @PrimaryKey @ColumnInfo(name = "heisig_id") val heisigId: Int,
        @ColumnInfo(name = "keyword_text") var keywordText: String)