package tech.diarmaid.koohiiaite.activity

import android.app.Activity
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.os.Handler
import android.support.v4.widget.CursorAdapter
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.InputMethodManager
import android.widget.ListView
import android.widget.TextView
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.adapter.KanjiListAdapter
import tech.diarmaid.koohiiaite.adapter.KanjiListFilterAdapter
import tech.diarmaid.koohiiaite.adapter.SuggestionsAdapter
import tech.diarmaid.koohiiaite.enumeration.FilterState
import tech.diarmaid.koohiiaite.utils.Constants.RETURN_CODE_IMPORT_STORY_ACTIVITY
import tech.diarmaid.koohiiaite.widget.KanjiSearchView
import tech.diarmaid.koohiiaite.widget.OnSpinnerEventsListener
import tech.diarmaid.koohiiaite.widget.SpinnerFilter
import java.util.*

/**
 * Koohii Aite uses Heisig Old Edition
 * Volume 1 : 5th edition or earlier.
 * Volume 3 : 1st or 2nd edition.
 *
 * This is because I use rikaikun Chrome plugin, which gives me old edition Heisig indexes.
 * At some point I should update the application to handle new edition indexes as well.
 * And update rikaikun's kanji.dat as well.
 *
 * https://docs.google.com/spreadsheets/d/1Z0BUSie8wh0JqlUejezs3EqauJuF-zKEomOQnqm9J08/edit#gid=0
 *
 * Entry point into the application.
 */
class KanjiListActivity : AppCompatActivity() {

    private lateinit var kanjiListAdapter: KanjiListAdapter
    private lateinit var kanjiListFilterAdapter: KanjiListFilterAdapter
    private lateinit var suggestionAdapter: SuggestionsAdapter
    private var searchView: KanjiSearchView? = null
    private var spinnerFilter: SpinnerFilter? = null
    private var spinnerListener: OnSpinnerEventsListener? = null
    private var result: TextView? = null
    private var joyoFilterState: TextView? = null
    private var keywordFilterState: TextView? = null
    private var storyFilterState: TextView? = null
    private var kanjiList: ListView? = null

    private var savedInstanceState: Bundle? = null

    private val cursorAdapter: SuggestionsAdapter
        get() {
            val from = arrayOf("keywordPrimitive")
            val to = intArrayOf(R.id.suggestion_item)

            return SuggestionsAdapter(this,
                    R.layout.list_item_suggestion, null,
                    from,
                    to,
                    CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER)
        }

    private//Delay after user input to smooth the user experience
    val textListener: SearchView.OnQueryTextListener
        get() = object : SearchView.OnQueryTextListener {
            private var text: String = ""
            internal var mFilterTask: Runnable = Runnable {
                kanjiListAdapter.search(text)
                result?.text = String.format(Locale.getDefault(), "%d items displayed", kanjiListAdapter.count)
            }
            private val mHandler = Handler()

            override fun onQueryTextSubmit(query: String): Boolean {
                text = query
                mHandler.removeCallbacks(mFilterTask)
                mHandler.postDelayed(mFilterTask, 0)
                hideKeyboard()
                kanjiList?.requestFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                text = newText
                suggestionAdapter.populateSuggestions(getLastPart(newText))
                mHandler.removeCallbacks(mFilterTask)
                mHandler.postDelayed(mFilterTask, 1000)
                return true
            }
        }

    private//if there's a comma in the search query, it means multiple terms
    //in that case we don't want to replace the whole query
    val suggestionListener: SearchView.OnSuggestionListener
        get() = object : SearchView.OnSuggestionListener {
            override fun onSuggestionSelect(position: Int): Boolean {
                val cursor = searchView?.suggestionsAdapter?.getItem(position) as Cursor
                val choice = cursor.getString(1)
                var query = searchView?.query.toString()
                query = query.replace(getLastPart(query), choice)
                searchView?.setQuery(query, false)
                return true
            }

            override fun onSuggestionClick(position: Int): Boolean {
                val cursor = searchView?.suggestionsAdapter?.getItem(position) as Cursor
                val choice = cursor.getString(1)
                var query = searchView?.query.toString()
                query = query.replace(getLastPart(query), choice)
                searchView?.setQuery(query, false)
                return true
            }
        }

