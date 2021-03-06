package net.jonmiranda.prompts.modules;

import com.squareup.otto.Bus;

import net.jonmiranda.prompts.app.AppModule;
import net.jonmiranda.prompts.presenters.main.PromptPresenter;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.ui.main.PromptFragment;
import net.jonmiranda.prompts.views.main.PromptView;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = PromptFragment.class,
        addsTo = AppModule.class,
        complete = false
)
public class PromptModule {

    private final PromptView view;
    private final String promptKey;
    private final Date date;

    public PromptModule(PromptView view, String promptKey, Date date) {
        this.view = view;
        this.promptKey = promptKey;
        this.date = date;
    }

    @Provides @Singleton
    PromptPresenter providePromptPresenter(Bus bus, Storage storage) {
        return new PromptPresenter(view, bus, storage, promptKey, date);
    }
}
