package tech.diarmaid.koohiiaite.model;

import java.util.ArrayList;
import java.util.Collections;
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

    public HeisigKanji() {
        this.id = 0;
        this.kanji = "";
        this.joyo = false;
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

    /**
     * They are stored 1 indexed in the database.
     * This should be used for display purposes.
     *
     * If they are to be used for indexing other tables should
     * get the 0 Indexed list of ids instead.
     */
    public static String[] getIds1Indexed(List<HeisigKanji> list)
    {
        List<String> ids = new ArrayList<>();

        for(HeisigKanji hk : list)
        {
            ids.add(String.valueOf(hk.getId()));
        }

        return ids.toArray(new String[ids.size()]);
    }

    /**
     * For indexing other collections, using the heisig_id as the index
     */
    public static String[] getIds0Indexed(List<HeisigKanji> list)
    {
        List<String> ids = new ArrayList<>();

        for(HeisigKanji hk : list)
        {
            ids.add(String.valueOf(hk.getId() - 1));
        }

        return ids.toArray(new String[ids.size()]);
    }

    public static List<HeisigKanji> getHeisigKanjiMatchingIds(List<Integer> ids, List<HeisigKanji> masterList)
    {
        List<HeisigKanji> filteredList = new ArrayList<>();
        Collections.sort(ids);

        for(Integer id : ids)
        {
            //convert to 0 notation when referencing java array
            filteredList.add(masterList.get(id-1));
        }

        return filteredList;
    }

    /**
     * Return 4 digit Heisig Frame number for display
     */
    public static String getHeisigIdAsString(int heisigId) {
        String prefixZeros = "";

        if (heisigId < 1000) {
            prefixZeros += "0";
            if (heisigId < 100) {
                prefixZeros += "0";
                if (heisigId < 10) {
                    prefixZeros += "0";
                }
            }
        }

        return prefixZeros + heisigId;
    }
}
