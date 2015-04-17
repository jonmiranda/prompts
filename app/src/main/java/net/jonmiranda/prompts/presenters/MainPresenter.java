package net.jonmiranda.prompts.presenters;

import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.events.DateEvent;
import net.jonmiranda.prompts.events.LoggedInEvent;
import net.jonmiranda.prompts.events.ShowKeyboardEvent;
import net.jonmiranda.prompts.views.MainView;

import java.util.Calendar;

import javax.inject.Inject;

public class MainPresenter implements BasePresenter, DateEvent.Listener, LoggedInEvent.Listener {

    private static final int TIMEOUT_MILLISECONDS = 300000; // 5 minutes

    @Inject Bus mBus;
    private Calendar mCalendarDate;

    private MainView mView;

    private long mLastOnPause = 0;
    private boolean mShowLogin = true;

    public MainPresenter(MainView view, Calendar date) {
        mView = view;
        mCalendarDate = date;
    }

    @Override @Subscribe
    public void onDateChanged(DateEvent event) {
        mCalendarDate = event.date;
        mView.showDate(mCalendarDate);
    }

    @Produce
    public DateEvent produceDate() {
        return new DateEvent(mCalendarDate);
    }

    public void updateDate(int dateOffset) {
        mCalendarDate.add(Calendar.DATE, dateOffset);
        mBus.post(new DateEvent(mCalendarDate));
    }

    public Calendar getDate() {
        return mCalendarDate;
    }

    public void onPageSelected() {
        mBus.post(new ShowKeyboardEvent());
    }

    public boolean showLogin() {
        return mShowLogin;
    }

    @Override @Subscribe
    public void onLoggedIn(LoggedInEvent event) {
        mShowLogin = false;
        mView.showPrompts();
    }

    @Override
    public void onResume() {
        mBus.register(this);
        mView.showDate(mCalendarDate);

        if (!mShowLogin) {
            long now = Calendar.getInstance().getTimeInMillis();
            mShowLogin = now - mLastOnPause > TIMEOUT_MILLISECONDS;
        }
        if (mShowLogin) {
            mView.showLogin();
        } else {
            mView.showPrompts();
        }
    }

    @Override
    public void onPause() {
        mLastOnPause = Calendar.getInstance().getTimeInMillis();
        mBus.unregister(this);
    }
}
