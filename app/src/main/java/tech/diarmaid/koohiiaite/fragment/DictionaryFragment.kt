package tech.diarmaid.koohiiaite.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.database.dao.FrequencyDataSource
import tech.diarmaid.koohiiaite.database.dao.MeaningDataSource
import tech.diarmaid.koohiiaite.database.dao.ReadingDataSource

/**
 * For display of dictionary derived information
 * about a given kanji related to the heisigId provided
 */
class DictionaryFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail_dictionary, container, false)

        val textViewKanji = view.findViewById<TextView>(R.id.kanji_detail)
        val textViewFrequency = view.findViewById<TextView>(R.id.frequency_detail)
        val textViewOnYomi = view.findViewById<TextView>(R.id.onyomi_detail)
        val textViewKunYomi = view.findViewById<TextView>(R.id.kunyomi_detail)
        val textViewMeanings = view.findViewById<TextView>(R.id.meanings_detail)

        val args = arguments
        val heisigIdInt = args!!.getInt("heisigId", 0)
        val kanji = args.getString("kanji")
        val frequency = getFrequencyFromDatabase(heisigIdInt)
        val onYomi = getReadingFromDatabase(heisigIdInt, 0)
        val kunYomi = getReadingFromDatabase(heisigIdInt, 1)
        val meaning = getMeaningFromDatabase(heisigIdInt)

        // Load the results into the TextViews
        textViewKanji.text = kanji
        textViewFrequency.text = frequency
        textViewOnYomi.text = onYomi //reading
        textViewKunYomi.text = kunYomi //reading
        textViewMeanings.text = meaning //meaning

        return view
    }

    private fun getReadingFromDatabase(heisigId: Int, type: Int): String {
        val dataSource = ReadingDataSource(activity!!)
        dataSource.open()
        val reading = dataSource.getMeaningForHeisigKanjiId(heisigId, type) //0 for onYomi, 1 for KunYomi
        dataSource.close()
        return reading?.readingText ?: ""
    }

    private fun getMeaningFromDatabase(heisigId: Int): String {
        val sb = StringBuilder()
        val dataSource = MeaningDataSource(activity!!)
        dataSource.open()
        val meanings = dataSource.getMeaningsForHeisigKanjiId(heisigId)
        dataSource.close()

        for (meaning in meanings) {
            sb.append(meaning.meaningText)
            sb.append(", ")
        }

        var meaningString = sb.toString()
        if (meaningString.endsWith(", ")) {
            meaningString = meaningString.substring(0, meaningString.length - 2)
        }
        return meaningString
    }

    private fun getFrequencyFromDatabase(heisigId: Int): String {
        val dataSource = FrequencyDataSource(activity!!)
        dataSource.open()
        val frequency = dataSource.getFrequencyFor(heisigId)!!
        dataSource.close()

        return if (frequency == 999999) "-" else frequency.toString()
    }
}
