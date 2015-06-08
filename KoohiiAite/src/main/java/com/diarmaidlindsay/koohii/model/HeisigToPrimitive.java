package com.diarmaidlindsay.koohii.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent an entry in the heisig_to_primitive table
 */
public class HeisigToPrimitive {
    private int id;
    private int heisigId;
    private int primitiveId;

    public HeisigToPrimitive(int id, int heisigId, int primitiveId) {
        this.id = id;
        this.heisigId = heisigId;
        this.primitiveId = primitiveId;
    }

    public int getId() {
        return id;
    }

    public int getHeisigId() {
        return heisigId;
    }

    public int getPrimitiveId() {
        return primitiveId;
    }

    public static List<Integer> getPrimitiveIdsForHeisigId(List<HeisigToPrimitive> heisigToPrimitiveList, int heisigId)
    {
        List<Integer> ids = new ArrayList<>();

        for(HeisigToPrimitive htp : heisigToPrimitiveList)
        {
            if(htp.getHeisigId() == heisigId)
            {
                ids.add(htp.getPrimitiveId());
            }
        }

        return ids;
    }
}
