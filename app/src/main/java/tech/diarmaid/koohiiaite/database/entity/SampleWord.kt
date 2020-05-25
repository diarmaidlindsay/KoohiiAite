package tech.diarmaid.koohiiaite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represent an entry in the sample_word table
 */
@Entity(tableName = "sample_words",
        foreignKeys = [ForeignKey(entity = HeisigKanji::class, parentColumns = ["id"], childColumns = ["heisig_id"])])
data class SampleWord(@PrimaryKey val id: Int,
                      @ColumnInfo(name = "heisig_id") val heisigId: Int,
                      @ColumnInfo(name = "kanji_word") val kanjiWord: String,
                      @ColumnInfo(name = "hiragana_reading") val hiraganaReading: String,
                      @ColumnInfo(name = "english_meaning") val englishMeaning: String,
                      val category: String?,
                      val frequency: Int)
