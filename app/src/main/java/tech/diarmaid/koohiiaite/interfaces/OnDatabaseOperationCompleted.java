package tech.diarmaid.koohiiaite.interfaces;

import java.util.List;

/**
 * Callback to import story activity when stories are imported
 */
public interface OnDatabaseOperationCompleted {
    void onImportCompleted(List<Integer> affectedIds);
}
