package tech.diarmaid.koohiiaite.interfaces

/**
 * Callback to import story activity when stories are imported
 */
interface OnDatabaseOperationCompleted {
    fun onImportCompleted(affectedIds: List<Int>)
}
