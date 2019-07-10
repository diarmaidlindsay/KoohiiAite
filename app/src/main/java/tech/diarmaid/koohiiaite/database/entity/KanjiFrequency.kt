package tech.diarmaid.koohiiaite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "kanji_frequency")
data class KanjiFrequency(@PrimaryKey @ColumnInfo(name = "heisig_id") var heisigId: Int, var frequency: Int)