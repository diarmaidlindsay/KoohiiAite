package tech.diarmaid.koohiiaite.widget

/**
 * http://stackoverflow.com/questions/18447063/spinner-get-state-or-get-notified-when-opens
 *
 * So that I know when the spinner has been closed to submit the new values
 */
interface OnSpinnerEventsListener {

    fun onSpinnerOpened()

    fun onSpinnerClosed()

    //this way, only perform an action on close if there has been a change
    fun notifyContentsChange()
}
