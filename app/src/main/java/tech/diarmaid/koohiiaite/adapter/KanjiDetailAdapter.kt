package tech.diarmaid.koohiiaite.adapter

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import tech.diarmaid.koohiiaite.database.dao.HeisigKanjiDataSource
import tech.diarmaid.koohiiaite.database.dao.KeywordDataSource
import tech.diarmaid.koohiiaite.database.dao.UserKeywordDataSource
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
        val keyword = getKeywordFromDatabase(heisigId)
        val userKeyword = getUserKeywordFromDatabase(heisigId)
        val kanji = getKanjiFromDatabase(heisigId)
        arguments.putString("keyword", keyword)
        arguments.putString("userKeyword", userKeyword)
        arguments.putString("kanji", kanji)
    }

    private fun getKanjiFromDatabase(heisigId: Int): String {
        val dataSource = HeisigKanjiDataSource(mContext)
        dataSource.open()
        val heisigKanji = dataSource.getKanjiFor(heisigId)
        dataSource.close()

        return heisigKanji.kanji
    }

    private fun getKeywordFromDatabase(heisigId: Int): String {
        val dataSource = KeywordDataSource(mContext)
        dataSource.open()
        val keyword = dataSource.getKeywordFor(heisigId)
        dataSource.close()

        return keyword.keywordText
    }

    private fun getUserKeywordFromDatabase(heisigId: Int): String? {
        val dataSource = UserKeywordDataSource(mContext)
        dataSource.open()
        val keyword = dataSource.getKeywordFor(heisigId)
        dataSource.close()

        return keyword?.keywordText
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
