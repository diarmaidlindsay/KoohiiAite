package tech.diarmaid.koohiiaite.widget

import android.content.Context
import android.support.v7.widget.SearchView
import android.util.AttributeSet

/**
 * Override the search view so that we don't clear the search
 * when the "up" button is pressed
 */
class KanjiSearchView : SearchView {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onActionViewCollapsed() {
        val query = query
        super.onActionViewCollapsed()
        setQuery(query, false)
    }

    override fun onActionViewExpanded() {
        val query = query
        super.onActionViewExpanded()
        setQuery(query, false)
    }
}
