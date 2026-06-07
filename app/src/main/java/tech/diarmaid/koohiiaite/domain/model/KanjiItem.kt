package tech.diarmaid.koohiiaite.domain.model

data class KanjiItem(
    val heisigId: Int,
    val kanji: String,
    val keyword: String,
    val primitives: List<String>,
    val isJoyo: Boolean,
    val hasStory: Boolean,
    val hasCustomKeyword: Boolean
) {
    val heisigIdFormatted: String
        get() = heisigId.toString().padStart(4, '0')

    val primitivesText: String
        get() = primitives.joinToString(", ")
}
