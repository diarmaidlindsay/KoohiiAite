package tech.diarmaid.koohiiaite.widget

import android.content.Context
import android.support.v7.widget.AppCompatSpinner
import android.util.AttributeSet

/**
 * For the filter in the kanji list view
 */
class SpinnerFilter : AppCompatSpinner {
    private var mListener: OnSpinnerEventsListener? = null
    private var mOpenInitiated = false

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun performClick(): Boolean {
        // register that the Spinner was opened so we have a status
        // indicator for the activity(which may lose focus for some other
        // reasons)
        mOpenInitiated = true
        mListener?.onSpinnerOpened()
        return super.performClick()
    }

    fun setSpinnerEventsListener(
            onSpinnerEventsListener: OnSpinnerEventsListener?) {
        mListener = onSpinnerEventsListener
    }

    /**
     * Propagate the closed Spinner event to the listener from outside.
     */
    fun performClosedEvent() {
        mOpenInitiated = false
        if (mListener != null) {
            mListener?.onSpinnerClosed()
        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    fun hasBeenOpened(): Boolean {
        return mOpenInitiated
    }
}
