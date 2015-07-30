package com.diarmaidlindsay.koohii.model;

/**
 * Represent an entry in an imported my_stories.csv File
 */
public class CSVEntry {
    public String id;
    public String kanji;
    public String keyword;
    public String publicFlag;
    public String lastEdited;
    public String story;

    public CSVEntry(String id, String kanji, String keyword, String publicFlag, String lastEdited, String story) {
        this.id = id;
        this.kanji = kanji;
        this.keyword = keyword.replaceAll("^\"|\"$", ""); //trim enclosing quotations from my_stories.csv
        this.publicFlag = publicFlag;
        this.lastEdited = lastEdited;
        this.story = story.replaceAll("^\"|\"$", ""); //trim enclosing quotations from my_stories.csv
    }
}
