package tech.diarmaid.koohiiaite.ui.kanjidetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import tech.diarmaid.koohiiaite.ui.kanjidetail.tabs.DictionaryTab
import tech.diarmaid.koohiiaite.ui.kanjidetail.tabs.KoohiiTab
import tech.diarmaid.koohiiaite.ui.kanjidetail.tabs.SampleWordsTab
import tech.diarmaid.koohiiaite.ui.kanjidetail.tabs.StoryTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KanjiDetailScreen(
    heisigId: Int,
    filteredIds: List<Int>,
    initialTabIndex: Int = 0,
    onNavigateBack: () -> Unit,
    onNavigateToKanji: (Int, Int) -> Unit,
    viewModel: KanjiDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentIndex = filteredIds.indexOf(heisigId)
    val isFirst = currentIndex <= 0
    val isLast = currentIndex >= filteredIds.size - 1

    LaunchedEffect(heisigId) {
        viewModel.loadKanji(heisigId)
    }

    val tabs = listOf("Story", "Dictionary", "Sample Words", "Koohii")
    val pagerState = rememberPagerState(
        pageCount = { tabs.size },
        initialPage = initialTabIndex.coerceIn(0, tabs.size - 1)
    )
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = uiState.detail?.kanji ?: "",
                        style = MaterialTheme.typography.headlineMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            if (currentIndex > 0) {
                                val prevId = filteredIds[currentIndex - 1]
                                onNavigateToKanji(prevId, pagerState.currentPage)
                            }
                        },
                        enabled = !isFirst
                    ) {
                        Icon(
                            Icons.Default.ChevronLeft,
                            contentDescription = "Previous",
                            tint = if (isFirst) {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                    IconButton(
                        onClick = {
                            if (currentIndex < filteredIds.size - 1) {
                                val nextId = filteredIds[currentIndex + 1]
                                onNavigateToKanji(nextId, pagerState.currentPage)
                            }
                        },
                        enabled = !isLast
                    ) {
                        Icon(
                            Icons.Default.ChevronRight,
                            contentDescription = "Next",
                            tint = if (isLast) {
                                MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            val detail = uiState.detail
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                PrimaryTabRow(selectedTabIndex = pagerState.currentPage) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = { Text(title) }
                        )
                    }
                }

                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    when (page) {
                        0 -> detail?.let {
                            StoryTab(
                                detail = it,
                                isEditingKeyword = uiState.isEditingKeyword,
                                keywordEditText = uiState.keywordEditText,
                                onStartEditKeyword = viewModel::startEditingKeyword,
                                onCancelEditKeyword = viewModel::cancelEditingKeyword,
                                onKeywordEditTextChange = viewModel::updateKeywordEditText,
                                onSaveKeyword = viewModel::saveKeyword,
                                onResetToDefault = viewModel::resetToDefaultKeyword,
                                onSaveStory = viewModel::saveStory,
                                onNavigateToKanji = { id ->
                                    onNavigateToKanji(id, pagerState.currentPage)
                                }
                            )
                        }
                        1 -> detail?.let { DictionaryTab(detail = it) }
                        2 -> detail?.let { SampleWordsTab(sampleWords = it.sampleWords) }
                        3 -> detail?.let { KoohiiTab(kanji = it.kanji) }
                    }
                }
            }
        }
    }
}
