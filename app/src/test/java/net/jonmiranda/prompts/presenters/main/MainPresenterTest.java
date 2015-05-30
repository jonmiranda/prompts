package net.jonmiranda.prompts.presenters.main;

import com.squareup.otto.Bus;

import junit.framework.TestCase;

import net.jonmiranda.prompts.events.LoggedInEvent;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.views.main.MainView;

import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.Calendar;
import java.util.Collections;

public class MainPresenterTest extends TestCase {

    MainPresenter mPresenter;

    @Mock MainView mView;
    @Mock Bus mBus;
    @Mock Storage mStorage;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        MockitoAnnotations.initMocks(this);

        Mockito.when(mStorage.getPrompts()).thenReturn(Collections.EMPTY_LIST);
        MainPresenter.TIMEOUT_MILLISECONDS = 100;
    }


    public void testShouldShowLoginWithoutPasscodeEnabled() throws Exception {
        mPresenter = new MainPresenter(mView, mBus, mStorage, Calendar.getInstance(), false);
        assertFalse(mPresenter.shouldShowLogin());
    }

    public void testShouldShowLoginWithPasscodeEnabled() throws Exception {
        mPresenter = new MainPresenter(mView, mBus, mStorage, Calendar.getInstance(), true);
        assertTrue(mPresenter.shouldShowLogin());
    }

    public void testLoggedInEvent() throws Exception {
        mPresenter = new MainPresenter(mView, mBus, mStorage, Calendar.getInstance(), true);
        assertTrue(mPresenter.shouldShowLogin());
        mPresenter.onLoggedIn(new LoggedInEvent());
        assertFalse(mPresenter.shouldShowLogin());
    }

    public void testShouldShowLoginWithPasscodeEnabledAfterLoggingInAndTimeout() throws Exception {
        mPresenter = new MainPresenter(mView, mBus, mStorage, Calendar.getInstance(), true);
        assertTrue(mPresenter.shouldShowLogin());
        // Login
        mPresenter.onLoggedIn(new LoggedInEvent());
        // Simulate leaving activity
        Thread.sleep(MainPresenter.TIMEOUT_MILLISECONDS);
        // We should need to re-login
        assertTrue(mPresenter.shouldShowLogin());
    }

    public void testShouldShowLoginLogic() throws Exception {
        mPresenter = new MainPresenter(mView, mBus, mStorage, Calendar.getInstance(), true);
        assertTrue(mPresenter.shouldShowLogin());
        Thread.sleep(MainPresenter.TIMEOUT_MILLISECONDS);
        assertTrue(mPresenter.shouldShowLogin());

        mPresenter.onLoggedIn(new LoggedInEvent());

        assertFalse(mPresenter.shouldShowLogin());
        assertFalse(mPresenter.shouldShowLogin());
        assertFalse(mPresenter.shouldShowLogin());
        Thread.sleep(MainPresenter.TIMEOUT_MILLISECONDS);
        assertTrue(mPresenter.shouldShowLogin());
        assertTrue(mPresenter.shouldShowLogin());
        assertTrue(mPresenter.shouldShowLogin());

        Thread.sleep(MainPresenter.TIMEOUT_MILLISECONDS);
        assertTrue(mPresenter.shouldShowLogin());
        assertTrue(mPresenter.shouldShowLogin());
    }
}