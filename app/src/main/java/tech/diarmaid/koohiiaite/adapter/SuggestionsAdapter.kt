package tech.diarmaid.koohiiaite.adapter

import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.provider.BaseColumns
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.cursoradapter.widget.SimpleCursorAdapter
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.database.dao.KeywordDataSource
import tech.diarmaid.koohiiaite.database.dao.PrimitiveDataSource
import tech.diarmaid.koohiiaite.model.Keyword
import tech.diarmaid.koohiiaite.model.Primitive
import java.text.Normalizer
import java.util.*

/**
 * For the suggestions list of the searchview inside the main kanji list activity
 */
class SuggestionsAdapter(context: Context, layout: Int, c: Cursor?, from: Array<String>, to: IntArray, flags: Int) : SimpleCursorAdapter(context, layout, c, from, to, flags) {
    private val allKeywords: List<Keyword>
    private val allPrimitives: List<Primitive>
    private var suggestionsList: MutableList<String>? = null
    private var previousQuery: String? = null
    private var queryNow: String? = null //should never become null

    init {
        val primitiveDataSource = PrimitiveDataSource(context)
        val keywordDataSource = KeywordDataSource(context)
        primitiveDataSource.open()
        keywordDataSource.open()
        allKeywords = keywordDataSource.allKeywords
        allPrimitives = primitiveDataSource.allPrimitives
        suggestionsList = ArrayList()
        keywordDataSource.close()
        primitiveDataSource.close()
    }

    override fun setViewText(v: TextView, text: String) {
        v.text = highlight(text, queryNow!!)
    }

    private fun highlight(originalText: String, search: String): CharSequence {
        if (search.length < 2) {
            return originalText
        }
        // ignore case and accents
        // the same thing should have been done for the search text
        val normalizedText = Normalizer
                .normalize(originalText, Normalizer.Form.NFD)
                .replace("\\p{InCombiningDiacriticalMarks}+".toRegex(), "")
                .toLowerCase(Locale.getDefault())
        var start = normalizedText.indexOf(search.toLowerCase(Locale.getDefault()))
        if (start < 0) {
            // not found, nothing to to
            return originalText
        } else {
            // highlight each appearance in the original text
            // while searching in normalized text
            val highlighted = SpannableString(originalText)
            while (start >= 0) {
                val spanStart = Math.min(start, originalText.length)
                val spanEnd = Math.min(start + search.length,
                        originalText.length)
                highlighted.setSpan(ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.ka_neutral)),
                        spanStart, spanEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

                start = normalizedText.indexOf(search, spanEnd)
            }
            return highlighted
        }
    }

    fun populateSuggestions(aQuery: String) {
        var query = aQuery
        val cursor = MatrixCursor(arrayOf(BaseColumns._ID, "keywordPrimitive"))

        //if there is a comma, only get suggestions for last part of query string
        if (query.contains(",")) {
            val queries = query.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            query = queries[queries.size - 1]
        }
        query = query.toLowerCase().trim { it <= ' ' }
        queryNow = query

        if (query.length < 2) {
            suggestionsList!!.clear()
            previousQuery = null
            changeCursor(cursor)
            return
        }
        val suggestionsSet = HashSet<String>()

        //some text was deleted so we should fall back to suggest from all primitives and keywords
        if (previousQuery == null || query.length < previousQuery!!.length) {
            for (primitive in allPrimitives) {
                if (primitive.primitiveText.toLowerCase().contains(query)) {
                    suggestionsSet.add(primitive.primitiveText)
                }
            }

            for (keyword in allKeywords) {
                if (keyword.keywordText.toLowerCase().contains(query)) {
                    suggestionsSet.add(keyword.keywordText)
                }
            }
        } else {
            for (text in suggestionsList!!) {
                if (text.toLowerCase().contains(query)) {
                    suggestionsSet.add(text)
                }
            }
        }//search the subset of results from the previous query
        suggestionsList = ArrayList(suggestionsSet)
        Collections.sort(suggestionsList!!, SortIgnoreCase())

        for (i in suggestionsList!!.indices) {
            cursor.addRow(arrayOf(i, suggestionsList!![i]))
        }
        previousQuery = query
        changeCursor(cursor)
    }

    inner class SortIgnoreCase : Comparator<Any> {
        override fun compare(o1: Any, o2: Any): Int {
            val s1 = o1 as String
            val s2 = o2 as String
            return s1.toLowerCase().compareTo(s2.toLowerCase())
        }
    }
}
