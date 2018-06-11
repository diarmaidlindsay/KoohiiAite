package tech.diarmaid.koohiiaite.model

/**
 * Represent an entry in the Story table
 */
class Story {
    var heisigId: Int = 0
        private set //primary key also foreign key to HeisigKanji table
    var storyText: String = ""
    var lastEdited: Long = 0 //unix time

    /**
     * Use when reading existing story from the database
     */
    constructor(heisig_id: Int, story_text: String, last_edited: Long) {
        this.heisigId = heisig_id
        this.storyText = story_text
        this.lastEdited = last_edited
    }

    /**
     * Use when creating adding or updating story
     */
    constructor(heisig_id: Int, story_text: String) {
        this.heisigId = heisig_id
        this.storyText = story_text
        this.lastEdited = System.currentTimeMillis() / 1000L //convert to Unix time;
    }
}
