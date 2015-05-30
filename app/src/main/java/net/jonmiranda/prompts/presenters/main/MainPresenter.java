package net.jonmiranda.prompts.presenters.main;

import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.events.DateEvent;
import net.jonmiranda.prompts.events.LoggedInEvent;
import net.jonmiranda.prompts.events.ShowKeyboardEvent;
import net.jonmiranda.prompts.presenters.BasePresenter;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.views.main.MainView;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainPresenter implements BasePresenter, DateEvent.Listener, LoggedInEvent.Listener {

    private static final int TIMEOUT_MILLISECONDS = 300000; // 5 minutes

    Bus mBus;
    Storage mStorage;

    private Calendar mCalendarDate;

    private MainView mView;

    private long mLastOnPause = 0;

    private boolean mPasscodeEnabled = false;
    private boolean mShowLogin = false;

    public MainPresenter(MainView view, Bus bus, Storage storage, Calendar date, boolean passcodeEnabled) {
        mView = view;
        mBus = bus;
        mStorage = storage;
        mCalendarDate = date;
        mPasscodeEnabled = passcodeEnabled;
        mView.setPrompts(mStorage.getPrompts());
    }

    @Override @Subscribe
    public void onDateChanged(DateEvent event) {
        mCalendarDate = new GregorianCalendar();
        mCalendarDate.setTime(event.date);
        mView.showDate(mCalendarDate);
    }

    @Produce
    public DateEvent produceDate() {
        return new DateEvent(Utils.stripDate(mCalendarDate));
    }

    public void updateDate(int dateOffset) {
        mCalendarDate.add(Calendar.DATE, dateOffset);
        mBus.post(new DateEvent(Utils.stripDate(mCalendarDate)));
    }

    public Calendar getDate() {
        return mCalendarDate;
    }

    public void onPageSelected() {
        mBus.post(new ShowKeyboardEvent());
    }

    public boolean showLogin() {
        return mPasscodeEnabled && mShowLogin;
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
        if (mPasscodeEnabled && mShowLogin) {
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
