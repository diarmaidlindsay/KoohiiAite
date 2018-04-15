package tech.diarmaid.koohiiaite.database.dao;

import android.content.Context;
import android.database.Cursor;
import tech.diarmaid.koohiiaite.database.DatabaseAssetHelper;
import tech.diarmaid.koohiiaite.model.Primitive;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Primitives table
 */
public class PrimitiveDataSource extends CommonDataSource {

    private final String COLUMN_ID = "id";
    private final String COLUMN_TEXT = "primitive_text";
    private String[] allColumns = {COLUMN_ID, COLUMN_TEXT};

    public PrimitiveDataSource(Context context) {
        super(context);
    }

    public List<Primitive> getAllPrimitives() {
        List<Primitive> primitiveList = new ArrayList<>();

        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_PRIMITIVE,
                allColumns, null, null, null, null, null);
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            primitiveList.add(cursorToPrimitive(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return primitiveList;
    }

    private Primitive cursorToPrimitive(Cursor cursor) {
        return new Primitive(
                cursor.getInt(0),
                cursor.getString(1)
        );
    }
}