    /**
     * Allow list adapter to observe the joyo filter state
     */
    val joyoFilter: FilterState
        get() = kanjiListFilterAdapter.joyoFilter

    /**
     * Allow list adapter to observe the keyword filter state
     */
    val keywordFilter: FilterState
        get() = kanjiListFilterAdapter.keywordFilter

    /**
     * Allow list adapter to observe the story filter state
     */
    val storyFilter: FilterState
        get() = kanjiListFilterAdapter.storyFilter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //if onCreate was called after rotation, we need saved bundle for options menu
        this.savedInstanceState = savedInstanceState

        setContentView(R.layout.activity_kanji_list)

        kanjiList = findViewById(R.id.kanjiListView)
        suggestionAdapter = cursorAdapter
        val spinnerValues = arrayOf("n/a, Yes, No")
        kanjiListFilterAdapter = KanjiListFilterAdapter(this, R.id.filter_spinner, spinnerValues)
        kanjiListAdapter = KanjiListAdapter(this, savedInstanceState)
        kanjiList?.adapter = kanjiListAdapter
        result = findViewById(R.id.result)
        joyoFilterState = findViewById(R.id.joyo_filter_state)
        keywordFilterState = findViewById(R.id.keyword_filter_state)
        storyFilterState = findViewById(R.id.story_filter_state)
        //created here because must be re-created if list activity is destroyed
        spinnerListener = object : OnSpinnerEventsListener {
            //if filter values changed, we should perform a search with new values
            internal var changed: Boolean = false

            internal var mFilterTask: Runnable = Runnable {
                kanjiListAdapter.search(searchView?.query.toString())
                result?.text = String.format(Locale.getDefault(), "%d items displayed", kanjiListAdapter.count)
            }
            private val mHandler = Handler()

            override fun notifyContentsChange() {
                changed = true
            }

            override fun onSpinnerOpened() {
                changed = false
            }

            override fun onSpinnerClosed() {
                if (changed) {
                    mHandler.postDelayed(mFilterTask, 0)
                }
            }
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        // mSpin is our custom Spinner
        if (spinnerFilter?.hasBeenOpened() == true && hasFocus) {
            spinnerFilter?.performClosedEvent()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_kanji_list, menu)

        val filterItem = menu.findItem(R.id.filter_spinner)
        spinnerFilter = filterItem.actionView as SpinnerFilter
        spinnerFilter?.adapter = kanjiListFilterAdapter
        spinnerFilter?.setSpinnerEventsListener(spinnerListener)

        // we want to be able to filter the search results!
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchItem = menu.findItem(R.id.kanji_list_search)
        searchView = searchItem.actionView as KanjiSearchView
        searchView?.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView?.setOnQueryTextListener(textListener)
        searchView?.setOnSuggestionListener(suggestionListener)
        searchView?.suggestionsAdapter = suggestionAdapter

        //if device rotated, restore values here!
        if (savedInstanceState != null) {
            kanjiListFilterAdapter.setJoyoFilter(savedInstanceState?.getInt("joyoFilter", 0))
            kanjiListFilterAdapter.setKeywordFilter(savedInstanceState?.getInt("keywordFilter", 0))
            kanjiListFilterAdapter.setStoryFilter(savedInstanceState?.getInt("storyFilter", 0))
            val query = savedInstanceState?.getString("searchQuery")
            //trigger a search with the onTextChanged Listener
            searchView?.setQuery(query, true)
        }

        notifyFilterChanged()

        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
            R.id.action_settings -> return true
            R.id.action_primitives -> {
                showPrimitives()
                return true
            }
            R.id.action_import_story -> {
                importStory()
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    private fun showPrimitives() {
        startActivity(Intent(this, PrimitiveListActivity::class.java))
    }

    private fun importStory() {
        startActivityForResult(Intent(this, ImportStoryActivity::class.java), 2)
    }

    /**
     * If there's a comma, we're only interested in last part after comma
     * for suggestions
     */
    private fun getLastPart(query: String): String {
        if (query.lastIndexOf(",") == -1) {
            return query
        } else if (query.endsWith(",")) {
            return ""
        }

        val parts = query.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

        return parts[parts.size - 1]
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == KanjiDetailActivity.ACTIVITY_CODE) { //Kanji Detail Activity
                val heisigIds = data?.getIntArrayExtra("heisigIds")
                val keywords = data?.getStringArrayExtra("keywords")

                //user didn't modify anything
                if (heisigIds == null || keywords == null) {
                    return
                } else if (heisigIds.size != keywords.size) {
                    Log.e("KanjiListActivity", "Array of Keyword ids and texts aren't same size!")
                    return
                }

                //user modified kanji(s) in the detail view, so update indicators
                for (i in heisigIds.indices) {
                    kanjiListAdapter.updateKeyword(heisigIds[i], keywords[i])
                    kanjiListAdapter.updateIndicatorVisibilityWithId(heisigIds[i]) //change to 0-indexed
                }

                kanjiListAdapter.notifyDataSetChanged()
                //if we return from detail view, and something has changed, resubmit the search query by triggering the filter's onClosed listener
                spinnerListener?.onSpinnerClosed()
            } else if (requestCode == RETURN_CODE_IMPORT_STORY_ACTIVITY) { //Import Stories Activity
                var heisigIds: IntArray
                if(data != null) {
                    heisigIds = data.getIntArrayExtra("heisigIds")
                } else {
                    throw IllegalArgumentException("The Imported Heisig Ids are not allowed to be empty")
                }
                kanjiListAdapter.initialiseUserKeywordsAndStories()
                for (heisigId in heisigIds) {
                    kanjiListAdapter.updateIndicatorVisibilityWithId(heisigId)
                }

                kanjiListAdapter.notifyDataSetChanged()
                spinnerListener?.onSpinnerClosed() //trigger list view refresh
            }
        }
    }

