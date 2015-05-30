package net.jonmiranda.prompts.presenters.main;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.events.DateEvent;
import net.jonmiranda.prompts.events.ShowKeyboardEvent;
import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.models.UserResponse;
import net.jonmiranda.prompts.presenters.BasePresenter;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.views.main.PromptView;

import java.util.Calendar;
import java.util.Date;

/**
 * Presentation logic for a {@link PromptView}.
 */
public class PromptPresenter implements BasePresenter, DateEvent.Listener {

    private PromptView mView;
    Bus mBus;
    Storage mStorage;

    private Prompt mPrompt;
    private UserResponse mResponse;
    private Date mDate;

    // only force-show the keyboard if on today's date
    private boolean mShowKeyboard = true;

    public PromptPresenter(PromptView view, Bus bus, Storage storage, String promptKey, Date date) {
        mView = view;
        mBus = bus;
        mStorage = storage;
        mPrompt = mStorage.getPrompt(promptKey);
        mView.setPromptTitle(mPrompt.getTitle());
        mDate = date;
        mShowKeyboard = Utils.stripDate(Calendar.getInstance()).equals(date);
    }

    /**
     * Checks the database to see if a response exists,
     * and if true it updates the view with the response.
     */
    public void tryGetResponse() {
        mResponse = mStorage.getResponse(mDate, mPrompt);
        mView.setResponse(mResponse.getResponse());
    }

    public void createOrUpdatePrompt(String response) {
        mResponse = mStorage.save(mResponse, response);
    }

    @Override @Subscribe
    public void onDateChanged(DateEvent event) {
        mDate = event.date;
        mShowKeyboard = Utils.stripDate(Calendar.getInstance()).equals(mDate);
        tryGetResponse();
    }

    @Subscribe
    public boolean showKeyboard(ShowKeyboardEvent event) {
        if (mShowKeyboard) {
            mView.showKeyboard();
        }
        return mShowKeyboard;
    }

    @Override
    public void onResume() {
        mBus.register(this);
        tryGetResponse();
    }

    @Override
    public void onPause() {
        mBus.unregister(this);
    }
}
