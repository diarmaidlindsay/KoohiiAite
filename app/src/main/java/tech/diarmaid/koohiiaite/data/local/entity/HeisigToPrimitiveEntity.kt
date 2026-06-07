package tech.diarmaid.koohiiaite.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "heisig_to_primitive",
    foreignKeys = [
        ForeignKey(
            entity = HeisigKanjiEntity::class,
            parentColumns = ["id"],
            childColumns = ["heisig_id"]
        ),
        ForeignKey(
            entity = PrimitiveEntity::class,
            parentColumns = ["id"],
            childColumns = ["primitive_id"]
        )
    ],
    indices = [
        Index("heisig_id"),
        Index("primitive_id")
    ]
)
data class HeisigToPrimitiveEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "heisig_id")
    val heisigId: Int,
    @ColumnInfo(name = "primitive_id")
    val primitiveId: Int
)
