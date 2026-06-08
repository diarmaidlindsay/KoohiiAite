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
import tech.diarmaid.koohiiaite.data.remote.KoohiiApiClient
import tech.diarmaid.koohiiaite.data.remote.KoohiiAuthException
import tech.diarmaid.koohiiaite.data.remote.KoohiiSessionStore
import tech.diarmaid.koohiiaite.data.repository.KanjiRepository
import tech.diarmaid.koohiiaite.domain.model.CsvEntry
import tech.diarmaid.koohiiaite.util.CsvParser
import java.io.BufferedReader
import java.io.StringReader
import javax.inject.Inject

data class ImportStoryUiState(
    val parsedEntries: List<CsvEntry> = emptyList(),
    val statusText: String = "",
    val isProcessing: Boolean = false,
    val showPreview: Boolean = false,
    val importComplete: Boolean = false,
    val affectedCount: Int = 0,
    val needsLogin: Boolean = false
)

@HiltViewModel
class ImportStoryViewModel @Inject constructor(
    private val storyDao: StoryDao,
    private val userKeywordDao: UserKeywordDao,
    private val kanjiRepository: KanjiRepository,
    private val koohiiApiClient: KoohiiApiClient,
    val koohiiSessionStore: KoohiiSessionStore
) : ViewModel() {

    private val _uiState = MutableStateFlow(ImportStoryUiState())
    val uiState: StateFlow<ImportStoryUiState> = _uiState.asStateFlow()

    private val csvParser = CsvParser()

    /**
     * Returns whether the user has stored Koohii session cookies.
     */
    fun isLoggedInToKoohii(): Boolean = koohiiSessionStore.isLoggedIn()

    /**
     * Called when Koohii login succeeds (from the WebView).
     * Immediately triggers the CSV download.
     */
    fun onKoohiiLoginSuccess() {
        _uiState.update { it.copy(needsLogin = false) }
        downloadFromKoohii()
    }

    /**
     * Initiates download of stories from Koohii.
     * If no session cookie is stored, sets needsLogin = true to show the WebView.
     */
    fun downloadFromKoohii() {
        if (!koohiiSessionStore.isLoggedIn()) {
            _uiState.update { it.copy(needsLogin = true) }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isProcessing = true, statusText = "Downloading stories from Koohii...") }

            val result = koohiiApiClient.downloadStoriesCsv()

            result.fold(
                onSuccess = { csvText ->
                    val parseResult = withContext(Dispatchers.IO) {
                        csvParser.parse(BufferedReader(StringReader(csvText)))
                    }
                    parseResult.fold(
                        onSuccess = { entries -> setParsedEntries(entries) },
                        onFailure = { error -> setParsingError(error.message ?: "Unknown error") }
                    )
                },
                onFailure = { error ->
                    if (error is KoohiiAuthException) {
                        _uiState.update {
                            it.copy(
                                isProcessing = false,
                                needsLogin = true,
                                statusText = "Session expired. Please log in again."
                            )
                        }
                    } else {
                        setParsingError(error.message ?: "Download failed")
                    }
                }
            )
        }
    }

    /**
     * Called when the user selects a local CSV file.
     */
    fun setParsedEntries(entries: List<CsvEntry>) {
        _uiState.update {
            it.copy(
                parsedEntries = entries,
                isProcessing = false,
                showPreview = true,
                needsLogin = false,
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

    /**
     * Clears stored Koohii session cookies (logout).
     */
    fun logoutFromKoohii() {
        koohiiSessionStore.clear()
        _uiState.update {
            it.copy(statusText = "Logged out of Koohii.")
        }
    }
}
