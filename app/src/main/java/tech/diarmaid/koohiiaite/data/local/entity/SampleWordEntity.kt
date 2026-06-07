package tech.diarmaid.koohiiaite.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "sample_words",
    foreignKeys = [
        ForeignKey(
            entity = HeisigKanjiEntity::class,
            parentColumns = ["id"],
            childColumns = ["heisig_id"]
        )
    ],
    indices = [Index("heisig_id")]
)
data class SampleWordEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "heisig_id")
    val heisigId: Int,
    @ColumnInfo(name = "kanji_word")
    val kanjiWord: String,
    @ColumnInfo(name = "hiragana_reading")
    val hiraganaReading: String,
    @ColumnInfo(name = "english_meaning")
    val englishMeaning: String,
    val category: String?,
    val frequency: Int
)
