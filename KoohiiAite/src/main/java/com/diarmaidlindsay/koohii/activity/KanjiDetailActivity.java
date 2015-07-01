package com.diarmaidlindsay.koohii.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import com.diarmaidlindsay.koohii.R;
import com.diarmaidlindsay.koohii.fragment.KanjiDetailFragment;

import java.util.List;


public class KanjiDetailActivity extends AppCompatActivity {

    int currentIndex;
    String[] filteredIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_detail);

        Bundle arguments = getIntent().getExtras();
        currentIndex = arguments.getInt("filteredListIndex");
        filteredIdList = arguments.getStringArray("filteredIdList");
        swapFragment(currentIndex);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_detail, menu);
        MenuItem prevButton = menu.findItem(R.id.action_previous);
        MenuItem nextButton = menu.findItem(R.id.action_next);
        if(currentIndex == 0)
        {
            prevButton.setEnabled(false);
        } else {
            prevButton.setEnabled(true);
        }

        if(currentIndex == (filteredIdList.length - 1))
        {
            nextButton.setEnabled(false);
        } else {
            nextButton.setEnabled(true);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            case R.id.action_previous:
                swapFragment(--currentIndex);
                return true;
            case R.id.action_next:
                swapFragment(++currentIndex);
                return true;
            case R.id.action_settings:
                //handle settings here
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private int getCurrentPagerIndex()
    {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if(fragmentList != null)
        {
            Fragment fragment = fragmentList.get(fragmentList.size() - 1);
            if(fragment instanceof KanjiDetailFragment)
            {
                return ((KanjiDetailFragment) fragment).getCurrentPagerIndex();
            }
        }

        return -1;
    }

    private void swapFragment(int newIndex)
    {
        KanjiDetailFragment fragment = new KanjiDetailFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("heisigId", Integer.parseInt(filteredIdList[newIndex]));
        //preserve viewpager index during next/prev, else set to 0 if none exists
        bundle.putInt("currentPage", getCurrentPagerIndex() == -1 ? 0 : getCurrentPagerIndex());
        fragment.setArguments(bundle);
        // Begin the transaction
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        // Replace the contents of the container with the new fragment
        ft.replace(R.id.detail_fragment_framelayout, fragment);
        // Complete the changes added above
        ft.commit();
    }

    public void setResult(int heisigId, String keyword)
    {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("heisigId", heisigId);
        returnIntent.putExtra("keyword", keyword);
        setResult(RESULT_OK, returnIntent);
    }
}
