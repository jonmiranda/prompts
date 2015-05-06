package net.jonmiranda.prompts.modules;

import net.jonmiranda.prompts.app.AppModule;
import net.jonmiranda.prompts.presenters.settings.SettingsPresenter;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.ui.settings.PrefsFragment;
import net.jonmiranda.prompts.views.settings.SettingsView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = PrefsFragment.class,
        addsTo = AppModule.class,
        complete = false
)
public class SettingsModule {

    private final SettingsView view;

    public SettingsModule(SettingsView view) {
        this.view = view;
    }

    @Provides @Singleton
    SettingsPresenter provideSettingsPresenter(Storage storage) {
        return new SettingsPresenter(view, storage);
    }
}
