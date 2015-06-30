package com.diarmaidlindsay.koohii.model;

import java.util.List;

/**
 *  Represent an entry in the keyword and user_keyword table
 */
public class Keyword {
    private int heisigId;
    private String keywordText;

    public Keyword(int heisigId, String keywordText) {
        this.heisigId = heisigId;
        this.keywordText = keywordText;
    }

    public int getHeisigId() {
        return heisigId;
    }

    public String getKeywordText() {
        return keywordText;
    }

    public void setKeywordText(String keywordText) {
        this.keywordText = keywordText;
    }

    public static String getTextForId(List<Keyword> list, int heisigId)
    {
        for(Keyword kw : list)
        {
            if (kw.getHeisigId() == heisigId)
            {
                return kw.getKeywordText();
            }
        }

        return "NOT FOUND";
    }
}
