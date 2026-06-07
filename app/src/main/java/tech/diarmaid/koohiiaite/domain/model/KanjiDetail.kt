package tech.diarmaid.koohiiaite.domain.model

import tech.diarmaid.koohiiaite.data.local.entity.SampleWordEntity

data class KanjiDetail(
    val heisigId: Int,
    val kanji: String,
    val keyword: String,
    val originalKeyword: String,
    val userKeyword: String?,
    val story: String?,
    val lastEdited: Long?,
    val frequency: Int?,
    val onyomi: String?,
    val kunyomi: String?,
    val meanings: List<String>,
    val sampleWords: List<SampleWordEntity>,
    val isJoyo: Boolean
) {
    val heisigIdFormatted: String
        get() = heisigId.toString().padStart(4, '0')

    val frequencyText: String
        get() = if (frequency == null || frequency == 999999) "-" else frequency.toString()

    val meaningsText: String
        get() = meanings.joinToString(", ")

    val hasStory: Boolean
        get() = !story.isNullOrBlank()

    val hasCustomKeyword: Boolean
        get() = !userKeyword.isNullOrBlank()
}
