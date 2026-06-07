package tech.diarmaid.koohiiaite.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "keyword",
    primaryKeys = ["heisig_id"],
    foreignKeys = [
        ForeignKey(
            entity = HeisigKanjiEntity::class,
            parentColumns = ["id"],
            childColumns = ["heisig_id"]
        )
    ],
    indices = [Index("heisig_id")]
)
data class KeywordEntity(
    @ColumnInfo(name = "heisig_id")
    val heisigId: Int,
    @ColumnInfo(name = "keyword_text")
    val keywordText: String
)
