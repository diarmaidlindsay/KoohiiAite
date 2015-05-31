package com.diarmaidlindsay.koohii.model;

/**
 * Represent an entry in the Story table
 */
public class Story {
    int heisig_id;
    String story_text;
    long last_edited; //unix time

    /**
     * Use when reading existing story from the database
     */
    public Story(int heisig_id, String story_text, long last_edited) {
        this.heisig_id = heisig_id;
        this.story_text = story_text;
        this.last_edited = last_edited;
    }

    /**
     * Use when creating adding or updating story
     */
    public Story(int heisig_id, String story_text) {
        this.heisig_id = heisig_id;
        this.story_text = story_text;
        this.last_edited = System.currentTimeMillis() / 1000L; //convert to Unix time;
    }

    public int getHeisig_id() {
        return heisig_id;
    }

    public void setHeisig_id(int heisig_id) {
        this.heisig_id = heisig_id;
    }

    public String getStory_text() {
        return story_text;
    }

    public void setStory_text(String story_text) {
        this.story_text = story_text;
    }

    public long getLast_edited() {
        return last_edited;
    }

    public void setLast_edited(long last_edited) {
        this.last_edited = last_edited;
    }
}
