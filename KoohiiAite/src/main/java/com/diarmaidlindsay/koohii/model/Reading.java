package com.diarmaidlindsay.koohii.model;

/**
 * Represent an entry in the reading table
 */
public class Reading {
    private int id;
    private int heisigId;
    private String readingText;
    private int type; //0 = onyomi, 1 = kunyomi

    public Reading(int id, int heisigId, String readingText, int type) {
        this.id = id;
        this.heisigId = heisigId;
        this.readingText = readingText;
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public int getHeisigId() {
        return heisigId;
    }

    public String getReadingText() {
        return readingText;
    }

    public int getType() {
        return type;
    }
}
