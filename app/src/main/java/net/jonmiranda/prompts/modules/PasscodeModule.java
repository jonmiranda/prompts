package net.jonmiranda.prompts.modules;

import com.squareup.otto.Bus;

import net.jonmiranda.prompts.app.AppModule;
import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.presenters.main.PasscodePresenter;
import net.jonmiranda.prompts.ui.main.PasscodeFragment;
import net.jonmiranda.prompts.views.main.PasscodeView;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = PasscodeFragment.class,
        addsTo = AppModule.class,
        complete = false
)
public class PasscodeModule {

    private final PasscodeView view;

    public PasscodeModule(PasscodeView view) {
        this.view = view;
    }

    @Provides @Singleton
    PasscodePresenter providePasscodePresenter(Bus bus, PromptApplication application) {
        return new PasscodePresenter(view, bus, application.getPasscode());
    }
}
