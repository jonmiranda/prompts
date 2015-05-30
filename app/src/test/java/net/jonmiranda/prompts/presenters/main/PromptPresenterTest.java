package net.jonmiranda.prompts.presenters.main;

import com.squareup.otto.Bus;

import junit.framework.TestCase;

import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.events.DateEvent;
import net.jonmiranda.prompts.events.ShowKeyboardEvent;
import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.models.UserResponse;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.views.main.PromptView;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Date;

public class PromptPresenterTest extends TestCase {

    private static final String FAKE_PROMPT_KEY = "FAKE_PROMPT_KEY";

    PromptPresenter mPresenter;

    @Mock PromptView mPromptView;
    @Mock Bus mBus;
    @Mock Storage mStorage;

    public Date getTodaysDate() {
        return Utils.stripDate(Calendar.getInstance());
    }

    public Date getYesterdaysDate() {
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        return Utils.stripDate(yesterday);
    }

    public void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);
    }

    /**
     * Should only show keyboard on today's prompts.
     * @throws Exception
     */
    public void testShowKeyboard() throws Exception {
        // Set up
        Date today = getTodaysDate();
        Date yesterday = getYesterdaysDate();

        Prompt prompt = new Prompt();
        Mockito.when(mStorage.getResponse(today, prompt)).thenReturn(new UserResponse());
        Mockito.when(mStorage.getResponse(yesterday, prompt)).thenReturn(new UserResponse());
        Mockito.when(mStorage.getPrompt(FAKE_PROMPT_KEY)).thenReturn(prompt);
        mPresenter = new PromptPresenter(mPromptView, mBus, mStorage, FAKE_PROMPT_KEY, today);

        // Date is set to today — should show keyboard
        assertTrue(mPresenter.showKeyboard(new ShowKeyboardEvent()));

        // Change date to yesterday — should not show keyboard
        mPresenter.onDateChanged(new DateEvent(yesterday));
        assertFalse(mPresenter.showKeyboard(new ShowKeyboardEvent()));
    }
}