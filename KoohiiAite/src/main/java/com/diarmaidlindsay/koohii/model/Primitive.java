package com.diarmaidlindsay.koohii.model;

/**
 * Represent an entry in the primitive table
 */
public class Primitive {
    private int id;
    private String primitiveText;

    public Primitive(int id, String primitiveText) {
        this.id = id;
        this.primitiveText = primitiveText;
    }

    public int getId() {
        return id;
    }

    public String getPrimitiveText() {
        return primitiveText;
    }
}
