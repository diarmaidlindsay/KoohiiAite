package tech.diarmaid.koohiiaite.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import tech.diarmaid.koohiiaite.adapter.KanjiDetailAdapter
import tech.diarmaid.koohiiaite.databinding.FragmentDetailBinding
import tech.diarmaid.koohiiaite.viewmodel.KanjiDetailViewModel

/**
 * Allows next and previous navigation by swapping this fragment
 *
 * Replaced by new versions of itself when Next/Prev pressed
 */
class KanjiDetailFragment(val heisigId: Int) : Fragment() {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!
    var currentPagerIndex: Int = 0
    private var adapterViewPager : KanjiDetailAdapter? = null
    private var viewModel: KanjiDetailViewModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(KanjiDetailViewModel::class.java)
        viewModel?.heisigId?.postValue(heisigId)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        //fragment instance is retained across Activity re-creation (device rotation)
        retainInstance = true //may cause memory leaks according to stackoverflow
        val parent = activity as AppCompatActivity?
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        adapterViewPager = KanjiDetailAdapter(childFragmentManager, arguments!!, parent!!, viewModel, viewLifecycleOwner)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        currentPagerIndex = binding.vpPager.currentItem
        binding.vpPager.adapter = adapterViewPager
        binding.vpPager.currentItem =
            arguments?.getInt("currentPage") ?: 0  //preserve page between next/prev operations
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
