package tech.diarmaid.koohiiaite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.material.button.MaterialButtonToggleGroup
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.activity.KanjiListActivity
import tech.diarmaid.koohiiaite.enumeration.FilterState

/**
 * Adapter for the spinner in the kanji list
 */
class KanjiListFilterAdapter(private val mContext: Context, resource: Int, objects: Array<String>) : ArrayAdapter<String>(mContext, resource, objects) {
    private val layoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    var joyoFilter = FilterState.UNSET
    var keywordFilter = FilterState.UNSET
    var storyFilter = FilterState.UNSET

    override fun getDropDownView(position: Int, cnvtView: View?, prnt: ViewGroup): View {
        return getCustomView(cnvtView, prnt)
    }

    override fun getView(pos: Int, cnvtView: View?, prnt: ViewGroup): View {
        val text = TextView(context)
        text.setText(R.string.filter_hint)
        return text
    }

    private fun getCustomView(aConvertView: View?, parent: ViewGroup): View {
        var convertView = aConvertView
        val viewHolder: ViewHolderItem
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.spinner_filter, parent, false)
            viewHolder = ViewHolderItem()
            viewHolder.joyoGroup = convertView!!.findViewById(R.id.toggle_joyo)
            viewHolder.keywordGroup = convertView.findViewById(R.id.toggle_keyword)
            viewHolder.storyGroup = convertView.findViewById(R.id.toggle_story)

            // Set initial states
            setToggleGroupState(viewHolder.joyoGroup, joyoFilter.stateNum)
            setToggleGroupState(viewHolder.keywordGroup, keywordFilter.stateNum)
            setToggleGroupState(viewHolder.storyGroup, storyFilter.stateNum)

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolderItem
        }

        viewHolder.joyoGroup!!.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val state = when (checkedId) {
                    R.id.toggle_joyo_all -> FilterState.UNSET
                    R.id.toggle_joyo_yes -> FilterState.YES
                    R.id.toggle_joyo_no -> FilterState.NO
                    else -> FilterState.UNSET
                }
                joyoFilter = state
                if (mContext is KanjiListActivity) {
                    mContext.notifyFilterChanged()
                }
            }
        }

        viewHolder.keywordGroup!!.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val state = when (checkedId) {
                    R.id.toggle_keyword_all -> FilterState.UNSET
                    R.id.toggle_keyword_yes -> FilterState.YES
                    R.id.toggle_keyword_no -> FilterState.NO
                    else -> FilterState.UNSET
                }
                keywordFilter = state
                if (mContext is KanjiListActivity) {
                    mContext.notifyFilterChanged()
                }
            }
        }

        viewHolder.storyGroup!!.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                val state = when (checkedId) {
                    R.id.toggle_story_all -> FilterState.UNSET
                    R.id.toggle_story_yes -> FilterState.YES
                    R.id.toggle_story_no -> FilterState.NO
                    else -> FilterState.UNSET
                }
                storyFilter = state
                if (mContext is KanjiListActivity) {
                    mContext.notifyFilterChanged()
                }
            }
        }

        return convertView
    }

    fun setJoyoFilter(value: Int?) {
        joyoFilter = FilterState.getStateFor(value!!)
    }

    fun setKeywordFilter(value: Int?) {
        keywordFilter = FilterState.getStateFor(value!!)
    }

    fun setStoryFilter(value: Int?) {
        storyFilter = FilterState.getStateFor(value!!)
    }

    private fun setToggleGroupState(group: MaterialButtonToggleGroup?, state: Int) {
        if (group == null) return
        val buttonId = when (state) {
            0 -> group.getChildAt(0).id // All
            1 -> group.getChildAt(1).id // Yes
            2 -> group.getChildAt(2).id // No
            else -> group.getChildAt(0).id
        }
        group.check(buttonId)
    }

    internal class ViewHolderItem {
        var joyoGroup: MaterialButtonToggleGroup? = null
        var keywordGroup: MaterialButtonToggleGroup? = null
        var storyGroup: MaterialButtonToggleGroup? = null
    }
}
