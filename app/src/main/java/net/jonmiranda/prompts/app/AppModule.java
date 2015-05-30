package net.jonmiranda.prompts.app;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import net.jonmiranda.prompts.presenters.settings.EditPromptsPresenter;
import net.jonmiranda.prompts.presenters.main.MainPresenter;
import net.jonmiranda.prompts.presenters.settings.SettingsPresenter;
import net.jonmiranda.prompts.storage.RealmStorage;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.ui.main.DatePickerFragment;
import net.jonmiranda.prompts.ui.main.MainActivity;
import net.jonmiranda.prompts.ui.settings.ColorPickerDialog;
import net.jonmiranda.prompts.ui.settings.PromptItemDialog;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module(
        injects = {
                DatePickerFragment.class,
                SettingsPresenter.class,
                EditPromptsPresenter.class,
                PromptItemDialog.class,
                ColorPickerDialog.class,
        },
        library = true
)
public class AppModule {

    final PromptApplication mApplication;

    public AppModule(PromptApplication application) {
        mApplication = application;
    }

    @Provides @Singleton Realm provideRealm() {
        return Realm.getInstance(mApplication);
    }

    @Provides @Singleton Storage provideStorage() {
        return new RealmStorage(provideRealm());
    }

    @Provides @Singleton Bus provideBus() {
        return new Bus(ThreadEnforcer.MAIN);
    }

    @Provides @Singleton PromptApplication provideApplication() {
        return mApplication;
    }
}
