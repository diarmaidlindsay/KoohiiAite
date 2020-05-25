package tech.diarmaid.koohiiaite.database.entity

import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represent an entry in the reading table
 */
@Entity(tableName = "reading",
        foreignKeys = [ForeignKey(entity = HeisigKanji::class, parentColumns = ["id"], childColumns = ["heisig_id"])])
data class Reading(@PrimaryKey val id: Int,
                   @ColumnInfo(name = "heisig_id") val heisigId: Int,
                   @ColumnInfo(name = "reading_text", defaultValue = "") @NonNull val readingText: String?,
                   val type: Int = 0
)
