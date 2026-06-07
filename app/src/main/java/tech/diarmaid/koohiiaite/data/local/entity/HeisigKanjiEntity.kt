package tech.diarmaid.koohiiaite.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "heisig_kanji")
data class HeisigKanjiEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,
    val kanji: String,
    val joyo: Boolean
)
