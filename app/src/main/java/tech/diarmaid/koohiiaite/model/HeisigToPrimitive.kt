package tech.diarmaid.koohiiaite.model

import java.util.*

/**
 * Represent an entry in the heisig_to_primitive table
 */
class HeisigToPrimitive(val id: Int, val heisigId: Int, val primitiveId: Int) {
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
