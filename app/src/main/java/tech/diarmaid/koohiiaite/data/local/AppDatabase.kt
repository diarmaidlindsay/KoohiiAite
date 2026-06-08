package tech.diarmaid.koohiiaite.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.diarmaid.koohiiaite.data.local.dao.HeisigKanjiDao
import tech.diarmaid.koohiiaite.data.local.dao.HeisigToPrimitiveDao
import tech.diarmaid.koohiiaite.data.local.dao.KanjiFrequencyDao
import tech.diarmaid.koohiiaite.data.local.dao.KeywordDao
import tech.diarmaid.koohiiaite.data.local.dao.MeaningDao
import tech.diarmaid.koohiiaite.data.local.dao.PrimitiveDao
import tech.diarmaid.koohiiaite.data.local.dao.ReadingDao
import tech.diarmaid.koohiiaite.data.local.dao.SampleWordDao
import tech.diarmaid.koohiiaite.data.local.dao.StoryDao
import tech.diarmaid.koohiiaite.data.local.dao.UserKeywordDao
import tech.diarmaid.koohiiaite.data.local.entity.HeisigKanjiEntity
import tech.diarmaid.koohiiaite.data.local.entity.HeisigToPrimitiveEntity
import tech.diarmaid.koohiiaite.data.local.entity.KanjiFrequencyEntity
import tech.diarmaid.koohiiaite.data.local.entity.KeywordEntity
import tech.diarmaid.koohiiaite.data.local.entity.MeaningEntity
import tech.diarmaid.koohiiaite.data.local.entity.PrimitiveEntity
import tech.diarmaid.koohiiaite.data.local.entity.ReadingEntity
import tech.diarmaid.koohiiaite.data.local.entity.SampleWordEntity
import tech.diarmaid.koohiiaite.data.local.entity.StoryEntity
import tech.diarmaid.koohiiaite.data.local.entity.UserKeywordEntity

@Database(
    entities = [
        HeisigKanjiEntity::class,
        HeisigToPrimitiveEntity::class,
        KanjiFrequencyEntity::class,
        KeywordEntity::class,
        MeaningEntity::class,
        PrimitiveEntity::class,
        ReadingEntity::class,
        SampleWordEntity::class,
        StoryEntity::class,
        UserKeywordEntity::class
    ],
    version = 2,
    exportSchema = true
)
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
}
