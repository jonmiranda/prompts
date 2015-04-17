package net.jonmiranda.prompts;

import android.test.ActivityInstrumentationTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.swipeLeft;
import static android.support.test.espresso.action.ViewActions.swipeRight;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;

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
        String[] prompts = getActivity().getResources().getStringArray(R.array.prompts);

        for (String prompt : prompts) {
            onView(withText(prompt)).check(matches(isDisplayed()));
            onView(withId(R.id.container)).perform(swipeLeft());
        }

        for (int i = prompts.length - 1; i >= 0; --i) {
            onView(withText(prompts[i])).check(matches(isDisplayed()));
            onView(withId(R.id.container)).perform(swipeRight());
        }
    }


}
