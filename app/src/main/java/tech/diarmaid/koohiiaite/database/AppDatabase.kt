package tech.diarmaid.koohiiaite.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import tech.diarmaid.koohiiaite.database.entity.*
import tech.diarmaid.koohiiaite.database.roomdao.*


@Database(entities = [
    HeisigKanji::class, HeisigToPrimitive::class, KanjiFrequency::class, Keyword::class,
    Meaning::class, Primitive::class, Reading::class, SampleWord::class, Story::class, UserKeyword::class
], version = 1)
abstract class AppDatabase : RoomDatabase() {

    abstract fun heisigKanjiDao(): HeisigKanjiDao
    abstract fun heisigToPrimitiveDao(): HeisigToPrimitiveDao
    abstract fun kanjiFrequencyDao(): KanjiFrequencyDao
    abstract fun keywordDao(): KeywordDao
    abstract fun meaningDao(): MeaningDao
    abstract fun primitiveDao(): PrimitiveDao
    abstract fun readingDao(): ReadingDao
    abstract fun sampleWordDao(): SampleWordDao
    abstract fun storyDao(): StoryDao
    abstract fun userKeywordDao(): UserKeywordDao

    companion object {
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                synchronized(AppDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                            AppDatabase::class.java, "koohii.sqlite3.db")
                            .build()
                }
            }

            return this.INSTANCE!!
        }
    }
}