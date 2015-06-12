package com.diarmaidlindsay.koohii.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import com.diarmaidlindsay.koohii.fragment.DictionaryFragment;
import com.diarmaidlindsay.koohii.fragment.KoohiiFragment;
import com.diarmaidlindsay.koohii.fragment.StoryFragment;

/**
 * Invoked when user clicks a list item in the KanjiListActivity
 */
public class KanjiDetailAdapter extends FragmentPagerAdapter {
    private final int NUM_ITEMS = 3;

    public KanjiDetailAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new StoryFragment();
            case 1:
                return new DictionaryFragment();
            case 2:
                return new KoohiiFragment();
            default:
                return null;
        }
    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Story";
            case 1:
                return "Dictionary";
            case 2:
                return "Koohii";
            default:
                return "Undefined";
        }
    }

}
