package tech.diarmaid.koohiiaite.ui.importstory

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tech.diarmaid.koohiiaite.domain.model.CsvEntry
import tech.diarmaid.koohiiaite.util.CsvParser
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Import source selection — the user can either:
 * 1. Download directly from Koohii (with stored or fresh login)
 * 2. Pick a local CSV file
 */
private enum class ImportSource {
    CHOOSING,
    KOOHII_WEBVIEW,
    KOOHII_DOWNLOADING,
    FILE_PICKER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImportStoryScreen(
    onBack: () -> Unit,
    onImportComplete: () -> Unit,
    viewModel: ImportStoryViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val csvParser = remember { CsvParser() }

    var importSource by remember { mutableStateOf(ImportSource.CHOOSING) }

    // Watch for needsLogin — switch to WebView if the ViewModel requests it
    LaunchedEffect(uiState.needsLogin) {
        if (uiState.needsLogin) {
            importSource = ImportSource.KOOHII_WEBVIEW
        }
    }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { selectedUri ->
            viewModel.setProcessing("Reading CSV file...")
            scope.launch {
                val result: Result<List<CsvEntry>> = withContext(Dispatchers.IO) {
                    try {
                        val inputStream = context.contentResolver.openInputStream(selectedUri)
                        if (inputStream != null) {
                            val reader = BufferedReader(InputStreamReader(inputStream))
                            inputStream.use {
                                csvParser.parse(reader)
                            }
                        } else {
                            Result.failure(Exception("Could not open file"))
                        }
                    } catch (e: Exception) {
                        Result.failure(e)
                    }
                }
                result.fold(
                    onSuccess = { entries -> viewModel.setParsedEntries(entries) },
                    onFailure = { error -> viewModel.setParsingError(error.message ?: "Unknown error") }
                )
            }
        }
    }

    LaunchedEffect(uiState.importComplete) {
        if (uiState.importComplete) {
            onImportComplete()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Import Stories") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Show logout button if logged into Koohii
                    if (viewModel.isLoggedInToKoohii() && !uiState.showPreview) {
                        TextButton(onClick = {
                            viewModel.logoutFromKoohii()
                            importSource = ImportSource.CHOOSING
                        }) {
                            Text(
                                "Logout Koohii",
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // --- Source chooser (only when not showing preview or login) ---
            if (!uiState.showPreview && importSource == ImportSource.CHOOSING) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Choose import source:",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 24.dp)
                    )

                    Button(
                        onClick = {
                            importSource = ImportSource.KOOHII_DOWNLOADING
                            viewModel.downloadFromKoohii()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Text("Import from Koohii")
                    }

                    OutlinedButton(
                        onClick = {
                            importSource = ImportSource.FILE_PICKER
                            filePickerLauncher.launch("text/*")
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Import from File")
                    }
                }
            }

            // --- Koohii WebView login ---
            if (importSource == ImportSource.KOOHII_WEBVIEW) {
                KoohiiLoginWebView(
                    sessionStore = viewModel.koohiiSessionStore,
                    onLoginSuccess = {
                        importSource = ImportSource.KOOHII_DOWNLOADING
                        viewModel.onKoohiiLoginSuccess()
                    },
                    modifier = Modifier.weight(1f)
                )
            }

            // --- Processing indicator ---
            if (uiState.isProcessing) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Spacer(modifier = Modifier.padding(8.dp))
            }

            // --- Status text ---
            if (uiState.statusText.isNotBlank()) {
                Text(
                    text = uiState.statusText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // --- Preview & confirm ---
            if (uiState.showPreview && uiState.parsedEntries.isNotEmpty()) {
                Text(
                    text = "Preview:",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(uiState.parsedEntries, key = { it.id }) { entry ->
                        CsvPreviewCard(entry)
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            // Go back to source chooser
                            importSource = ImportSource.CHOOSING
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        onClick = viewModel::confirmImport,
                        modifier = Modifier.weight(1f),
                        enabled = !uiState.isProcessing
                    ) {
                        Text("Confirm Import")
                    }
                }
            }
        }
    }
}

@Composable
private fun CsvPreviewCard(
    entry: CsvEntry,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = entry.id,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = entry.kanji,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = entry.keyword,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(end = 8.dp)
            )
            Text(
                text = entry.story.take(40) + if (entry.story.length > 40) "..." else "",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
