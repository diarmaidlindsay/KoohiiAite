package tech.diarmaid.koohiiaite.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import tech.diarmaid.koohiiaite.fragment.KanjiDetailFragment;
import tech.diarmaid.koohiiaite.utils.Utils;

import tech.diarmaid.koohiiaite.R;

import java.util.ArrayList;
import java.util.List;


public class KanjiDetailActivity extends AppCompatActivity {

    public static final int ACTIVITY_CODE = 1;
    private int currentIndex;
    private String[] filteredIdList;
    private List<Integer> changedIds = new ArrayList<>();
    private List<String> changedKeywords = new ArrayList<>();
    private MenuItem prevButton;
    private MenuItem nextButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kanji_detail);

        Bundle arguments = getIntent().getExtras();
        currentIndex = arguments.getInt("filteredListIndex");
        filteredIdList = arguments.getStringArray("filteredIdList");
        if (savedInstanceState != null) {
            swapFragment(savedInstanceState.getInt("currentIndex"));
        } else {
            swapFragment(currentIndex);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        //if you click a link in the story and come back, we need to refresh button state
        updateButtonEnablement();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_kanji_detail, menu);
        prevButton = menu.findItem(R.id.action_previous);
        nextButton = menu.findItem(R.id.action_next);
        updateButtonEnablement();
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

    /**
     * Ask the view pager in the child fragment manager which page index it is currently displaying.
     * Used for pressing next/prev and preserving the tab index.
     */
    private int getCurrentPagerIndex() {
        List<Fragment> fragmentList = getSupportFragmentManager().getFragments();
        if (fragmentList != null && !fragmentList.isEmpty()) {
            Fragment fragment = fragmentList.get(fragmentList.size() - 1);
            if (fragment instanceof KanjiDetailFragment) {
                return ((KanjiDetailFragment) fragment).getCurrentPagerIndex();
            }
        }

        return -1;
    }

    private void swapFragment(int newIndex) {
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
        updateButtonEnablement();
    }

    public void setResult(int heisigId, String keyword) {
        Intent returnIntent = new Intent();
        changedIds.add(heisigId);
        changedKeywords.add(keyword);
        returnIntent.putExtra("heisigIds", Utils.toIntArray(changedIds));
        returnIntent.putExtra("keywords", changedKeywords.toArray(new String[changedKeywords.size()]));
        setResult(RESULT_OK, returnIntent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentIndex", currentIndex);
    }

    /**
     * Manage Prev/Next button state
     */
    private void updateButtonEnablement() {
        if (prevButton != null && nextButton != null) {
            if (currentIndex == 0) {
                prevButton.setEnabled(false);
                prevButton.getIcon().setAlpha(130);
            } else {
                prevButton.setEnabled(true);
                prevButton.getIcon().setAlpha(255);
            }

            if (currentIndex == (filteredIdList.length - 1)) {
                nextButton.setEnabled(false);
                nextButton.getIcon().setAlpha(130);

            } else {
                nextButton.setEnabled(true);
                nextButton.getIcon().setAlpha(255);
            }
        }

    }
}
