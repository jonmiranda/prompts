package net.jonmiranda.prompts;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import net.jonmiranda.prompts.ui.main.MainActivity;

import java.util.Calendar;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.doesNotExist;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.allOf;

@SmallTest
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();
        getActivity(); // Launching the activity is required
    }

    public void testAllPromptsAreSwipeable() {
        String[] prompts = getActivity().getResources().getStringArray(R.array.initial_prompts);

        for (String prompt : prompts) {
            onView(withText(prompt)).check(matches(isDisplayed()));
            onView(withId(R.id.container)).perform(swipeLeft());
        }

        for (int i = prompts.length - 1; i >= 0; --i) {
            onView(withText(prompts[i])).check(matches(isDisplayed()));
            onView(withId(R.id.container)).perform(swipeRight());
        }
    }

    private static void clearResponseText() {
        onView(allOf(withId(R.id.editor), isDisplayed())).perform(clearText());
    }

    private static void enterPromptResponse(String response) {
        onView(allOf(withId(R.id.editor), isDisplayed())).perform(typeText(response));
    }

    private static void showPreviousDate() {
        onView(withId(R.id.arrow_left)).perform(click());
    }

    private static void showNextDate() {
        onView(withId(R.id.arrow_right)).perform(click());
    }

    public void testChangingDatePreservesResponses() {
        Calendar today = Calendar.getInstance();
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);

        String todaysResponse = today.getTime().toLocaleString();
        String yesterdaysResponse = yesterday.getTime().toLocaleString();

        // Let's clear yesterday and today's response
        clearResponseText();
        showPreviousDate();
        clearResponseText();

        // Reset to today
        showNextDate();

        // Enter in today's response
        enterPromptResponse(todaysResponse);

        // Change date to yesterday
        showPreviousDate();
        onView(withText(todaysResponse)).check(doesNotExist());

        // Set yesterday's response
        enterPromptResponse(yesterdaysResponse);
        onView(withText(yesterdaysResponse)).check(matches(isDisplayed()));

        // Change date to today, Confirm persistence
        showNextDate();
        onView(withText(todaysResponse)).check(matches(isDisplayed()));

        // Change date to yesterday, Confirm persistence
        showPreviousDate();
        onView(withText(yesterdaysResponse)).check(matches(isDisplayed()));
    }


}
