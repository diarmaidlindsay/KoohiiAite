package com.diarmaidlindsay.koohii.interfaces;

/**
 * Callback to update table containing csv contents after
 * parsing completed
 */
public interface OnCSVParseCompleted {
    void onParsingCompleted(boolean success);
}
