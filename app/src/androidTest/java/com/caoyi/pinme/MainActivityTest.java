package com.caoyi.pinme;

/**
 * Created by A.C. on 4/9/18.
 */

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;
import android.widget.EditText;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDescendantOfA;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    @Test
    public void loginTest() throws Exception {
        // Context of the app under test.
        onView(withId(R.id.start_login_button)).perform(click());

        onView(allOf(isDescendantOfA(withId(R.id.login_email)), isAssignableFrom(EditText.class)))
                .perform(typeText("test@pinme.com"), closeSoftKeyboard());
        onView(allOf(isDescendantOfA(withId(R.id.login_password)), isAssignableFrom(EditText.class)))
                .perform(typeText("testtest"), closeSoftKeyboard());
        onView(withId(R.id.login_login_button)).perform(click());

    }

    @Test
    public void changeStatusTest() throws Exception {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Account Settings")).perform(click());
        onView(withId(R.id.settings_status_button)).perform(click());
        onView(allOf(isDescendantOfA(withId(R.id.status_input)), isAssignableFrom(EditText.class)))
                .perform(clearText(), typeText("status changed"), closeSoftKeyboard());
        onView(withId(R.id.status_save_button)).perform(click());
        Espresso.pressBack();
    }

    @Test
    public void logOutTest() throws Exception {
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText("Logout")).perform(click());
    }
}
