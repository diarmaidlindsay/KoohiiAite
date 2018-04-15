package tech.diarmaid.koohiiaite.widget;

import android.content.Context;
import android.support.v7.widget.AppCompatSpinner;
import android.util.AttributeSet;

/**
 * For the filter in the kanji list view
 */
public class SpinnerFilter extends AppCompatSpinner {
    private OnSpinnerEventsListener mListener;
    private boolean mOpenInitiated = false;

    public SpinnerFilter(Context context) {
        super(context);
    }

    public SpinnerFilter(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SpinnerFilter(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public boolean performClick() {
        // register that the Spinner was opened so we have a status
        // indicator for the activity(which may lose focus for some other
        // reasons)
        mOpenInitiated = true;
        if (mListener != null) {
            mListener.onSpinnerOpened();
        }
        return super.performClick();
    }

    public void setSpinnerEventsListener(
            OnSpinnerEventsListener onSpinnerEventsListener) {
        mListener = onSpinnerEventsListener;
    }

    /**
     * Propagate the closed Spinner event to the listener from outside.
     */
    public void performClosedEvent() {
        mOpenInitiated = false;
        if (mListener != null) {
            mListener.onSpinnerClosed();
        }
    }

    /**
     * A boolean flag indicating that the Spinner triggered an open event.
     *
     * @return true for opened Spinner
     */
    public boolean hasBeenOpened() {
        return mOpenInitiated;
    }
}
