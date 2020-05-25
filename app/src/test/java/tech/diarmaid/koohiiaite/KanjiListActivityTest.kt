package tech.diarmaid.koohiiaite

import android.os.Build
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.rule.ActivityTestRule
import androidx.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import tech.diarmaid.koohiiaite.activity.KanjiListActivity

@RunWith(AndroidJUnit4::class)
@Config(sdk = [Build.VERSION_CODES.P])
class KanjiListActivityTest {
    @get:Rule
    val rule = ActivityTestRule(KanjiListActivity::class.java)

    @Test
    fun testAssertHelloText() {
        onView(withId(R.id.result)).check(matches(withText("All items displayed")))
    }
}