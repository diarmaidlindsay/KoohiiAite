package tech.diarmaid.koohiiaite.widget;

import android.content.Context;
import android.support.v7.widget.SearchView;
import android.util.AttributeSet;

/**
 * Override the search view so that we don't clear the search
 * when the "up" button is pressed
 */
public class KanjiSearchView extends SearchView {

    public KanjiSearchView(Context context) {
        super(context);
    }

    public KanjiSearchView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public KanjiSearchView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onActionViewCollapsed() {
        CharSequence query = getQuery();
        super.onActionViewCollapsed();
        setQuery(query, false);
    }

    @Override
    public void onActionViewExpanded() {
        CharSequence query = getQuery();
        super.onActionViewExpanded();
        setQuery(query, false);
    }
}
