package com.diarmaidlindsay.koohii.model;

/**
 * Represent an entry in the Kanji table
 */
public class Kanji {
    int heisig_id;
    String kanji;
    String keyword;
    int joyo;

    public Kanji(int heisig_id, String keyword, String kanji, int joyo) {
        this.heisig_id = heisig_id;
        this.kanji = kanji;
        this.keyword = keyword;
        this.joyo = joyo;
    }

    public int getHeisig_id() {
        return heisig_id;
    }

    public void setHeisig_id(int heisig_id) {
        this.heisig_id = heisig_id;
    }

    public String getKanji() {
        return kanji;
    }

    public void setKanji(String kanji) {
        this.kanji = kanji;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }

    public int getJoyo() {
        return joyo;
    }

    public void setJoyo(int joyo) {
        this.joyo = joyo;
    }
}
