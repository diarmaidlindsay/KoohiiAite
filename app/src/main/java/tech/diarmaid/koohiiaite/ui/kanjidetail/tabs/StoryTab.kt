package tech.diarmaid.koohiiaite.ui.kanjidetail.tabs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import tech.diarmaid.koohiiaite.domain.model.KanjiDetail

@Composable
fun StoryTab(
    detail: KanjiDetail,
    isEditingKeyword: Boolean,
    keywordEditText: String,
    onStartEditKeyword: () -> Unit,
    onCancelEditKeyword: () -> Unit,
    onKeywordEditTextChange: (String) -> Unit,
    onSaveKeyword: () -> Unit,
    onResetToDefault: () -> Unit,
    onSaveStory: (String) -> Unit,
    onNavigateToKanji: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var storyText by remember(detail.heisigId) { mutableStateOf(detail.story ?: "") }
    var isEditingStory by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Kanji display
        Text(
            text = detail.kanji,
            style = TextStyle(fontSize = 48.sp, textAlign = TextAlign.Center),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Heisig ID
        Text(
            text = "Heisig Frame #${detail.heisigIdFormatted}",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Keyword (clickable)
        val keywordDisplay = if (detail.hasCustomKeyword) {
            "${detail.keyword} (${detail.originalKeyword})"
        } else {
            detail.keyword
        }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onStartEditKeyword),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = "Keyword: ",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = keywordDisplay,
                    style = MaterialTheme.typography.titleMedium,
                    color = if (detail.hasCustomKeyword) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                if (detail.hasCustomKeyword) {
                    Text(
                        text = " (${detail.originalKeyword})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = FontStyle.Italic,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Story
        Text(
            text = "Story",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        if (isEditingStory) {
            OutlinedTextField(
                value = storyText,
                onValueChange = { storyText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp),
                maxLines = 20
            )
            Row(modifier = Modifier.fillMaxWidth()) {
                TextButton(onClick = {
                    isEditingStory = false
                    onSaveStory(storyText)
                }) {
                    Text("Save")
                }
                TextButton(onClick = { isEditingStory = false }) {
                    Text("Cancel")
                }
            }
        } else {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isEditingStory = true }
            ) {
                if (storyText.isBlank()) {
                    Text(
                        text = "Tap to add story...",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(16.dp)
                    )
                } else {
                    Text(
                        text = storyText,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }

    // Keyword edit dialog
    if (isEditingKeyword) {
        AlertDialog(
            onDismissRequest = onCancelEditKeyword,
            title = { Text("Edit Keyword") },
            text = {
                OutlinedTextField(
                    value = keywordEditText,
                    onValueChange = onKeywordEditTextChange,
                    label = { Text("New Keyword") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(onClick = onSaveKeyword) {
                    Text("Submit")
                }
            },
            dismissButton = {
                Row {
                    if (detail.hasCustomKeyword) {
                        OutlinedButton(onClick = onResetToDefault) {
                            Text("Default")
                        }
                    }
                    Spacer(modifier = Modifier.padding(horizontal = 4.dp))
                    TextButton(onClick = onCancelEditKeyword) {
                        Text("Cancel")
                    }
                }
            }
        )
    }
}
