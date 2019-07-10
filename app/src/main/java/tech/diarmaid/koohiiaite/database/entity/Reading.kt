package tech.diarmaid.koohiiaite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represent an entry in the reading table
 */
@Entity(tableName = "reading")
data class Reading(@PrimaryKey val id: Int,
                   @ColumnInfo(name = "heisig_id") val heisigId: Int,
                   @ColumnInfo(name = "reading_text") val readingText: String,
                   val type: Int = 0
)
