package tech.diarmaid.koohiiaite.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "story",
    primaryKeys = ["id"],
    foreignKeys = [
        ForeignKey(
            entity = HeisigKanjiEntity::class,
            parentColumns = ["id"],
            childColumns = ["id"]
        )
    ]
)
data class StoryEntity(
    @ColumnInfo(name = "id")
    val id: Int,
    @ColumnInfo(name = "story_text", defaultValue = "")
    val storyText: String? = "",
    @ColumnInfo(name = "last_edited")
    val lastEdited: Long? = 0
)
