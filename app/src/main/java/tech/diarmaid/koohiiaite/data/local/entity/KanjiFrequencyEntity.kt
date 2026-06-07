package tech.diarmaid.koohiiaite.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "kanji_frequency",
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
data class KanjiFrequencyEntity(
    @ColumnInfo(name = "heisig_id")
    val heisigId: Int,
    val frequency: Int
)
