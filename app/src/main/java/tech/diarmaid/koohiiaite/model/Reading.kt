package tech.diarmaid.koohiiaite.model

/**
 * Represent an entry in the reading table
 */
class Reading(val id: Int, val heisigId: Int, val readingText: String, val type: Int //0 = onyomi, 1 = kunyomi
)
