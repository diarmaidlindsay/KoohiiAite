package tech.diarmaid.koohiiaite.adapter

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import tech.diarmaid.koohiiaite.database.AppDatabase
import tech.diarmaid.koohiiaite.fragment.DictionaryFragment
import tech.diarmaid.koohiiaite.fragment.KoohiiFragment
import tech.diarmaid.koohiiaite.fragment.SampleWordsFragment
import tech.diarmaid.koohiiaite.fragment.StoryFragment
import tech.diarmaid.koohiiaite.utils.Constants.KANJI_DETAIL_TABS
import tech.diarmaid.koohiiaite.viewmodel.KanjiDetailViewModel
import kotlin.coroutines.CoroutineContext

/**
 * Invoked when user clicks a list item in the KanjiListActivity
 */
class KanjiDetailAdapter(fragmentManager: FragmentManager, private val arguments: Bundle, private val mContext: Context, viewModel: KanjiDetailViewModel?, viewLifecycleOwner: LifecycleOwner) : FragmentPagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT), CoroutineScope {
    private var storyFragment: StoryFragment? = null
    private var dictionaryFragment: DictionaryFragment? = null
    private var sampleWordsFragment: SampleWordsFragment? = null
    private var koohiiFragment: KoohiiFragment? = null
    override val coroutineContext: CoroutineContext
        get() = Dispatchers.Default

    init {
        viewModel?.heisigId?.observe(viewLifecycleOwner, Observer { heisigId ->
            if (heisigId > 0) {
                launch(Dispatchers.IO) {
                    val kanji = AppDatabase.getDatabase(mContext).heisigKanjiDao().getKanjiFor(heisigId)?.kanji
                    val keyword = AppDatabase.getDatabase(mContext).keywordDao().getKeywordFor(heisigId)?.keywordText
                    val userKeyword = AppDatabase.getDatabase(mContext).userKeywordDao().getKeywordFor(heisigId)?.keywordText
                            ?: ""
                    Log.d("KanjiDetailAdapter", "Async id: $heisigId, kanji $kanji, keyword : $keyword, userkeyword : $userKeyword")
                    launch(Dispatchers.Main) {
                        viewModel.kanji.value = kanji
                        viewModel.keyword.value = keyword
                        viewModel.userKeyword.value = userKeyword
                        Log.d("KanjiDetailAdapter", "Posted id: $heisigId, kanji $kanji, keyword : $keyword, userkeyword : $userKeyword")
                    }
                }
            }
        })
    }

    private fun getKanjiFromDatabase(heisigId: Int): LiveData<String> {
        val data = MutableLiveData<String>()
        launch(Dispatchers.IO) {
            val dataSource = AppDatabase.getDatabase(mContext).heisigKanjiDao()
            data.postValue(dataSource.getKanjiFor(heisigId)?.kanji)
        }
        return data
    }

    private fun getKeywordFromDatabase(heisigId: Int): LiveData<String> {
        val data = MutableLiveData<String>()
        launch(Dispatchers.IO) {
            val dataSource = AppDatabase.getDatabase(mContext).keywordDao()
            data.postValue(dataSource.getKeywordFor(heisigId)?.keywordText)
        }
        return data
    }

    private fun getUserKeywordFromDatabase(heisigId: Int): LiveData<String?> {
        val data = MutableLiveData<String>()
        launch(Dispatchers.IO) {
            val dataSource = AppDatabase.getDatabase(mContext).userKeywordDao()
            data.postValue(dataSource.getKeywordFor(heisigId)?.keywordText)
        }
        return data
    }

    // Returns total number of pages
    override fun getCount(): Int {
        return KANJI_DETAIL_TABS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment {
        when (position) {
            0 -> {
                if (storyFragment == null) {
                    storyFragment = StoryFragment()
                    storyFragment?.arguments = arguments
                }
                return storyFragment as StoryFragment
            }
            1 -> {
                if (dictionaryFragment == null) {
                    dictionaryFragment = DictionaryFragment()
                    dictionaryFragment?.arguments = arguments
                }
                return dictionaryFragment as DictionaryFragment
            }
            2 -> {
                if (sampleWordsFragment == null) {
                    sampleWordsFragment = SampleWordsFragment()
                    sampleWordsFragment?.arguments = arguments
                }
                return sampleWordsFragment as SampleWordsFragment
            }
            3 -> {
                if (koohiiFragment == null) {
                    koohiiFragment = KoohiiFragment()
                    koohiiFragment?.arguments = arguments
                }
                return koohiiFragment as KoohiiFragment
            }
            else -> return getItem(0)
        }
    }

    // Returns the page title for the top indicator
    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> "Story"
            1 -> "Dictionary"
            2 -> "Sample Words"
            3 -> "Koohii"
            else -> "Undefined"
        }
    }
}
