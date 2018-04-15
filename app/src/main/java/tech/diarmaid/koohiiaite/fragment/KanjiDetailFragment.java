package tech.diarmaid.koohiiaite.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import tech.diarmaid.koohiiaite.R;
import tech.diarmaid.koohiiaite.adapter.KanjiDetailAdapter;

/**
 * Allows next and previous navigation by swapping this fragment
 *
 * Replaced by new versions of itself when Next/Prev pressed
 */
public class KanjiDetailFragment extends Fragment {

    private ViewPager vPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        //fragment instance is retained across Activity re-creation (device rotation)
        setRetainInstance(true); //may cause memory leaks according to stackoverflow
        Bundle arguments = getArguments();
        AppCompatActivity parent = (AppCompatActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        vPager = (ViewPager) view.findViewById(R.id.vpPager);
        FragmentPagerAdapter adapterViewPager = new KanjiDetailAdapter(getChildFragmentManager(), arguments, parent);
        vPager.setAdapter(adapterViewPager);
        vPager.setCurrentItem(arguments.getInt("currentPage")); //preserve page between next/prev operations
        return view;
    }

    public int getCurrentPagerIndex()
    {
        return vPager == null ? -1 : vPager.getCurrentItem();
    }
}
