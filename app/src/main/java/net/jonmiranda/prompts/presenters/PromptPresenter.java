package net.jonmiranda.prompts.presenters;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.events.DateEvent;
import net.jonmiranda.prompts.events.ShowKeyboardEvent;
import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.models.UserResponse;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.views.PromptView;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

/**
 * Presentation logic for a {@link net.jonmiranda.prompts.views.PromptView}.
 */
public class PromptPresenter implements BasePresenter, DateEvent.Listener {

    private PromptView mView;
    @Inject Bus mBus;
    @Inject Storage mStorage;

    private Prompt mPrompt;
    private UserResponse mResponse;
    private Date mDate;

    // only force-show the keyboard if on today's date
    private boolean mShowKeyboard = true;

    public PromptPresenter(PromptView view, int color) {
        mView = view;
        mView.setColor(color);
    }

    public void bind(String promptKey, Date date) {
        mPrompt = mStorage.getPrompt(promptKey);
        mView.setPromptTitle(mPrompt.getTitle());
        mDate = date;
        mShowKeyboard = Calendar.getInstance().getTime().equals(date);
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
    public void showKeyboard(ShowKeyboardEvent event) {
        if (mShowKeyboard) {
            mView.showKeyboard();
        }
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
