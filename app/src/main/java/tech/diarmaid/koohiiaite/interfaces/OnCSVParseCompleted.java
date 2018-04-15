package tech.diarmaid.koohiiaite.interfaces;

import tech.diarmaid.koohiiaite.model.CSVEntry;

import java.util.List;

/**
 * Callback to update table containing csv contents after
 * parsing completed
 */
public interface OnCSVParseCompleted {
    void onParsingCompleted(List<CSVEntry> parsedEntries);
}
