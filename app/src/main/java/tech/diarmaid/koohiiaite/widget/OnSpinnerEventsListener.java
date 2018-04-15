package tech.diarmaid.koohiiaite.widget;

/**
 * http://stackoverflow.com/questions/18447063/spinner-get-state-or-get-notified-when-opens
 *
 * So that I know when the spinner has been closed to submit the new values
 */
public interface OnSpinnerEventsListener {

    void onSpinnerOpened();

    void onSpinnerClosed();

    //this way, only perform an action on close if there has been a change
    void notifyContentsChange();
}
