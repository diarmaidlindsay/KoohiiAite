package tech.diarmaid.koohiiaite.ui.kanjidetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.diarmaid.koohiiaite.data.repository.KanjiRepository
import tech.diarmaid.koohiiaite.domain.model.KanjiDetail
import javax.inject.Inject

data class KanjiDetailUiState(
    val detail: KanjiDetail? = null,
    val isLoading: Boolean = true,
    val isEditingKeyword: Boolean = false,
    val keywordEditText: String = ""
)

@HiltViewModel
class KanjiDetailViewModel @Inject constructor(
    private val repository: KanjiRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(KanjiDetailUiState())
    val uiState: StateFlow<KanjiDetailUiState> = _uiState.asStateFlow()

    fun loadKanji(heisigId: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            val detail = repository.getKanjiDetail(heisigId)
            _uiState.update {
                it.copy(
                    detail = detail,
                    isLoading = false,
                    isEditingKeyword = false
                )
            }
        }
    }

    fun startEditingKeyword() {
        val currentDetail = _uiState.value.detail ?: return
        _uiState.update {
            it.copy(
                isEditingKeyword = true,
                keywordEditText = currentDetail.userKeyword ?: currentDetail.keyword
            )
        }
    }

    fun cancelEditingKeyword() {
        _uiState.update { it.copy(isEditingKeyword = false) }
    }

    fun updateKeywordEditText(text: String) {
        _uiState.update { it.copy(keywordEditText = text) }
    }

    fun saveKeyword() {
        val detail = _uiState.value.detail ?: return
        val newText = _uiState.value.keywordEditText

        viewModelScope.launch {
            if (newText.isBlank() || newText == detail.originalKeyword) {
                // Delete user keyword (revert to default)
                repository.deleteUserKeyword(detail.heisigId)
                loadKanji(detail.heisigId)
            } else {
                repository.upsertUserKeyword(detail.heisigId, newText)
                loadKanji(detail.heisigId)
            }
        }
    }

    fun resetToDefaultKeyword() {
        val detail = _uiState.value.detail ?: return
        viewModelScope.launch {
            repository.deleteUserKeyword(detail.heisigId)
            loadKanji(detail.heisigId)
        }
    }

    fun saveStory(storyText: String) {
        val detail = _uiState.value.detail ?: return
        viewModelScope.launch {
            repository.upsertStory(detail.heisigId, storyText)
            loadKanji(detail.heisigId)
        }
    }
}
