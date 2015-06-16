package com.diarmaidlindsay.koohii.model;

/**
 * Represent an entry in the sample_word table
 */
public class SampleWord {
    int id;
    int heisigId;
    String kanjiWord;
    String hiraganaReading;
    String englishMeaning;
    String category;
    int frequency;

    public SampleWord(int id, int heisigId, String kanjiWord, String hiraganaReading, String englishMeaning, String category, int frequency) {
        this.id = id;
        this.heisigId = heisigId;
        this.kanjiWord = kanjiWord;
        this.hiraganaReading = hiraganaReading;
        this.englishMeaning = englishMeaning;
        this.category = category;
        this.frequency = frequency;
    }

    public int getId() {
        return id;
    }

    public int getHeisigId() {
        return heisigId;
    }

    public String getKanjiWord() {
        return kanjiWord;
    }

    public String getHiraganaReading() {
        return hiraganaReading;
    }

    public String getEnglishMeaning() {
        return englishMeaning;
    }

    public String getCategory() {
        return category;
    }

    public int getFrequency() {
        return frequency;
    }
}
