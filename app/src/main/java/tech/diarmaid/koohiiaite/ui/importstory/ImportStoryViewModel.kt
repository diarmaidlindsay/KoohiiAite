package tech.diarmaid.koohiiaite.ui.importstory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.diarmaid.koohiiaite.data.local.dao.StoryDao
import tech.diarmaid.koohiiaite.data.local.dao.UserKeywordDao
import tech.diarmaid.koohiiaite.data.local.entity.StoryEntity
import tech.diarmaid.koohiiaite.data.local.entity.UserKeywordEntity
import tech.diarmaid.koohiiaite.data.repository.KanjiRepository
import tech.diarmaid.koohiiaite.domain.model.CsvEntry
import javax.inject.Inject

data class ImportStoryUiState(
    val parsedEntries: List<CsvEntry> = emptyList(),
    val statusText: String = "",
    val isProcessing: Boolean = false,
    val showPreview: Boolean = false,
    val importComplete: Boolean = false,
    val affectedCount: Int = 0
)

@HiltViewModel
class ImportStoryViewModel @Inject constructor(
    private val storyDao: StoryDao,
    private val userKeywordDao: UserKeywordDao,
    private val kanjiRepository: KanjiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportStoryUiState())
    val uiState: StateFlow<ImportStoryUiState> = _uiState.asStateFlow()

    fun setParsedEntries(entries: List<CsvEntry>) {
        _uiState.update {
            it.copy(
                parsedEntries = entries,
                isProcessing = false,
                showPreview = true,
                statusText = "${entries.size} stories found for import."
            )
        }
    }

    fun setParsingError(error: String) {
        _uiState.update {
            it.copy(
                isProcessing = false,
                statusText = "Failed to read CSV: $error"
            )
        }
    }

    fun setProcessing(status: String) {
        _uiState.update { it.copy(isProcessing = true, statusText = status) }
    }

    fun confirmImport() {
        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, statusText = "Saving to database...") }

            withContext(Dispatchers.IO) {
                val entries = _uiState.value.parsedEntries
                val allKanji = kanjiRepository.getAllKanjiItems()
                val lastId = allKanji.maxOfOrNull { it.heisigId } ?: return@withContext

                val stories = mutableListOf<StoryEntity>()
                val keywords = mutableListOf<UserKeywordEntity>()

                for (entry in entries) {
                    val id = entry.id.toIntOrNull() ?: continue
                    if (id < 1 || id > lastId) continue

                    val originalKw = allKanji.find { it.heisigId == id }
                    if (originalKw != null && originalKw.keyword != entry.keyword) {
                        keywords.add(UserKeywordEntity(id, entry.keyword))
                    }

                    stories.add(
                        StoryEntity(
                            id = id,
                            storyText = entry.story,
                            lastEdited = System.currentTimeMillis() / 1000L
                        )
                    )
                }

                storyDao.upsertAll(stories)
                userKeywordDao.upsertAll(keywords)

                val affectedCount = entries.mapNotNull { it.id.toIntOrNull() }
                    .count { it in 1..lastId }

                withContext(Dispatchers.Main) {
                    _uiState.update {
                        it.copy(
                            isProcessing = false,
                            importComplete = true,
                            affectedCount = affectedCount,
                            statusText = "$affectedCount stories and keywords updated."
                        )
                    }
                }
            }
        }
    }
}
