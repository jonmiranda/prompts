package net.jonmiranda.prompts.presenters;

import com.squareup.otto.Bus;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.events.DateEvent;
import net.jonmiranda.prompts.views.MainView;

import java.util.Calendar;

import javax.inject.Inject;

public class MainPresenter implements BasePresenter, DateEvent.Listener {

    @Inject Bus mBus;
    private Calendar mCalendarDate;

    private MainView mView;

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

    @Override
    public void onResume() {
        mBus.register(this);
        mView.showDate(mCalendarDate);
    }

    @Override
    public void onPause() {
        mBus.unregister(this);
    }
}
