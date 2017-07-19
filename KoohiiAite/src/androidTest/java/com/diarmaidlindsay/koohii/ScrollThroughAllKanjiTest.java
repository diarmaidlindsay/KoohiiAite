package com.diarmaidlindsay.koohii;

import android.app.Activity;
import android.support.test.espresso.NoMatchingViewException;
import android.support.test.espresso.ViewAssertion;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.TextView;
import com.diarmaidlindsay.koohii.activity.KanjiListActivity;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.containsString;

/**
 * Created by diarmaidlindsay on 5/18/17.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ScrollThroughAllKanjiTest {
    @Rule
    public ActivityTestRule<KanjiListActivity> listActivityTestRule = new ActivityTestRule<>(
            KanjiListActivity.class);

    @Test
    public void scrollThroughAllWithoutErrors() {


//        TextView result = (TextView)listActivityTestRule.getActivity()
//                .findViewById(R.id.result);

//        Pattern p = Pattern.compile("\\d+");
//        Matcher m = p.matcher(result.getText().toString());
//        Integer numberOfKanji = Integer.parseInt(m.group());

        onData(anything())
                .inAdapterView(withId(R.id.kanjiListView))
                .atPosition(0).perform(click());

        for(int i = 2; i < 3007; i++) {
            onView(withId(R.id.action_next))
                    .perform(click());
            onView(withId(R.id.heisig_id_detail))
                    .check(matches(withText(containsString(""+i))));
        }
    }
}
