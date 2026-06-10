package tech.diarmaid.koohiiaite.ui.kanjilist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
                windowInsets = WindowInsets(0, 0, 0, 0),
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
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Search bar
                TextField(
                    value = uiState.searchQuery,
                    onValueChange = {
                        viewModel.onSearchQueryChanged(it)
                        searchExpanded = true
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Primitives, Keyword, Kanji, Frame #") },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search"
                        )
                    },
                    trailingIcon = {
                        Row {
                            if (uiState.searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Clear search"
                                    )
                                }
                            }
                            IconButton(onClick = { searchExpanded = !searchExpanded }) {
                                Icon(
                                    imageVector = if (searchExpanded) {
                                        Icons.Default.KeyboardArrowUp
                                    } else {
                                        Icons.Default.KeyboardArrowDown
                                    },
                                    contentDescription = if (searchExpanded) {
                                        "Collapse search"
                                    } else {
                                        "Expand search"
                                    }
                                )
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(28.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )

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

            // Floating dropdown overlay
            if (searchExpanded && uiState.suggestions.isNotEmpty()) {
                Card(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .offset(y = 52.dp)
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .heightIn(max = 132.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    LazyColumn {
                        items(uiState.suggestions) { suggestion ->
                            Text(
                                text = suggestion,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        viewModel.onSuggestionSelected(suggestion)
                                        searchExpanded = false
                                    }
                                    .padding(horizontal = 16.dp, vertical = 16.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
                            if (suggestion != uiState.suggestions.last()) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    color = MaterialTheme.colorScheme.outlineVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
