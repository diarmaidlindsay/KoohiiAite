package tech.diarmaid.koohiiaite.adapter

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tech.diarmaid.koohiiaite.database.AppDatabase
import tech.diarmaid.koohiiaite.fragment.DictionaryFragment
import tech.diarmaid.koohiiaite.fragment.KoohiiFragment
import tech.diarmaid.koohiiaite.fragment.SampleWordsFragment
import tech.diarmaid.koohiiaite.fragment.StoryFragment
import tech.diarmaid.koohiiaite.utils.Constants.KANJI_DETAIL_TABS

/**
 * Invoked when user clicks a list item in the KanjiListActivity
 */
class KanjiDetailAdapter(fragmentManager: FragmentManager, private val arguments: Bundle, private val mContext: Context) : FragmentPagerAdapter(fragmentManager) {
    private var storyFragment: StoryFragment? = null
    private var dictionaryFragment: DictionaryFragment? = null
    private var sampleWordsFragment: SampleWordsFragment? = null
    private var koohiiFragment: KoohiiFragment? = null

    init {
            val heisigId = arguments.getInt("heisigId")
            getKeywordFromDatabase(heisigId).observe(mContext as AppCompatActivity, Observer {
                arguments.putString("keyword", it)
            })
            getUserKeywordFromDatabase(heisigId).observe(mContext, Observer {
                arguments.putString("userKeyword", it)
            })
            getKanjiFromDatabase(heisigId).observe(mContext, Observer {
                arguments.putString("kanji", it)
            })

        //TODO : The getItem() method is being called BEFORE these db queries return
    }

    private fun getKanjiFromDatabase(heisigId: Int): LiveData<String> {
        val data = MutableLiveData<String>()
        GlobalScope.launch {
            launch(Dispatchers.IO) {
                val dataSource = AppDatabase.getDatabase(mContext).heisigKanjiDao()
                data.postValue(dataSource.getKanjiFor(heisigId).kanji)
            }
        }
        return data
    }

    private fun getKeywordFromDatabase(heisigId: Int): LiveData<String> {
        val data = MutableLiveData<String>()
        GlobalScope.launch {
            launch(Dispatchers.IO) {
                val dataSource = AppDatabase.getDatabase(mContext).keywordDao()
                data.postValue(dataSource.getKeywordFor(heisigId).keywordText)
            }
        }
        return data
    }

    private fun getUserKeywordFromDatabase(heisigId: Int): LiveData<String?> {
        val data = MutableLiveData<String>()
        GlobalScope.launch {
            launch(Dispatchers.IO) {
                val dataSource = AppDatabase.getDatabase(mContext).userKeywordDao()
                data.postValue(dataSource.getKeywordFor(heisigId)?.keywordText)
            }
        }
        return data
    }

    // Returns total number of pages
    override fun getCount(): Int {
        return KANJI_DETAIL_TABS
    }

    // Returns the fragment to display for that page
    override fun getItem(position: Int): Fragment? {
        when (position) {
            0 -> {
                if (storyFragment == null) {
                    storyFragment = StoryFragment()
                    storyFragment!!.arguments = arguments
                }
                return storyFragment
            }
            1 -> {
                if (dictionaryFragment == null) {
                    dictionaryFragment = DictionaryFragment()
                    dictionaryFragment!!.arguments = arguments
                }
                return dictionaryFragment
            }
            2 -> {
                if (sampleWordsFragment == null) {
                    sampleWordsFragment = SampleWordsFragment()
                    sampleWordsFragment!!.arguments = arguments
                }
                return sampleWordsFragment
            }
            3 -> {
                if (koohiiFragment == null) {
                    koohiiFragment = KoohiiFragment()
                    koohiiFragment!!.arguments = arguments
                }
                return koohiiFragment
            }
            else -> return null
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
