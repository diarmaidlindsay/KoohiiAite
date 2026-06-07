package tech.diarmaid.koohiiaite.ui.kanjilist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import tech.diarmaid.koohiiaite.domain.model.FilterState
import tech.diarmaid.koohiiaite.ui.kanjilist.components.FilterChips
import tech.diarmaid.koohiiaite.ui.kanjilist.components.KanjiListItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiListScreen(
    onKanjiClick: (heisigId: Int, filteredIds: List<Int>) -> Unit,
    onPrimitivesClick: () -> Unit,
    onImportStoryClick: () -> Unit,
    shouldRefresh: Boolean = false,
    viewModel: KanjiListViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var searchExpanded by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    LaunchedEffect(shouldRefresh) {
        if (shouldRefresh) {
            viewModel.refreshData()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KoohiiAite") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    IconButton(onClick = { searchExpanded = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Menu, contentDescription = "Menu")
                    }
                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Primitives") },
                            onClick = {
                                showMenu = false
                                onPrimitivesClick()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Import Stories") },
                            onClick = {
                                showMenu = false
                                onImportStoryClick()
                            }
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Search bar
            DockedSearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = uiState.searchQuery,
                        onQueryChange = viewModel::onSearchQueryChanged,
                        onSearch = { searchExpanded = false },
                        expanded = searchExpanded,
                        onExpandedChange = { searchExpanded = it },
                        placeholder = { Text("Primitives, Keyword, Kanji, Frame #") },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                        colors = SearchBarDefaults.inputFieldColors()
                    )
                },
                expanded = searchExpanded,
                onExpandedChange = { searchExpanded = it },
                modifier = Modifier.fillMaxWidth(),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                if (uiState.suggestions.isNotEmpty()) {
                    LazyColumn {
                        items(uiState.suggestions) { suggestion ->
                            Text(
                                text = suggestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { viewModel.onSuggestionSelected(suggestion) }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }

            // Result count
            Text(
                text = "${uiState.filteredItems.size} items displayed",
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            // Filter chips
            FilterChips(
                joyoFilter = uiState.joyoFilter,
                keywordFilter = uiState.keywordFilter,
                storyFilter = uiState.storyFilter,
                onJoyoFilterChange = viewModel::setJoyoFilter,
                onKeywordFilterChange = viewModel::setKeywordFilter,
                onStoryFilterChange = viewModel::setStoryFilter
            )

            // Content
            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxSize()) {
                    items(
                        items = uiState.filteredItems,
                        key = { it.heisigId }
                    ) { item ->
                        KanjiListItem(
                            item = item,
                            onClick = {
                                onKanjiClick(
                                    item.heisigId,
                                    uiState.filteredItems.map { it.heisigId }
                                )
                            }
                        )
                    }
                }
            }
        }
    }
}
