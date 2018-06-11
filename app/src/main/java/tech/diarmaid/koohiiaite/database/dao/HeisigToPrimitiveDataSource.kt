package tech.diarmaid.koohiiaite.database.dao

import android.content.Context
import android.database.Cursor
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper
import tech.diarmaid.koohiiaite.model.HeisigToPrimitive
import java.util.*

/**
 * DAO for HeisigToPrimitive Table
 */
class HeisigToPrimitiveDataSource(context: Context) : CommonDataSource(context) {

    private val columnId = "id"
    private val columnHeisigId = "heisig_id"
    private val columnPrimitiveId = "primitive_id"
    private val allColumns = arrayOf(columnId, columnHeisigId, columnPrimitiveId)

    fun getHeisigToPrimitiveMatching(heisigIds: List<String>): List<HeisigToPrimitive> {
        val heisigToPrimitiveList = ArrayList<HeisigToPrimitive>()

        val cursor = database.query(DatabaseAssetHelper.TABLE_HEISIG_PRIMITIVE,
                allColumns, columnHeisigId + " IN (" + generateIdList(heisigIds) + ")", null, null, null, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            heisigToPrimitiveList.add(cursorToHeisigToPrimitive(cursor))
            cursor.moveToNext()
        }
        cursor.close()

        return heisigToPrimitiveList
    }

    fun getHeisigIdsMatching(primitiveIds: Array<Int>): List<Int> {
        val heisigIdList = ArrayList<Int>()
        val cursor = database.query(DatabaseAssetHelper.TABLE_HEISIG_PRIMITIVE,
                arrayOf(columnHeisigId), columnPrimitiveId + " IN (" + generateIdList(primitiveIds.map { it.toString() }) + ")", null, null, null, null)
        cursor.moveToFirst()
        while (!cursor.isAfterLast) {
            heisigIdList.add(cursor.getInt(0))
            cursor.moveToNext()
        }
        cursor.close()

        return heisigIdList
    }

    private fun generateIdList(ids: List<String>): String {
        val sb = StringBuilder()

        for (i in ids.indices) {
            val id = ids[i]
            sb.append(id)
            if (i < ids.size - 1) {
                sb.append(", ")
            }
        }

        return sb.toString()
    }

    private fun cursorToHeisigToPrimitive(cursor: Cursor): HeisigToPrimitive {
        return HeisigToPrimitive(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2))
    }
}
