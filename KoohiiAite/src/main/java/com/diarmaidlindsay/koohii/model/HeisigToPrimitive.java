package com.diarmaidlindsay.koohii.model;

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
}
