package tech.diarmaid.koohiiaite.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView

import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.adapter.KanjiSampleWordsAdapter

/**
 * Display contents of sample_words table for given heisigId
 */
class SampleWordsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_detail_sample_words, container, false)
        val adapter = KanjiSampleWordsAdapter(activity!!, arguments!!)
        val listView = view.findViewById<ListView>(R.id.sample_words_list_view)

        listView.adapter = adapter

        return view
    }
}
