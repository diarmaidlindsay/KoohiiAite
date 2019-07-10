package tech.diarmaid.koohiiaite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Represent an entry in the heisig_to_primitive table
 */
@Entity(tableName = "heisig_to_primitive")
data class HeisigToPrimitive(@PrimaryKey val id: Int,
                             @ColumnInfo(name = "heisig_id") val heisigId: Int,
                             @ColumnInfo(name = "primitive_id") val primitiveId: Int) {
    companion object {

        fun getPrimitiveIdsForHeisigId(heisigToPrimitiveList: List<HeisigToPrimitive>, heisigId: Int): List<Int> {
            val ids = ArrayList<Int>()

            for (htp in heisigToPrimitiveList) {
                if (htp.heisigId == heisigId) {
                    ids.add(htp.primitiveId)
                }
            }

            return ids
        }
    }
}
