package tech.diarmaid.koohiiaite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

/**
 * Represent an entry in the primitive table
 */
@Entity(tableName = "primitive")
data class Primitive(@PrimaryKey val id: Int,
                     @ColumnInfo(name = "primitive_text") val primitiveText: String) {
    companion object {

        /**
         * Given the complete ordered list of primitives, get the primitive text of
         * the ids which are in the HeisigToPrimitive objects
         */
        fun getPrimitiveText(primitives: List<Primitive>, primitiveIds: Set<Int>): List<String> {
            val primitiveText = ArrayList<String>()

            for (id in primitiveIds) {
                //resolve the primitive id to the actual text
                //convert to 0-based index from db 1-based
                primitiveText.add(primitives[id - 1].primitiveText)
            }

            return primitiveText
        }

        fun getPrimitiveIdsContaining(aPrimitiveTextQuery: String, primitives: List<Primitive>, ignoreCase: Boolean): List<Int> {
            var primitiveTextQuery = aPrimitiveTextQuery
            val primitiveIds = ArrayList<Int>()
            primitiveTextQuery = if (ignoreCase) primitiveTextQuery.toLowerCase() else primitiveTextQuery

            for (primitive in primitives) {
                val primitiveListText = if (ignoreCase) primitive.primitiveText.toLowerCase() else primitive.primitiveText
                if (primitiveListText.contains(primitiveTextQuery)) {
                    primitiveIds.add(primitive.id)
                }
            }

            return primitiveIds
        }

        fun getPrimitiveIdWhichMatches(aPrimitiveTextQuery: String, primitives: List<Primitive>, ignoreCase: Boolean): Int {
            var primitiveTextQuery = aPrimitiveTextQuery
            primitiveTextQuery = if (ignoreCase) primitiveTextQuery.toLowerCase() else primitiveTextQuery

            for (primitive in primitives) {
                val primitiveListText = if (ignoreCase) primitive.primitiveText.toLowerCase() else primitive.primitiveText
                if (primitiveListText == primitiveTextQuery) {
                    return primitive.id
                }
            }

            return -1
        }
    }
}
