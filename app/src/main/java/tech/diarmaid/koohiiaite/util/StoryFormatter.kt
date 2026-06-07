package tech.diarmaid.koohiiaite.util

import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import tech.diarmaid.koohiiaite.data.local.entity.KeywordEntity
import tech.diarmaid.koohiiaite.data.local.entity.UserKeywordEntity
import java.util.Locale

class StoryFormatter {

    data class FormattedStory(
        val annotatedString: AnnotatedString,
        val kanjiLinks: List<KanjiLink>
    )

    data class KanjiLink(
        val heisigId: Int,
        val start: Int,
        val end: Int
    )

    fun format(
        rawStory: String,
        keywordText: String,
        keywords: List<KeywordEntity>,
        userKeywords: List<UserKeywordEntity>
    ): FormattedStory {
        var text = rawStory

        // Parse all markup
        val italicRanges = parseRanges(text, '*')
        val boldRanges = parseRanges(text, '#')
        val linkData = parseBraceLinks(text, keywords, userKeywords)

        // Replace brace links with just the kanji character
        for (item in linkData.sortedByDescending { it.braceStart }) {
            val before = text.substring(0, item.braceStart)
            val after = text.substring(item.braceEnd + 1)
            text = before + item.displayChar + after
        }

        // Recalculate link positions after replacements
        val adjustedLinks = mutableListOf<KanjiLink>()
        for (item in linkData) {
            // The kanji was placed at the position of the opening brace
            adjustedLinks.add(KanjiLink(item.heisigId, item.braceStart, item.braceStart + 1))
        }

        // Remove formatting markers
        var cleaned = text
        val asterisksToRemove = italicRanges.flatMap { listOf(it.first, it.second) }.sortedDescending()
        val hashesToRemove = boldRanges.flatMap { listOf(it.first, it.second) }.sortedDescending()
        val allRemovals = (asterisksToRemove + hashesToRemove).sortedDescending()

        for (pos in allRemovals) {
            if (pos < cleaned.length) {
                cleaned = cleaned.substring(0, pos) + cleaned.substring(pos + 1)
            }
        }

        // Build AnnotatedString
        return FormattedStory(
            annotatedString = buildAnnotatedString {
                append(cleaned)

                // Apply italic spans
                for ((start, end) in italicRanges) {
                    val adjustedStart = adjustIndex(start, asterisksToRemove, hashesToRemove)
                    val adjustedEnd = adjustIndex(end, asterisksToRemove, hashesToRemove)
                    if (adjustedStart < adjustedEnd && adjustedStart >= 0 && adjustedEnd <= length) {
                        addStyle(
                            SpanStyle(fontStyle = FontStyle.Italic),
                            adjustedStart,
                            adjustedEnd
                        )
                    }
                }

                // Apply bold spans
                for ((start, end) in boldRanges) {
                    val adjustedStart = adjustIndex(start, asterisksToRemove, hashesToRemove)
                    val adjustedEnd = adjustIndex(end, asterisksToRemove, hashesToRemove)
                    if (adjustedStart < adjustedEnd && adjustedStart >= 0 && adjustedEnd <= length) {
                        addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            adjustedStart,
                            adjustedEnd
                        )
                    }
                }

                // Bold the keyword wherever it appears
                val keywordLower = keywordText.lowercase(Locale.ROOT)
                val textLower = cleaned.lowercase(Locale.ROOT)
                var searchStart = 0
                while (true) {
                    val idx = textLower.indexOf(keywordLower, searchStart)
                    if (idx == -1) break
                    if (keywordLower.length >= 2) {
                        addStyle(
                            SpanStyle(fontWeight = FontWeight.Bold),
                            idx,
                            idx + keywordLower.length
                        )
                    }
                    searchStart = idx + 1
                }

                // Make uppercase keywords clickable
                val uppercasePattern = Regex("[A-Z]{2,}")
                for (match in uppercasePattern.findAll(cleaned)) {
                    val word = match.value
                    val userKw = userKeywords.firstOrNull {
                        it.keywordText.equals(word, ignoreCase = true)
                    }
                    val kw = keywords.firstOrNull {
                        it.keywordText.equals(word, ignoreCase = true)
                    }
                    if (userKw != null || kw != null) {
                        addStyle(
                            SpanStyle(
                                fontWeight = FontWeight.Bold,
                                color = androidx.compose.ui.graphics.Color(0xFF609732)
                            ),
                            match.range.first,
                            match.range.last + 1
                        )
                    }
                }
            },
            kanjiLinks = adjustedLinks
        )
    }

    private fun parseRanges(text: String, marker: Char): List<Pair<Int, Int>> {
        val ranges = mutableListOf<Pair<Int, Int>>()
        val positions = text.withIndex().filter { it.value == marker }
        if (positions.size >= 2) {
            val count = positions.size / 2 * 2
            for (i in 0 until count step 2) {
                ranges.add(positions[i].index to positions[i + 1].index)
            }
        }
        return ranges
    }

    private fun parseBraceLinks(
        text: String,
        keywords: List<KeywordEntity>,
        userKeywords: List<UserKeywordEntity>
    ): List<BraceLinkData> {
        val results = mutableListOf<BraceLinkData>()
        val regex = Regex("\\{([^}]+)\\}")
        for (match in regex.findAll(text)) {
            val content = match.groupValues[1]
            // Try to parse as heisig ID
            val numericId = content.toIntOrNull()
            if (numericId != null) {
                val kw = keywords.getOrNull(numericId - 1)
                results.add(BraceLinkData(
                    braceStart = match.range.first,
                    braceEnd = match.range.last,
                    heisigId = numericId,
                    displayChar = kw?.keywordText?.take(1) ?: "#"
                ))
            } else if (content.length == 1) {
                // It's a single kanji character
                val kw = keywords.firstOrNull { it.keywordText == content }
                val userKw = userKeywords.firstOrNull { it.keywordText == content }
                val targetId = userKw?.heisigId ?: kw?.heisigId ?: continue
                results.add(BraceLinkData(
                    braceStart = match.range.first,
                    braceEnd = match.range.last,
                    heisigId = targetId,
                    displayChar = content
                ))
            }
        }
        return results
    }

    private data class BraceLinkData(
        val braceStart: Int,
        val braceEnd: Int,
        val heisigId: Int,
        val displayChar: String
    )

    private fun adjustIndex(
        originalIndex: Int,
        asterisks: List<Int>,
        hashes: List<Int>
    ): Int {
        var adjusted = originalIndex
        val all = (asterisks + hashes).sorted()
        for (pos in all) {
            if (pos < originalIndex) adjusted--
        }
        return adjusted
    }
}
