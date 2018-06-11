package tech.diarmaid.koohiiaite.database.dao

import android.content.Context
import android.database.Cursor
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper
import tech.diarmaid.koohiiaite.model.Primitive
import java.util.*

/**
 * DAO for Primitives table
 */
class PrimitiveDataSource(context: Context) : CommonDataSource(context) {

    private val columnId = "id"
    private val columnText = "primitive_text"
    private val allColumns = arrayOf(columnId, columnText)

    val allPrimitives: List<Primitive>
        get() {
            val primitiveList = ArrayList<Primitive>()

            val cursor = database.query(DatabaseAssetHelper.TABLE_PRIMITIVE,
                    allColumns, null, null, null, null, null)
            cursor.moveToFirst()
            while (!cursor.isAfterLast) {
                primitiveList.add(cursorToPrimitive(cursor))
                cursor.moveToNext()
            }
            cursor.close()

            return primitiveList
        }

    private fun cursorToPrimitive(cursor: Cursor): Primitive {
        return Primitive(
                cursor.getInt(0),
                cursor.getString(1)
        )
    }
}
