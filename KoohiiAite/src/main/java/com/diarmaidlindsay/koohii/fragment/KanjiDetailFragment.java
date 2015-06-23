package com.diarmaidlindsay.koohii.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.adapter.KanjiDetailAdapter;

/**
 * Allows next and previous navigation by swapping this fragment
 *
 * Replaced by new versions of itself when Next/Prev pressed
 */
public class KanjiDetailFragment extends Fragment {

    private ViewPager vPager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        AppCompatActivity parent = (AppCompatActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        vPager = (ViewPager) view.findViewById(R.id.vpPager);
        FragmentPagerAdapter adapterViewPager = new KanjiDetailAdapter(getChildFragmentManager(), arguments, parent);
        vPager.setAdapter(adapterViewPager);
        setHasOptionsMenu(true);
        vPager.setCurrentItem(arguments.getInt("currentPage")); //preserve page between next/prev operations
        return view;
    }

    public int getCurrentPagerIndex()
    {
        return vPager.getCurrentItem();
    }
}
