package tech.diarmaid.koohiiaite.adapter;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import tech.diarmaid.koohiiaite.database.dao.HeisigKanjiDataSource;
import tech.diarmaid.koohiiaite.database.dao.KeywordDataSource;
import tech.diarmaid.koohiiaite.database.dao.UserKeywordDataSource;
import tech.diarmaid.koohiiaite.fragment.DictionaryFragment;
import tech.diarmaid.koohiiaite.fragment.KoohiiFragment;
import tech.diarmaid.koohiiaite.fragment.SampleWordsFragment;
import tech.diarmaid.koohiiaite.fragment.StoryFragment;
import tech.diarmaid.koohiiaite.model.HeisigKanji;
import tech.diarmaid.koohiiaite.model.Keyword;

import static tech.diarmaid.koohiiaite.utils.Constants.KANJI_DETAIL_TABS;

/**
 * Invoked when user clicks a list item in the KanjiListActivity
 */
public class KanjiDetailAdapter extends FragmentPagerAdapter {
    private StoryFragment storyFragment;
    private DictionaryFragment dictionaryFragment;
    private SampleWordsFragment sampleWordsFragment;
    private KoohiiFragment koohiiFragment;

    private Context mContext;
    private Bundle arguments;

    public KanjiDetailAdapter(FragmentManager fragmentManager, Bundle arguments, Context context) {
        super(fragmentManager);
        this.mContext = context;
        int heisigId = arguments.getInt("heisigId");
        String keyword = getKeywordFromDatabase(heisigId);
        String userKeyword = getUserKeywordFromDatabase(heisigId);
        String kanji = getKanjiFromDatabase(heisigId);
        arguments.putString("keyword", keyword);
        arguments.putString("userKeyword", userKeyword);
        arguments.putString("kanji", kanji);
        this.arguments = arguments;
    }

    private String getKanjiFromDatabase(int heisigId)
    {
        HeisigKanjiDataSource dataSource = new HeisigKanjiDataSource(mContext);
        dataSource.open();
        HeisigKanji heisigKanji = dataSource.getKanjiFor(heisigId);
        dataSource.close();

        return heisigKanji.getKanji();
    }

    private String getKeywordFromDatabase(int heisigId)
    {
        KeywordDataSource dataSource = new KeywordDataSource(mContext);
        dataSource.open();
        Keyword keyword = dataSource.getKeywordFor(heisigId);
        dataSource.close();

        return keyword.getKeywordText();
    }

    private String getUserKeywordFromDatabase(int heisigId)
    {
        UserKeywordDataSource dataSource = new UserKeywordDataSource(mContext);
        dataSource.open();
        Keyword keyword = dataSource.getKeywordFor(heisigId);
        dataSource.close();

        return keyword == null ? null : keyword.getKeywordText();
    }

    // Returns total number of pages
    @Override
    public int getCount() {
        return KANJI_DETAIL_TABS;
    }

    // Returns the fragment to display for that page
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                if(storyFragment == null) {
                    storyFragment = new StoryFragment();
                    storyFragment.setArguments(arguments);
                }
                return storyFragment;
            case 1:
                if(dictionaryFragment == null) {
                    dictionaryFragment = new DictionaryFragment();
                    dictionaryFragment.setArguments(arguments);
                }
                return dictionaryFragment;
            case 2:
                if(sampleWordsFragment == null) {
                    sampleWordsFragment = new SampleWordsFragment();
                    sampleWordsFragment.setArguments(arguments);
                }
                return sampleWordsFragment;
            case 3:
                if(koohiiFragment == null) {
                    koohiiFragment = new KoohiiFragment();
                    koohiiFragment.setArguments(arguments);
                }
                return koohiiFragment;
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
                return "Sample Words";
            case 3:
                return "Koohii";
            default:
                return "Undefined";
        }
    }

}
