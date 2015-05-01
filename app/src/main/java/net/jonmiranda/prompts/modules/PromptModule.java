package net.jonmiranda.prompts.modules;

import com.squareup.otto.Bus;

import net.jonmiranda.prompts.app.AppModule;
import net.jonmiranda.prompts.presenters.PromptPresenter;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.ui.main.PromptFragment;
import net.jonmiranda.prompts.views.PromptView;

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

    public PromptModule(PromptView view) {
        this.view = view;
    }

    @Provides @Singleton
    PromptPresenter providePromptPresenter(Bus bus, Storage storage) {
        return new PromptPresenter(view, bus, storage);
    }
}
