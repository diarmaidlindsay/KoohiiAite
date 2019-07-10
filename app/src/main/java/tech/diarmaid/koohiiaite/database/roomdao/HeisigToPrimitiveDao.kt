package tech.diarmaid.koohiiaite.database.roomdao

import androidx.room.Dao
import androidx.room.Query
import tech.diarmaid.koohiiaite.database.entity.HeisigToPrimitive

@Dao
interface HeisigToPrimitiveDao {
    @Query("SELECT * FROM heisig_to_primitive WHERE heisig_id IN(:heisigIds)")
    fun getHeisigToPrimitiveMatching(heisigIds: List<String>): List<HeisigToPrimitive>

    @Query("SELECT heisig_id FROM heisig_to_primitive WHERE primitive_id IN(:primitiveIds)")
    fun getHeisigIdsMatching(primitiveIds: Array<Int>): List<Int>
}