package tech.diarmaid.koohiiaite.interfaces

import tech.diarmaid.koohiiaite.model.CSVEntry

/**
 * Callback to update table containing csv contents after
 * parsing completed
 */
interface OnCSVParseCompleted {
    fun onParsingCompleted(parsedEntries: List<CSVEntry>)
}
