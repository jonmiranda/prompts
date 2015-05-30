package net.jonmiranda.prompts.modules;

import com.squareup.otto.Bus;

import net.jonmiranda.prompts.app.AppModule;
import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.presenters.main.MainPresenter;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.ui.main.MainActivity;
import net.jonmiranda.prompts.views.main.MainView;

import java.util.Calendar;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = MainActivity.class,
        addsTo = AppModule.class,
        complete = false
)
public class MainModule {

    private final MainView view;
    private final Calendar date;

    public MainModule(MainView view, Calendar date) {
        this.view = view;
        this.date = date;
    }

    @Provides @Singleton
    MainPresenter providesMainPresenter(PromptApplication application, Bus bus, Storage storage) {
        return new MainPresenter(view, bus, storage, date, application.hasPasscodeEnabled());
    }
}
