package tech.diarmaid.koohiiaite.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.adapter.KanjiSampleWordsAdapter
import tech.diarmaid.koohiiaite.database.AppDatabase
import tech.diarmaid.koohiiaite.database.entity.SampleWord
import tech.diarmaid.koohiiaite.viewmodel.KanjiDetailViewModel

/**
 * Display contents of sample_words table for given heisigId
 */
class SampleWordsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail_sample_words, container, false)
        initData(view)
        return view
    }

    private fun initData(view: View) {
        val viewModel = ViewModelProvider(parentFragment as KanjiDetailFragment).get(KanjiDetailViewModel::class.java)
        val listView = view.findViewById<ListView>(R.id.sample_words_list_view)
        var samplewords: List<SampleWord> = arrayListOf()
        context?.let { theContext ->
            viewModel.heisigId.observe(viewLifecycleOwner, Observer { value ->
                GlobalScope.launch {
                    launch(Dispatchers.IO) {
                        samplewords = AppDatabase.getDatabase(theContext).sampleWordDao().getSampleWordsFor(value)
                    }.invokeOnCompletion {
                        launch(Dispatchers.Main) {
                            val adapter = KanjiSampleWordsAdapter(theContext, arguments!!, samplewords)
                            listView?.adapter = adapter
                        }
                    }
                }
            })
        }
    }
}
