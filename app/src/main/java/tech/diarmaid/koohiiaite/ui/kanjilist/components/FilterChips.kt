package tech.diarmaid.koohiiaite.ui.kanjilist.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import tech.diarmaid.koohiiaite.domain.model.FilterState

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FilterChips(
    joyoFilter: FilterState,
    keywordFilter: FilterState,
    storyFilter: FilterState,
    onJoyoFilterChange: (FilterState) -> Unit,
    onKeywordFilterChange: (FilterState) -> Unit,
    onStoryFilterChange: (FilterState) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        FilterChipGroup(
            label = "Joyo",
            currentState = joyoFilter,
            onStateChange = onJoyoFilterChange
        )
        FilterChipGroup(
            label = "Keyword",
            currentState = keywordFilter,
            onStateChange = onKeywordFilterChange
        )
        FilterChipGroup(
            label = "Story",
            currentState = storyFilter,
            onStateChange = onStoryFilterChange
        )
    }
}

@Composable
private fun FilterChipGroup(
    label: String,
    currentState: FilterState,
    onStateChange: (FilterState) -> Unit
) {
    val selected = currentState != FilterState.UNSET
    val containerColor = when {
        !selected -> Color.Transparent
        currentState == FilterState.YES -> Color(0xFFBAE397)
        currentState == FilterState.NO -> Color(0xFFFFCDD2)
        else -> Color.Transparent
    }

    FilterChip(
        selected = selected,
        onClick = {
            onStateChange(
                when (currentState) {
                    FilterState.UNSET -> FilterState.YES
                    FilterState.YES -> FilterState.NO
                    FilterState.NO -> FilterState.UNSET
                }
            )
        },
        label = {
            Text(
                text = when (currentState) {
                    FilterState.UNSET -> label
                    FilterState.YES -> "$label O"
                    FilterState.NO -> "$label X"
                }
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = containerColor
        )
    )
}