    fun hideKeyboard() {
        val inputManager = this.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(searchView?.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
    }

    fun notifyFilterChanged() {
        spinnerListener?.notifyContentsChange()
        updateFilterIndicators()
    }

    /**
     * The textual indicators above the kanji list, describing the state of the filter toggles
     */
    private fun updateFilterIndicators() {
        val joyo = getString(R.string.filter_state_joyo)
        val keyword = getString(R.string.filter_state_keyword)
        val story = getString(R.string.filter_state_story)

        val unset = "  "
        val yes = "O"
        val no = "X"

        when (joyoFilter) {
            FilterState.UNSET -> joyoFilterState?.text = String.format(joyo, unset)
            FilterState.YES -> joyoFilterState?.text = String.format(joyo, yes)
            FilterState.NO -> joyoFilterState?.text = String.format(joyo, no)
        }

        when (keywordFilter) {
            FilterState.UNSET -> keywordFilterState?.text = String.format(keyword, unset)
            FilterState.YES -> keywordFilterState?.text = String.format(keyword, yes)
            FilterState.NO -> keywordFilterState?.text = String.format(keyword, no)
        }

        when (storyFilter) {
            FilterState.UNSET -> storyFilterState?.text = String.format(story, unset)
            FilterState.YES -> storyFilterState?.text = String.format(story, yes)
            FilterState.NO -> storyFilterState?.text = String.format(story, no)
        }
    }

    /**
     * When rotation changes, we need to save...
     * Search query
     * Filters
     */
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("searchQuery", searchView?.query.toString())
        outState.putInt("joyoFilter", kanjiListFilterAdapter.joyoFilter.stateNum)
        outState.putInt("keywordFilter", kanjiListFilterAdapter.keywordFilter.stateNum)
        outState.putInt("storyFilter", kanjiListFilterAdapter.storyFilter.stateNum)
        super.onSaveInstanceState(outState)
    }
}