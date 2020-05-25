package tech.diarmaid.koohiiaite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * Represent an entry in the meaning table
 */
@Entity(tableName = "meaning",
        foreignKeys = [ForeignKey(entity = HeisigKanji::class, parentColumns = ["id"], childColumns = ["heisig_id"])])
data class Meaning(@PrimaryKey val id: Int,
                   @ColumnInfo(name = "heisig_id") val heisigId: Int,
                   @ColumnInfo(name = "meaning_text") val meaningText: String)
