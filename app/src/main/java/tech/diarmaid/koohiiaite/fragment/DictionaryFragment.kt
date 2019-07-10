package tech.diarmaid.koohiiaite.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.database.AppDatabase

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
        getFrequencyFromDatabase(heisigIdInt).observe(context as AppCompatActivity, Observer {
            textViewFrequency.text = it
        })
        getReadingFromDatabase(heisigIdInt, 0).observe(context as AppCompatActivity, Observer {
            textViewOnYomi.text = it //reading
        })
        getReadingFromDatabase(heisigIdInt, 1).observe(context as AppCompatActivity, Observer {
            textViewKunYomi.text = it //reading
        })
        getMeaningFromDatabase(heisigIdInt).observe(context as AppCompatActivity, Observer {
            textViewMeanings.text = it //meaning
        })

        // Load the results into the TextViews
        textViewKanji.text = kanji

        return view
    }

    private fun getReadingFromDatabase(heisigId: Int, type: Int): LiveData<String> {
        val data = MutableLiveData<String>()
        GlobalScope.launch {
            launch(Dispatchers.IO) {
                val dataSource = context?.let { AppDatabase.getDatabase(it).readingDao() }
                val reading = dataSource?.getMeaningForHeisigKanjiId(heisigId, type) //0 for onYomi, 1 for KunYomi
                data.postValue(reading?.readingText ?: "")
            }
        }
        return data
    }

    private fun getMeaningFromDatabase(heisigId: Int): LiveData<String> {
        val data = MutableLiveData<String>()
        val sb = StringBuilder()
        GlobalScope.launch {
            launch(Dispatchers.IO) {
                val dataSource = context?.let { AppDatabase.getDatabase(it).meaningDao() }
                dataSource?.getMeaningsForHeisigKanjiId(heisigId)
                        ?.let { meanings ->
                            for (meaning in meanings) {
                                sb.append(meaning.meaningText)
                                sb.append(", ")
                            }
                        }

                var meaningString = sb.toString()
                if (meaningString.endsWith(", ")) {
                    meaningString = meaningString.substring(0, meaningString.length - 2)
                }
                data.postValue(meaningString)
            }
        }

        return data
    }

    private fun getFrequencyFromDatabase(heisigId: Int): LiveData<String> {
        val data = MutableLiveData<String>()
        GlobalScope.launch {
            launch(Dispatchers.IO) {
                val kanjiFrequency = activity?.let { AppDatabase.getDatabase(it).kanjiFrequencyDao().getFrequencyFor(heisigId) }
                val frequency = kanjiFrequency?.firstOrNull()?.frequency
                data.postValue(if (frequency == 999999 || frequency == null) "-" else frequency.toString())
            }
        }
        return data
    }
}
