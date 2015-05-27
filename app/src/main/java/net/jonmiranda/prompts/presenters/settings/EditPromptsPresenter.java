package net.jonmiranda.prompts.presenters.settings;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.events.PromptUpdateEvent;
import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.presenters.BasePresenter;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.views.settings.EditPromptsView;

import java.util.List;

import javax.inject.Inject;

public class EditPromptsPresenter implements BasePresenter,  PromptUpdateEvent.Listener {

    @Inject Bus mBus;
    @Inject Storage mStorage;

    private EditPromptsView mView;

    public EditPromptsPresenter(EditPromptsView view) {
        mView = view;
    }

    public void onCheckBoxClicked(Prompt prompt) {
        mStorage.save(prompt, !prompt.getIsVisible());
        mView.refreshList();
    }

    public void onPromptTitleClicked(Prompt prompt) {
        mView.showPromptItemDialog(prompt.getKey(), prompt.getTitle());
    }

    public List<Prompt> getAllPrompts() {
        return mStorage.getAllPrompts();
    }

    @Subscribe @Override
    public void onPromptChanged(PromptUpdateEvent event) {
        if (event.key == null) {
            mStorage.createPrompt(event.title, true);
        } else {
            mStorage.updatePrompt(event.key, event.title);
        }
        mView.refreshList();
    }

    @Override
    public void onResume() {
        mBus.register(this);
    }

    @Override
    public void onPause() {
        mBus.unregister(this);
    }
}
