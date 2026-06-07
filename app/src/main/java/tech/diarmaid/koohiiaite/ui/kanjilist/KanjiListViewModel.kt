package tech.diarmaid.koohiiaite.ui.kanjilist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.diarmaid.koohiiaite.data.repository.KanjiRepository
import tech.diarmaid.koohiiaite.domain.model.FilterState
import tech.diarmaid.koohiiaite.domain.model.KanjiItem
import tech.diarmaid.koohiiaite.domain.usecase.SearchKanjiUseCase
import javax.inject.Inject

data class KanjiListUiState(
    val allItems: List<KanjiItem> = emptyList(),
    val filteredItems: List<KanjiItem> = emptyList(),
    val searchQuery: String = "",
    val suggestions: List<String> = emptyList(),
    val joyoFilter: FilterState = FilterState.UNSET,
    val keywordFilter: FilterState = FilterState.UNSET,
    val storyFilter: FilterState = FilterState.UNSET,
    val isLoading: Boolean = true
)

@HiltViewModel
class KanjiListViewModel @Inject constructor(
    private val repository: KanjiRepository,
    private val searchUseCase: SearchKanjiUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(KanjiListUiState())
    val uiState: StateFlow<KanjiListUiState> = _uiState.asStateFlow()

    private val _searchQuery = MutableStateFlow("")

    init {
        loadData()
    }

    @OptIn(FlowPreview::class)
    private fun loadData() {
        viewModelScope.launch {
            val items = repository.getAllKanjiItems()
            _uiState.update { it.copy(allItems = items, isLoading = false) }
            performSearch("")
        }

        _searchQuery
            .debounce(300)
            .distinctUntilChanged()
            .onEach { query ->
                performSearch(query)
            }
            .launchIn(viewModelScope)
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        _searchQuery.value = query

        // Show suggestions while typing
        val suggestions = searchUseCase.getSuggestions(query, _uiState.value.allItems)
        _uiState.update { it.copy(suggestions = suggestions) }
    }

    fun onSuggestionSelected(suggestion: String) {
        val currentQuery = _uiState.value.searchQuery
        val newQuery = if (currentQuery.contains(",")) {
            val parts = currentQuery.split(",").toMutableList()
            parts[parts.lastIndex] = " $suggestion"
            parts.joinToString(",")
        } else {
            suggestion
        }
        onSearchQueryChanged(newQuery)
        performSearch(newQuery)
    }

    fun setJoyoFilter(state: FilterState) {
        _uiState.update { it.copy(joyoFilter = state) }
        performSearch(_uiState.value.searchQuery)
    }

    fun setKeywordFilter(state: FilterState) {
        _uiState.update { it.copy(keywordFilter = state) }
        performSearch(_uiState.value.searchQuery)
    }

    fun setStoryFilter(state: FilterState) {
        _uiState.update { it.copy(storyFilter = state) }
        performSearch(_uiState.value.searchQuery)
    }

    fun refreshData() {
        viewModelScope.launch {
            val items = repository.getAllKanjiItems()
            _uiState.update { it.copy(allItems = items, isLoading = false) }
            performSearch(_uiState.value.searchQuery)
        }
    }

    private fun performSearch(query: String) {
        val state = _uiState.value
        val filtered = searchUseCase.search(
            query = query,
            allItems = state.allItems,
            joyoFilter = state.joyoFilter,
            keywordFilter = state.keywordFilter,
            storyFilter = state.storyFilter
        )
        _uiState.update { it.copy(filteredItems = filtered) }
    }
}
