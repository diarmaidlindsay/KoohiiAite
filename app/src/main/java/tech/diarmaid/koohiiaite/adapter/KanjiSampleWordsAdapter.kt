package tech.diarmaid.koohiiaite.adapter

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.database.entity.SampleWord

/**
 * Adapter for the Sample words tab of the Kanji Detail view
 */
class KanjiSampleWordsAdapter(private val mContext: Context, args: Bundle, val sampleWordList: List<SampleWord>) : BaseAdapter() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(mContext)

    internal class ViewHolderItem {
        var kanji: TextView? = null
        var hiragana: TextView? = null
        var english: TextView? = null
        var category: TextView? = null
        var frequency: TextView? = null
    }

    override fun getCount(): Int {
        return sampleWordList.size
    }

    override fun getItem(position: Int): Any {
        return sampleWordList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, aConvertView: View?, parent: ViewGroup): View {
        var convertView = aConvertView
        val viewHolder: ViewHolderItem
        if (convertView == null) {
            convertView = layoutInflater.inflate(R.layout.list_item_sample_word, parent, false)
            viewHolder = ViewHolderItem()
            viewHolder.kanji = convertView!!.findViewById(R.id.sample_kanji)
            viewHolder.hiragana = convertView.findViewById(R.id.sample_hiragana)
            viewHolder.english = convertView.findViewById(R.id.sample_english)
            viewHolder.category = convertView.findViewById(R.id.sample_category)
            viewHolder.frequency = convertView.findViewById(R.id.sample_frequency)

            convertView.tag = viewHolder
        } else {
            viewHolder = convertView.tag as ViewHolderItem
        }

        val sampleWord = getItem(position) as SampleWord

        viewHolder.kanji!!.text = sampleWord.kanjiWord
        viewHolder.hiragana!!.text = sampleWord.hiraganaReading
        viewHolder.english!!.text = sampleWord.englishMeaning
        viewHolder.category!!.text = sampleWord.category
        viewHolder.frequency!!.text = sampleWord.frequency.toString()

        return convertView
    }
}
