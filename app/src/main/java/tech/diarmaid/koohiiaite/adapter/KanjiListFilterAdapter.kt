package tech.diarmaid.koohiiaite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView

import org.honorato.multistatetogglebutton.MultiStateToggleButton

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
            viewHolder.joyoButton = convertView!!.findViewById(R.id.toggle_joyo)
            viewHolder.keywordButton = convertView.findViewById(R.id.toggle_keyword)
            viewHolder.storyButton = convertView.findViewById(R.id.toggle_story)
            viewHolder.joyoButton!!.value = joyoFilter.stateNum
            viewHolder.keywordButton!!.value = keywordFilter.stateNum
            viewHolder.storyButton!!.value = storyFilter.stateNum

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolderItem
        }

        viewHolder.joyoButton!!.setOnValueChangedListener { i ->
            joyoFilter = FilterState.getStateFor(i)
            if (mContext is KanjiListActivity) {
                mContext.notifyFilterChanged()
            }
        }

        viewHolder.keywordButton!!.setOnValueChangedListener { i ->
            keywordFilter = FilterState.getStateFor(i)
            if (mContext is KanjiListActivity) {
                mContext.notifyFilterChanged()
            }
        }

        viewHolder.storyButton!!.setOnValueChangedListener { i ->
            storyFilter = FilterState.getStateFor(i)
            if (mContext is KanjiListActivity) {
                mContext.notifyFilterChanged()
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

    internal class ViewHolderItem {
        var joyoButton: MultiStateToggleButton? = null
        var keywordButton: MultiStateToggleButton? = null
        var storyButton: MultiStateToggleButton? = null
    }
}
