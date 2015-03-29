package net.jonmiranda.prompts.presenters;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.events.DateEvent;
import net.jonmiranda.prompts.events.ShowKeyboardEvent;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.views.PromptView;

import javax.inject.Inject;

/**
 * Presentation logic for a {@link net.jonmiranda.prompts.views.PromptView}.
 */
public class PromptPresenter implements BasePresenter, DateEvent.Listener {

    private PromptView mView;
    @Inject Bus mBus;
    @Inject Storage mStorage;

    private final String mPrompt;
    private String mDate;

    public PromptPresenter(PromptView view, String prompt, String date, int color) {
        mView = view;
        mPrompt = prompt;
        mDate = date;

        mView.setPrompt(mPrompt);
        mView.setColor(color);
    }

    /**
     * Checks the database to see if a response exists,
     * and if true it updates the view with the response.
     */
    public void tryGetResponse() {
        String response = mStorage.getResponse(mDate, mPrompt);
        mView.setResponse(response);
    }

    public void createOrUpdatePrompt(CharSequence response) {
        mStorage.save(mDate, mPrompt, response);
    }

    @Override @Subscribe
    public void onDateChanged(DateEvent event) {
        mDate = Utils.getRealmDateString(event.date);
        tryGetResponse();
    }

    @Subscribe
    public void showKeyboard(ShowKeyboardEvent event) {
        mView.showKeyboard();
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
