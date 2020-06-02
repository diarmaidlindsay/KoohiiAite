package tech.diarmaid.koohiiaite.adapter

import android.content.Context
import android.content.Intent
import android.util.Log
import android.util.SparseArray
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.activity.KanjiDetailActivity
import tech.diarmaid.koohiiaite.activity.KanjiListActivity
import tech.diarmaid.koohiiaite.database.AppDatabase
import tech.diarmaid.koohiiaite.database.entity.HeisigKanji
import tech.diarmaid.koohiiaite.database.entity.HeisigToPrimitive
import tech.diarmaid.koohiiaite.database.entity.Keyword
import tech.diarmaid.koohiiaite.database.entity.Primitive
import tech.diarmaid.koohiiaite.enumeration.FilterState
import tech.diarmaid.koohiiaite.utils.Utils
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Adapter for Main Kanji ListView
 */
class KanjiListAdapter(private val mContext: Context) : BaseAdapter(), CoroutineScope {
    private var viewHolder: ViewHolderItem? = null

    var allHeisigKanji: List<HeisigKanji> = ArrayList() //list of all HeisigKanjis
    var allKeywords: List<Keyword> = ArrayList() //list of all Keywords
    var allPrimitives: List<Primitive> = ArrayList() //list of all Primitives
    var primitivesForHeisigId: Map<Int, List<String>> = HashMap()
    private var heisigKanjiToUserKeyword: SparseArray<String> = SparseArray() //HashMap of User Keywords (id, text)

    //to save memory, only story booleans to indicate whether a list item has a story
    private var storyList: MutableLiveData<List<Boolean>> = MutableLiveData()

    private val filteredHeisigKanjiSet = HashSet<Int>() //filtered heisig_ids
    private val filteredHeisigKanjiList = ArrayList<HeisigKanji>() //filtered HeisigKanjis for display
    private var filteredHeisigToPrimitiveList: MutableList<HeisigToPrimitive> = mutableListOf() //filtered list of Primitives for display

    private val layoutInflater: LayoutInflater = LayoutInflater.from(mContext)
    override val coroutineContext: CoroutineContext = Dispatchers.Default

    /**
     * Called after a CSV import
     */
    fun initialiseUserKeywordsAndStories() {
        launch(Dispatchers.IO) {
            Log.d("KanjiListAdapter", "allHeisigKanji size ${allHeisigKanji.size}")
            storyList.postValue(AppDatabase.getDatabase(mContext).storyDao().getStoryFlags(allHeisigKanji.size))
            val allUserKeywords = AppDatabase.getDatabase(mContext).userKeywordDao().allUserKeywords()
            launch(Dispatchers.Main) {
                allUserKeywords.observe((mContext as AppCompatActivity), androidx.lifecycle.Observer { userKeywords ->
                    userKeywords.forEach { heisigKanjiToUserKeyword.put(it.heisigId, it.keywordText) }
                })
            }
        }
        storyList.observe((mContext as AppCompatActivity), androidx.lifecycle.Observer {
            search("")
        })
    }

    override fun getCount(): Int {
        return filteredHeisigKanjiList.size
    }

