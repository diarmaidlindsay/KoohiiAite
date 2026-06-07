package tech.diarmaid.koohiiaite.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "primitive")
data class PrimitiveEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "primitive_text")
    val primitiveText: String
)
