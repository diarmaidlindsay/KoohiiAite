package tech.diarmaid.koohiiaite.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represent an entry in the Story table
 */
@Entity(tableName = "story")
data class Story(
        @PrimaryKey @ColumnInfo(name = "id") var heisigId: Int = 0,
        @ColumnInfo(name = "story_text") var storyText: String = "",
        @ColumnInfo(name = "last_edited") var lastEdited: Long = 0) {
//        private set //primary key also foreign key to HeisigKanji table

    /**
     * Use when creating adding or updating story
     */
    constructor(heisig_id: Int, story_text: String) : this() {
        this.heisigId = heisig_id
        this.storyText = story_text
        this.lastEdited = System.currentTimeMillis() / 1000L //convert to Unix time;
    }
}
