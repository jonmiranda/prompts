package net.jonmiranda.prompts.app;

import net.jonmiranda.prompts.MainActivity;
import net.jonmiranda.prompts.PromptFragment;
import net.jonmiranda.prompts.PromptPresenter;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.realm.Realm;

@Module(
        injects = {
                PromptPresenter.class,
                MainActivity.class,
                PromptFragment.class
        },
        library = true
)
public class PromptModule {

    PromptApplication mApplication;

    public PromptModule(PromptApplication application) {
        mApplication = application;
    }

    @Provides @Singleton Realm provideRealm() {
        return Realm.getInstance(mApplication);
    }
}
