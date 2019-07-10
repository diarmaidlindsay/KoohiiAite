package tech.diarmaid.koohiiaite.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_detail.*
import tech.diarmaid.koohiiaite.R
import tech.diarmaid.koohiiaite.adapter.KanjiDetailAdapter
import tech.diarmaid.koohiiaite.viewmodel.KanjiDetailViewModel

/**
 * Allows next and previous navigation by swapping this fragment
 *
 * Replaced by new versions of itself when Next/Prev pressed
 */
class KanjiDetailFragment : Fragment() {

    var currentPagerIndex: Int = 0
    private var adapterViewPager : KanjiDetailAdapter? = null
    private lateinit var viewModel: KanjiDetailViewModel
    //TODO : Use the ViewModel to share data between fragments instead of args

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        //fragment instance is retained across Activity re-creation (device rotation)
        retainInstance = true //may cause memory leaks according to stackoverflow
        val arguments = arguments
        val parent = activity as AppCompatActivity?
        val view = inflater.inflate(R.layout.fragment_detail, container, false)
        adapterViewPager = KanjiDetailAdapter(childFragmentManager, arguments!!, parent!!)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPagerIndex = vpPager.currentItem
        vpPager.adapter = adapterViewPager
        vpPager.currentItem = arguments?.getInt("currentPage") ?:0  //preserve page between next/prev operations
    }
}
