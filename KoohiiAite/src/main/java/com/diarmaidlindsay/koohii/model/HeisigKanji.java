package com.diarmaidlindsay.koohii.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represent an entry in the heisig_kanji table
 */
public class HeisigKanji {
    private int id;
    private String kanji;
    private boolean joyo;

    public HeisigKanji(int id, String kanji, boolean joyo) {
        this.id = id;
        this.kanji = kanji;
        this.joyo = joyo;
    }

    public HeisigKanji(int id, String kanji, int joyo) {
        this.id = id;
        this.kanji = kanji;
        this.joyo = joyo!=0;
    }

    public int getId() {
        return id;
    }

    public String getKanji() {
        return kanji;
    }

    public int getJoyo()
    {
        return joyo ? 1 : 0;
    }

    public boolean isJoyo() {
        return joyo;
    }

    public static String[] getIds(List<HeisigKanji> list)
    {
        List<String> ids = new ArrayList<>();

        for(HeisigKanji hk : list)
        {
            ids.add(String.valueOf(hk.getId()));
        }

        return ids.toArray(new String[ids.size()]);
    }

    public static List<HeisigKanji> getObjects(List<Integer> ids, List<HeisigKanji> masterList)
    {
        List<HeisigKanji> filteredList = new ArrayList<>();

        for(Integer id : ids)
        {
            filteredList.add(masterList.get(id));
        }

        return filteredList;
    }
}
