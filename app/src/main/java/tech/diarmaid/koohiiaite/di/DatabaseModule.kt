package tech.diarmaid.koohiiaite.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import tech.diarmaid.koohiiaite.data.local.AppDatabase
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
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "koohii.sqlite3.db"
        )
            .createFromAsset("databases/koohii.sqlite3.db")
            .build()
    }

    @Provides fun provideHeisigKanjiDao(db: AppDatabase): HeisigKanjiDao = db.heisigKanjiDao()
    @Provides fun provideHeisigToPrimitiveDao(db: AppDatabase): HeisigToPrimitiveDao = db.heisigToPrimitiveDao()
    @Provides fun provideKanjiFrequencyDao(db: AppDatabase): KanjiFrequencyDao = db.kanjiFrequencyDao()
    @Provides fun provideKeywordDao(db: AppDatabase): KeywordDao = db.keywordDao()
    @Provides fun provideMeaningDao(db: AppDatabase): MeaningDao = db.meaningDao()
    @Provides fun providePrimitiveDao(db: AppDatabase): PrimitiveDao = db.primitiveDao()
    @Provides fun provideReadingDao(db: AppDatabase): ReadingDao = db.readingDao()
    @Provides fun provideSampleWordDao(db: AppDatabase): SampleWordDao = db.sampleWordDao()
    @Provides fun provideStoryDao(db: AppDatabase): StoryDao = db.storyDao()
    @Provides fun provideUserKeywordDao(db: AppDatabase): UserKeywordDao = db.userKeywordDao()
}
