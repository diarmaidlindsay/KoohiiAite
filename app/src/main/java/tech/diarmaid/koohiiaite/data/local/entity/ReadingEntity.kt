package tech.diarmaid.koohiiaite.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reading",
    foreignKeys = [
        ForeignKey(
            entity = HeisigKanjiEntity::class,
            parentColumns = ["id"],
            childColumns = ["heisig_id"]
        )
    ],
    indices = [Index("heisig_id")]
)
data class ReadingEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "heisig_id")
    val heisigId: Int,
    @ColumnInfo(name = "reading_text")
    val readingText: String?,
    val type: Int
)
