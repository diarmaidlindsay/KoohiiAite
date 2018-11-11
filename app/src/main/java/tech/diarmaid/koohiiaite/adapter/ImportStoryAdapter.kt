package tech.diarmaid.koohiiaite.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.model.CSVEntry
import java.util.*

/**
 * Adapter for the List View in the Activity which displays importedStories CSV
 */
class ImportStoryAdapter(mContext: Context) : BaseAdapter() {

    internal val importedStories = ArrayList<CSVEntry>()
    private val layoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    fun setStories(stories: List<CSVEntry>) {
        importedStories.addAll(stories)
    }

    override fun getCount(): Int {
        return importedStories.size
    }

    override fun getItem(position: Int): Any {
        return importedStories[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, aConvertView: View?, parent: ViewGroup): View {
        var convertView = aConvertView
        val viewHolder: ViewHolderItem
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_import_story, parent, false)
            viewHolder = ViewHolderItem()
            viewHolder.id = convertView!!.findViewById(R.id.csv_id)
            viewHolder.kanji = convertView.findViewById(R.id.csv_kanji)
            viewHolder.keyword = convertView.findViewById(R.id.csv_keyword)
            viewHolder.story = convertView.findViewById(R.id.csv_story)

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolderItem
        }

        val entry = getItem(position) as CSVEntry

        viewHolder.id!!.text = entry.id
        viewHolder.kanji!!.text = entry.kanji
        viewHolder.keyword!!.text = entry.keyword
        viewHolder.story!!.text = entry.story

        return convertView
    }

    fun clearStories() {
        importedStories.clear()
    }

    internal class ViewHolderItem {
        var id: TextView? = null
        var kanji: TextView? = null
        var keyword: TextView? = null
        var story: TextView? = null
    }
}