    override fun getItem(position: Int): Any {
        return filteredHeisigKanjiList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, aConvertView: View?, parent: ViewGroup): View {
        var convertView = aConvertView

        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_kanji, parent, false)
            viewHolder = ViewHolderItem()
            viewHolder?.heisig = convertView?.findViewById(R.id.heisig_id_list_item)
            viewHolder?.kanji = convertView.findViewById(R.id.kanji_list_item)
            viewHolder?.keyword = convertView.findViewById(R.id.keyword_list_item)
            viewHolder?.primitives = convertView.findViewById(R.id.primitives_list_item)
            viewHolder?.joyoIndicator = convertView.findViewById(R.id.list_indicator_joyo)
            viewHolder?.storyIndicator = convertView.findViewById(R.id.list_indicator_story)
            viewHolder?.keywordIndicator = convertView.findViewById(R.id.list_indicator_keyword)

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolderItem
        }

        val theKanji = getItem(position) as HeisigKanji

        val heisigId = theKanji.heisigId

        val primitiveText = StringBuilder()
        primitivesForHeisigId[heisigId]?.forEachIndexed { idx, primitive ->
            primitiveText.append(primitive)
            if (idx < primitivesForHeisigId[heisigId]?.size?.minus(1) ?: 0) {
                primitiveText.append(", ")
            }
        }

        val kanji = theKanji.kanji
        val userKeyword = heisigKanjiToUserKeyword.get(heisigId)
        //if user entered their own keyword, use it, else use default keyword
        val keyword = userKeyword ?: allKeywords[heisigId - 1].keywordText

        viewHolder?.heisig?.text = HeisigKanji.getHeisigIdAsString(heisigId)
        viewHolder?.kanji?.text = kanji
        viewHolder?.keyword?.text = keyword
        viewHolder?.primitives?.text = primitiveText.toString()
        // Listen for ListView Item Click
        convertView?.setOnClickListener {
            //collapse search box and hide keyboard
            if (mContext is KanjiListActivity) {
                mContext.hideKeyboard()
                val intent = Intent(mContext, KanjiDetailActivity::class.java)
                intent.putExtra("filteredListIndex", position)
                //put all the filtered heisig ids for next/prev navigation
                intent.putExtra("filteredIdList", HeisigKanji.getIds1Indexed(filteredHeisigKanjiList).toTypedArray())
                mContext.startActivityForResult(intent, KanjiDetailActivity.ACTIVITY_CODE)
            } else {
                Toast.makeText(mContext, "Context was not KanjiListActivity", Toast.LENGTH_SHORT).show()
            }
        }

        //populate the indicators, to indicate if kanji is joyo, has custom keyword or story written
        updateIndicatorVisibilityAtPos(position)

        return convertView!!
    }

    /**
     * Search the Kanji list with the given search string
     */
    fun search(aSearchString: String) {
        var searchString = aSearchString
        searchString = searchString.toLowerCase(Locale.getDefault())
        filteredHeisigKanjiSet.clear()

        if (searchString.isNotEmpty()) {
            when {
                Utils.isNumeric(searchString) -> {
                    filterOnId(searchString)
                }
                Utils.isKanji(searchString[0]) -> {
                    filterOnKanji(searchString)
                }
                else -> {
                    filterOnKeyword(searchString)
                    filterOnUserKeyword(searchString)
                    filterOnPrimitives(searchString)
                }
            }
        }

        updateFilteredList(searchString)
        applyFilters()
        notifyDataSetChanged()
    }

    private fun updateFilteredList(filterText: String) {
        filteredHeisigKanjiList.clear()

        if (filterText.isEmpty()) {
            //if nothing is entered in search, display all
            filteredHeisigKanjiList.addAll(allHeisigKanji)

        } else {
            filteredHeisigKanjiList.addAll(HeisigKanji.getHeisigKanjiMatchingIds(
                    ArrayList(filteredHeisigKanjiSet), allHeisigKanji))
        }
    }

    /**
     * Iterate over masterList
     */
    private fun filterOnId(filterText: String) {
        //only run if string is number
        for (kanji in allHeisigKanji) {
            val id = kanji.heisigId.toString()

            if (id.contains(filterText)) {
                filteredHeisigKanjiSet.add(kanji.heisigId)
            }
        }
    }

    /**
     * Iterate over keywordList
     */
    private fun filterOnKeyword(filterText: String) {
        for (keyword in allKeywords) {
            if (keyword.keywordText.toLowerCase(Locale.getDefault()).contains(filterText)) {
                filteredHeisigKanjiSet.add(keyword.heisigId)
            }
        }
    }

    /**
     * Iterate over user keywords
     */
    private fun filterOnUserKeyword(filterText: String) {
        val mapSize = heisigKanjiToUserKeyword.size()
        for (i in 0 until mapSize) {
            val kanjiIndex = heisigKanjiToUserKeyword.keyAt(i)
            val userKeyword = heisigKanjiToUserKeyword.get(kanjiIndex)
            if (userKeyword.toLowerCase(Locale.getDefault()).contains(filterText)) {
                filteredHeisigKanjiSet.add(kanjiIndex)
            }
        }
    }

    /**
     * Iterate over masterList
     */
    private fun filterOnKanji(filterText: String) {
        for (kanjiCharacterFilter in filterText.toCharArray()) {
            for (kanji in allHeisigKanji) {
                val kanjiChar = kanji.kanji

                if (kanjiChar == kanjiCharacterFilter.toString()) {
                    filteredHeisigKanjiSet.add(kanji.heisigId)
                    break
                }
            }
        }
    }

    /**
     * If no comma, fuzzy search on 1 primitive string.
     * If comma, exact match on 1 or more primitive strings.
     * Can't do multiple fuzzy matches because would return too many results and difficult
     * to collate effectively and remove duplicates without intersection method.
     * Maybe fancy SQL could solve this?
     */
    private fun filterOnPrimitives(filterText: String) {
        if (filterText.isNotEmpty()) {
            val dataSource = AppDatabase.getDatabase(mContext).heisigToPrimitiveDao()
            var heisigIds: List<Int> = arrayListOf()

            //ie sun,moon
            if (filterText.contains(",")) {
                //if comma seperated primitive list, we're searching for many exact matches
                val primitiveSearchStrings = filterText.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                val primitiveMatches = ArrayList<Int>()
                //find primitive id which exactly matches string input
                for (primitiveString in primitiveSearchStrings) {
                    val primitiveInput = primitiveString.trim { it <= ' ' }
                    val primitiveIdMatched = Primitive.getPrimitiveIdWhichMatches(primitiveInput, allPrimitives, true)
                    if (primitiveIdMatched == -1) {
                        //no exact match found for primitive string
                        return
                    }
                    primitiveMatches.add(primitiveIdMatched)
                }

                val heisigIdsForPrimitiveIds = ArrayList<List<Int>>()

                //we've found the primitive ids matching the strings the user entered
                //which kanjis contain all these primitives?

                runBlocking {
                    launch(Dispatchers.IO) {
                        for (match in primitiveMatches) {
                            heisigIdsForPrimitiveIds.add(
                                    dataSource.getHeisigIdsMatching(arrayOf(match)))
                        }
                    }
                }
                //only retain the intersection of these matches, kanji which contain ALL these primitives
                heisigIds = intersection(heisigIdsForPrimitiveIds)
            } else {
                //if no comma, assume we're fuzzy searching for 1 primitive
                //ie night -> night, nightbreak
                runBlocking {
                    val primitiveIds = Primitive.getPrimitiveIdsContaining(filterText, allPrimitives, true)
                    launch(Dispatchers.IO) {
                        heisigIds = dataSource.getHeisigIdsMatching(primitiveIds.toTypedArray())
                    }
                }
            }

            if (heisigIds.isEmpty()) {
                return
            }

            filteredHeisigKanjiSet.addAll(heisigIds)
        }
    }

    /**
     * Apply the filter selections from the dropdown in the title bar.
     * If all 3 filters match, the kanji should be displayed, else it should be
     * removed from the filteredHeisigKanjiList.
     */
    private fun applyFilters() {
        if (mContext is KanjiListActivity) {
            val joyoFilter = mContext.joyoFilter
            val storyFilter = mContext.storyFilter
            val keywordFilter = mContext.keywordFilter

            val filteredOutKanji = ArrayList<HeisigKanji>()

            for (heisigKanji in filteredHeisigKanjiList) {

                val joyoMatch = isJoyoMatch(joyoFilter, heisigKanji)
                val storyMatch = isStoryMatch(storyFilter, heisigKanji)
                val keywordMatch = isKeywordMatch(keywordFilter, heisigKanji)

                if (!joyoMatch || !storyMatch || !keywordMatch) {
                    filteredOutKanji.add(heisigKanji)
                }
            }

            filteredHeisigKanjiList.removeAll(filteredOutKanji)
        }
    }

    /**
     * Does the heisig kanji match the user's JOYO filter choice?
     * Return true if it matches, else return false, meaning it should be filtered out.
     */
    private fun isJoyoMatch(state: FilterState, heisigKanji: HeisigKanji): Boolean {
        //If Kanji IS Joyo but user specified NOT joyo, or kanji NOT joyo but user specified IS joyo, it should be removed
        return !(heisigKanji.joyo && state == FilterState.NO || !heisigKanji.joyo && state == FilterState.YES)
    }

    /**
     * Does the heisig kanji match the user's KEYWORD filter choice?
     * Return true if it matches, else return false, meaning it should be filtered out.
     */
    private fun isKeywordMatch(state: FilterState, heisigKanji: HeisigKanji): Boolean {
        val heisigId = heisigKanji.heisigId

        return !(heisigKanjiToUserKeyword.get(heisigId) != null && state == FilterState.NO || heisigKanjiToUserKeyword.get(heisigId) == null && state == FilterState.YES)
    }

    /**
     * Does the heisig kanji match the user's STORY filter choice?
     * Return true if it matches, else return false, meaning it should be filtered out.
     */
    private fun isStoryMatch(state: FilterState, heisigKanji: HeisigKanji): Boolean {
        val heisigId = heisigKanji.heisigId

        return !(storyList.value!![heisigId - 1] && state == FilterState.NO || !storyList.value!![heisigId - 1] && state == FilterState.YES)
    }

    private fun intersection(matches: List<List<Int>>): List<Int> {
        val intersection = ArrayList<Int>()
        if (matches.isNotEmpty()) {
            intersection.addAll(matches[0])

            for (list in matches) {
                intersection.retainAll(list)
            }
        }

        return intersection
    }

    fun updateKeyword(heisigId: Int, keywordText: String) {
        //if the "updated" keyword is equal to the orignal, assume a revert to default and remove from user map
        if (allKeywords[heisigId - 1].keywordText == keywordText) {
            heisigKanjiToUserKeyword.remove(heisigId)
        } else {
            heisigKanjiToUserKeyword.put(heisigId, keywordText)
        }
    }

    /**
     * If we have the position in the filtered array, we can use this method
     * to update the indicators for
     * JOYO, KEYWORD and STORY for the given position in the list adaptor
     */
    private fun updateIndicatorVisibilityAtPos(position: Int) {
        val theKanji = getItem(position) as HeisigKanji
        updateIndicatorVisibilityWithId(theKanji.heisigId)
    }

    /**
     * Update the indicator based on a given heisig id, after these kanji
     * were modified in detail view
     */
    fun updateIndicatorVisibilityWithId(heisigId: Int) {
        if (viewHolder != null) {
            val theKanji = allHeisigKanji[heisigId - 1] //convert to 0-indexed

            if (theKanji.joyo) {
                viewHolder?.joyoIndicator?.visibility = View.VISIBLE
            } else {
                viewHolder?.joyoIndicator?.visibility = View.INVISIBLE
            }

            if (heisigKanjiToUserKeyword.get(heisigId) != null) {
                viewHolder?.keywordIndicator?.visibility = View.VISIBLE
            } else {
                viewHolder?.keywordIndicator?.visibility = View.INVISIBLE
            }

            if (storyList.value!![heisigId - 1]) {
                viewHolder?.storyIndicator?.visibility = View.VISIBLE
            } else {
                viewHolder?.storyIndicator?.visibility = View.INVISIBLE
            }
        }
    }

    /**
     * This prevents wasted cpu cycles by only inflating the view once
     * http://www.javacodegeeks.com/2013/09/android-viewholder-pattern-example.html
     */
    private class ViewHolderItem {
        internal var heisig: TextView? = null
        internal var kanji: TextView? = null
        internal var keyword: TextView? = null
        internal var primitives: TextView? = null
        internal var joyoIndicator: TextView? = null
        internal var storyIndicator: TextView? = null
        internal var keywordIndicator: TextView? = null
    }
}
