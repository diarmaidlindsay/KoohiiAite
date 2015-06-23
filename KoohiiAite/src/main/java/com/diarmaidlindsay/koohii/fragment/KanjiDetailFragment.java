package com.diarmaidlindsay.koohii.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Bundle arguments = getArguments();
        AppCompatActivity parent = (AppCompatActivity) getActivity();
        View view = inflater.inflate(R.layout.fragment_detail, container, false);
        ViewPager vPager = (ViewPager) view.findViewById(R.id.vpPager);
        FragmentPagerAdapter adapterViewPager = new KanjiDetailAdapter(getChildFragmentManager(), arguments, parent);
        vPager.setAdapter(adapterViewPager);
        setHasOptionsMenu(true);
        return view;
    }


}
