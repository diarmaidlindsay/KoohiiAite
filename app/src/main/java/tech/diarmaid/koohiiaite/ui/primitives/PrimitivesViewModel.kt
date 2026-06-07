package tech.diarmaid.koohiiaite.ui.primitives

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.diarmaid.koohiiaite.domain.model.PrimitiveItem
import javax.inject.Inject

data class PrimitivesUiState(
    val primitives: List<PrimitiveItem> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class PrimitivesViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(PrimitivesUiState())
    val uiState: StateFlow<PrimitivesUiState> = _uiState.asStateFlow()

    fun loadPrimitives(assetsFilenames: Array<String>) {
        viewModelScope.launch {
            val items = assetsFilenames
                .filter { it.endsWith(".png") }
                .sorted()
                .mapIndexed { index, filename ->
                    PrimitiveItem(
                        id = index,
                        filename = filename,
                        label = deriveLabel(filename)
                    )
                }
            _uiState.value = PrimitivesUiState(primitives = items, isLoading = false)
        }
    }

    private fun deriveLabel(filename: String): String {
        var name = filename
            .substringAfter("-")
            .substringBeforeLast(".")
        // Split on hyphens
        val parts = name.split("-")
        return parts.joinToString(", ") { splitCamelCase(it) }
    }

    private fun splitCamelCase(text: String): String {
        val result = StringBuilder()
        for (char in text) {
            if (char.isUpperCase() && result.isNotEmpty()) {
                result.append(" ")
            }
            result.append(char.lowercaseChar())
        }
        val words = result.toString()
        return words.replaceFirstChar { it.uppercase() }
    }
}
