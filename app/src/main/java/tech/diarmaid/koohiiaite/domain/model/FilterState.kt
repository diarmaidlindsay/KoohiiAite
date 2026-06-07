package tech.diarmaid.koohiiaite.domain.model

enum class FilterState(val value: Int) {
    UNSET(0),
    YES(1),
    NO(2);

    companion object {
        fun fromInt(value: Int): FilterState = entries.firstOrNull { it.value == value } ?: UNSET
    }
}
