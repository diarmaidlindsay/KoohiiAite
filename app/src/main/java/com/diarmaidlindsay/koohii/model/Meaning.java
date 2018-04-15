package com.diarmaidlindsay.koohii.model;

/**
 * Represent an entry in the meaning table
 */
public class Meaning {
    private int id;
    private int heisigId;
    private String meaningText;

    public Meaning(int id, int heisigId, String meaningText) {
        this.id = id;
        this.heisigId = heisigId;
        this.meaningText = meaningText;
    }

    public int getId() {
        return id;
    }

    public int getHeisigId() {
        return heisigId;
    }

    public String getMeaningText() {
        return meaningText;
    }
}
