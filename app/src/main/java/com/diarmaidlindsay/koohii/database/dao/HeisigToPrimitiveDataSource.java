package com.diarmaidlindsay.koohii.database.dao;

import android.content.Context;
import android.database.Cursor;
import com.diarmaidlindsay.koohii.database.DatabaseAssetHelper;
import com.diarmaidlindsay.koohii.model.HeisigToPrimitive;

import java.util.ArrayList;
import java.util.List;

/**
 * DAO for HeisigToPrimitive Table
 */
public class HeisigToPrimitiveDataSource extends CommonDataSource {

    private final String COLUMN_ID = "id";
    private final String COLUMN_HEISIG_ID = "heisig_id";
    private final String COLUMN_PRIMITIVE_ID = "primitive_id";
    private String[] allColumns = {COLUMN_ID, COLUMN_HEISIG_ID, COLUMN_PRIMITIVE_ID};

    public HeisigToPrimitiveDataSource(Context context) {
        super(context);
    }

    public List<HeisigToPrimitive> getHeisigToPrimitiveMatching(String[] heisigIds)
    {
        List<HeisigToPrimitive> heisigToPrimitiveList = new ArrayList<>();

        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_HEISIG_PRIMITIVE,
                allColumns, COLUMN_HEISIG_ID + " IN ("+generateIdList(heisigIds)+")",
                null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            heisigToPrimitiveList.add(cursorToHeisigToPrimitive(cursor));
            cursor.moveToNext();
        }
        cursor.close();

        return heisigToPrimitiveList;
    }

    public List<Integer> getHeisigIdsMatching(Integer[] primitiveIds)
    {
        List<Integer> heisigIdList = new ArrayList<>();

        Cursor cursor = database.query(DatabaseAssetHelper.TABLE_HEISIG_PRIMITIVE,
                new String[]{COLUMN_HEISIG_ID}, COLUMN_PRIMITIVE_ID + " IN ("+generateIdList(primitiveIds)+")",
                null, null, null, null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast())
        {
            heisigIdList.add(cursor.getInt(0));
            cursor.moveToNext();
        }
        cursor.close();

        return heisigIdList;
    }

    private String generateIdList(Object[] ids)
    {
        StringBuilder sb = new StringBuilder();

        for(int i=0; i < ids.length; i++)
        {
            String id = ids[i].toString();
            sb.append(id);
            if(i < ids.length - 1) {
                sb.append(", ");
            }
        }

        return sb.toString();
    }

    private HeisigToPrimitive cursorToHeisigToPrimitive(Cursor cursor) {
        return new HeisigToPrimitive(cursor.getInt(0), cursor.getInt(1), cursor.getInt(2));
    }
}
