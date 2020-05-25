package tech.diarmaid.koohiiaite.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils
import kotlinx.android.synthetic.main.fragment_detail.*
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.fragment.KanjiDetailFragment
import tech.diarmaid.koohiiaite.utils.Utils
import java.util.*


class KanjiDetailActivity : AppCompatActivity() {
    private var currentIndex: Int = 0
    private var filteredIdList: Array<String>? = null
    private val changedIds = ArrayList<Int>()
    private val changedKeywords = ArrayList<String>()
    private var prevButton: MenuItem? = null
    private var nextButton: MenuItem? = null

    /**
     * Ask the view pager in the child fragment manager which page index it is currently displaying.
     * Used for pressing next/prev and preserving the tab index.
     */
    private val currentPagerIndex: Int
        get() {
            val fragmentList = supportFragmentManager.fragments
            if (fragmentList.isNotEmpty()) {
                val fragment = fragmentList[fragmentList.size - 1]
                if (fragment is KanjiDetailFragment) {
                    return fragment.vpPager.currentItem
                }
            }

            return -1
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_kanji_detail)

        val arguments = intent.extras
        currentIndex = arguments!!.getInt("filteredListIndex")
        filteredIdList = arguments.getStringArray("filteredIdList")
        if (savedInstanceState != null) {
            swapFragment(savedInstanceState.getInt("currentIndex"))
        } else {
            swapFragment(currentIndex)
        }
    }

    override fun onResume() {
        super.onResume()
        //if you click a link in the story and come back, we need to refresh button state
        updateButtonEnablement()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_kanji_detail, menu)
        prevButton = menu.findItem(R.id.action_previous)
        nextButton = menu.findItem(R.id.action_next)
        updateButtonEnablement()
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId

        when (id) {
        // Respond to the action bar's Up/Home button
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                return true
            }
            R.id.action_previous -> {
                swapFragment(--currentIndex)
                return true
            }
            R.id.action_next -> {
                swapFragment(++currentIndex)
                return true
            }
            R.id.action_settings ->
                //handle settings here
                return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun swapFragment(newIndex: Int) {
        val fragment = KanjiDetailFragment(Integer.parseInt(filteredIdList!![newIndex]))
        val bundle = Bundle()
        //preserve viewpager index during next/prev, else set to 0 if none exists
        bundle.putInt("currentPage", if (currentPagerIndex == -1) 0 else currentPagerIndex)
        fragment.arguments = bundle
        // Begin the transaction
        val ft = supportFragmentManager.beginTransaction()
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.detail_fragment_framelayout, fragment)
        // Complete the changes added above
        ft.commit()
        updateButtonEnablement()
    }

    fun setResult(heisigId: Int, keyword: String) {
        val returnIntent = Intent()
        changedIds.add(heisigId)
        changedKeywords.add(keyword)
        returnIntent.putExtra("heisigIds", Utils.toIntArray(changedIds))
        returnIntent.putExtra("keywords", changedKeywords.toTypedArray())
        setResult(Activity.RESULT_OK, returnIntent)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("currentIndex", currentIndex)
    }

    /**
     * Manage Prev/Next button state
     */
    private fun updateButtonEnablement() {
        if (prevButton != null && nextButton != null) {
            if (currentIndex == 0) {
                prevButton?.isEnabled = false
                prevButton?.icon?.alpha = 130
            } else {
                prevButton?.isEnabled = true
                prevButton?.icon?.alpha = 255
            }

            if (currentIndex == filteredIdList?.size?.minus(1) ?: 0) {
                nextButton?.isEnabled = false
                nextButton?.icon?.alpha = 130

            } else {
                nextButton?.isEnabled = true
                nextButton?.icon?.alpha = 255
            }
        }

    }

    companion object {

        const val ACTIVITY_CODE = 1
    }
}
