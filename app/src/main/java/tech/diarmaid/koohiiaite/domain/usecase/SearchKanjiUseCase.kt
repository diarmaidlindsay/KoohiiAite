package tech.diarmaid.koohiiaite.domain.usecase

import tech.diarmaid.koohiiaite.domain.model.FilterState
import tech.diarmaid.koohiiaite.domain.model.KanjiItem
import java.util.Locale
import javax.inject.Inject

class SearchKanjiUseCase @Inject constructor() {

    fun search(
        query: String,
        allItems: List<KanjiItem>,
        joyoFilter: FilterState,
        keywordFilter: FilterState,
        storyFilter: FilterState
    ): List<KanjiItem> {
        val trimmedQuery = query.trim().lowercase(Locale.ROOT)

        val matched = if (trimmedQuery.isEmpty()) {
            allItems
        } else {
            searchByQuery(trimmedQuery, allItems)
        }

        return applyFilters(matched, joyoFilter, keywordFilter, storyFilter)
    }

    fun getSuggestions(
        query: String,
        allItems: List<KanjiItem>
    ): List<String> {
        val trimmedQuery = query.trim().lowercase(Locale.ROOT)
        if (trimmedQuery.length < 2) return emptyList()

        // For comma-separated queries, only suggest for the last part
        val searchTerm = if (trimmedQuery.contains(",")) {
            trimmedQuery.split(",").last().trim()
        } else {
            trimmedQuery
        }

        if (searchTerm.length < 2) return emptyList()

        val suggestions = mutableSetOf<String>()

        for (item in allItems) {
            if (item.keyword.lowercase(Locale.ROOT).contains(searchTerm)) {
                suggestions.add(item.keyword)
            }
            for (primitive in item.primitives) {
                if (primitive.lowercase(Locale.ROOT).contains(searchTerm)) {
                    suggestions.add(primitive)
                }
            }
        }

        return suggestions.sortedBy { it.lowercase(Locale.ROOT) }
    }

    private fun searchByQuery(query: String, items: List<KanjiItem>): List<KanjiItem> {
        return when {
            query.all { it.isDigit() } -> items.filter { item ->
                item.heisigId.toString().contains(query)
            }
            query.first().isKanji() -> items.filter { item ->
                query.any { qChar -> item.kanji.contains(qChar.toString()) }
            }
            query.contains(",") -> searchByPrimitives(query, items)
            else -> items.filter { item ->
                val lc = query
                item.keyword.lowercase(Locale.ROOT).contains(lc) ||
                        item.primitivesText.lowercase(Locale.ROOT).contains(lc)
            }
        }
    }

    private fun searchByPrimitives(query: String, items: List<KanjiItem>): List<KanjiItem> {
        val primitiveNames = query.split(",").map { it.trim().lowercase(Locale.ROOT) }.filter { it.isNotEmpty() }
        if (primitiveNames.isEmpty()) return emptyList()

        return items.filter { item ->
            primitiveNames.all { name ->
                // Exact match on each primitive name segment
                item.primitives.any { p -> p.lowercase(Locale.ROOT) == name }
            }
        }
    }

    private fun applyFilters(
        items: List<KanjiItem>,
        joyoFilter: FilterState,
        keywordFilter: FilterState,
        storyFilter: FilterState
    ): List<KanjiItem> {
        return items.filter { item ->
            when (joyoFilter) {
                FilterState.YES -> item.isJoyo
                FilterState.NO -> !item.isJoyo
                FilterState.UNSET -> true
            } && when (keywordFilter) {
                FilterState.YES -> item.hasCustomKeyword
                FilterState.NO -> !item.hasCustomKeyword
                FilterState.UNSET -> true
            } && when (storyFilter) {
                FilterState.YES -> item.hasStory
                FilterState.NO -> !item.hasStory
                FilterState.UNSET -> true
            }
        }
    }

    private fun Char.isKanji(): Boolean =
        Character.UnicodeBlock.of(this) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
}
