package tech.diarmaid.koohiiaite.data.repository

import kotlinx.coroutines.flow.Flow
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
import tech.diarmaid.koohiiaite.data.local.entity.KeywordEntity
import tech.diarmaid.koohiiaite.data.local.entity.MeaningEntity
import tech.diarmaid.koohiiaite.data.local.entity.PrimitiveEntity
import tech.diarmaid.koohiiaite.data.local.entity.ReadingEntity
import tech.diarmaid.koohiiaite.data.local.entity.SampleWordEntity
import tech.diarmaid.koohiiaite.data.local.entity.StoryEntity
import tech.diarmaid.koohiiaite.data.local.entity.UserKeywordEntity
import tech.diarmaid.koohiiaite.domain.model.FilterState
import tech.diarmaid.koohiiaite.domain.model.KanjiDetail
import tech.diarmaid.koohiiaite.domain.model.KanjiItem
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class KanjiRepository @Inject constructor(
    private val heisigKanjiDao: HeisigKanjiDao,
    private val keywordDao: KeywordDao,
    private val userKeywordDao: UserKeywordDao,
    private val primitiveDao: PrimitiveDao,
    private val heisigToPrimitiveDao: HeisigToPrimitiveDao,
    private val storyDao: StoryDao,
    private val readingDao: ReadingDao,
    private val meaningDao: MeaningDao,
    private val sampleWordDao: SampleWordDao,
    private val kanjiFrequencyDao: KanjiFrequencyDao
) {
    suspend fun getAllKanjiItems(): List<KanjiItem> {
        val kanjiList = heisigKanjiDao.getAll()
        val keywords = keywordDao.getAll()
        val userKeywords = userKeywordDao.getAll().associateBy { it.heisigId }
        val primitives = primitiveDao.getAll()
        val htp = heisigToPrimitiveDao.getAll()
        val stories = storyDao.getAll().associateBy { it.id }

        val primitivesByHeisigId: Map<Int, List<String>> = htp
            .groupBy { it.heisigId }
            .mapValues { entry ->
                entry.value.map { htpItem ->
                    primitives.getOrNull(htpItem.primitiveId - 1)?.primitiveText ?: ""
                }
            }

        return kanjiList.map { kanji ->
            val id = kanji.id
            KanjiItem(
                heisigId = id,
                kanji = kanji.kanji,
                keyword = userKeywords[id]?.keywordText ?: keywords.getOrNull(id - 1)?.keywordText ?: "",
                primitives = primitivesByHeisigId[id] ?: emptyList(),
                isJoyo = kanji.joyo,
                hasStory = stories.containsKey(id),
                hasCustomKeyword = userKeywords.containsKey(id)
            )
        }
    }

    suspend fun getKanjiDetail(heisigId: Int): KanjiDetail {
        val kanji = heisigKanjiDao.getById(heisigId)
        val keyword = keywordDao.getById(heisigId)
        val userKeyword = userKeywordDao.getById(heisigId)
        val frequency = kanjiFrequencyDao.getByHeisigId(heisigId)
        val onyomi = readingDao.getByHeisigIdAndType(heisigId, 0)
        val kunyomi = readingDao.getByHeisigIdAndType(heisigId, 1)
        val meanings = meaningDao.getByHeisigId(heisigId)
        val sampleWords = sampleWordDao.getByHeisigId(heisigId)
        val story = storyDao.getById(heisigId)

        return KanjiDetail(
            heisigId = heisigId,
            kanji = kanji?.kanji ?: "",
            keyword = userKeyword?.keywordText ?: keyword?.keywordText ?: "",
            originalKeyword = keyword?.keywordText ?: "",
            userKeyword = userKeyword?.keywordText,
            story = story?.storyText,
            lastEdited = story?.lastEdited,
            frequency = frequency?.frequency,
            onyomi = onyomi?.readingText,
            kunyomi = kunyomi?.readingText,
            meanings = meanings.map { it.meaningText },
            sampleWords = sampleWords,
            isJoyo = kanji?.joyo ?: false
        )
    }

    suspend fun upsertUserKeyword(heisigId: Int, keywordText: String) {
        userKeywordDao.upsert(UserKeywordEntity(heisigId, keywordText))
    }

    suspend fun deleteUserKeyword(heisigId: Int) {
        userKeywordDao.deleteById(heisigId)
    }

    suspend fun upsertStory(heisigId: Int, storyText: String) {
        storyDao.upsertAll(listOf(StoryEntity(
            id = heisigId,
            storyText = storyText,
            lastEdited = System.currentTimeMillis() / 1000L
        )))
    }
}
