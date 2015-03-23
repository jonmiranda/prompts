package net.jonmiranda.prompts.app;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import net.jonmiranda.prompts.datepicker.DatePickerFragment;
import net.jonmiranda.prompts.MainActivity;
import net.jonmiranda.prompts.presenters.MainPresenter;
import net.jonmiranda.prompts.presenters.PromptPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module(
        injects = {
                PromptPresenter.class,
                MainActivity.class,
                MainPresenter.class,
                DatePickerFragment.class,
        },
        library = true
)
public class PromptModule {

    final PromptApplication mApplication;

    public PromptModule(PromptApplication application) {
        mApplication = application;
    }

    @Provides @Singleton Realm provideRealm() {
        return Realm.getInstance(mApplication);
    }

    @Provides @Singleton Bus provideBus() {
        return new Bus(ThreadEnforcer.MAIN);
    }

    @Provides @Singleton PromptApplication provieApplication() {
        return mApplication;
    }
}
