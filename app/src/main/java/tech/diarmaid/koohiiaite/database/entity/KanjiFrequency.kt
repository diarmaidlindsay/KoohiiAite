package tech.diarmaid.koohiiaite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "kanji_frequency",
        foreignKeys = [ForeignKey(entity = HeisigKanji::class, parentColumns = ["id"], childColumns = ["heisig_id"])])
data class KanjiFrequency(@PrimaryKey @ColumnInfo(name = "heisig_id") var heisigId: Int, var frequency: Int)