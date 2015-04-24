package net.jonmiranda.prompts.app;

import android.app.Application;
import android.preference.PreferenceManager;

import net.jonmiranda.prompts.R;
import net.jonmiranda.prompts.storage.Storage;

import dagger.ObjectGraph;
import io.realm.Realm;

public class PromptApplication extends Application {

    Storage mStorage;

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        Realm.deleteRealmFile(this); // TODO: Remove this before merging to master
        PromptModule module = new PromptModule(this);
        mObjectGraph = ObjectGraph.create(module);
        mStorage = module.provideStorage();

        initializePrompts();
    }

    public void initializePrompts() {
        mStorage.initializePrompts(getResources().getStringArray(R.array.initial_prompts));
    }

    public void inject(Object object) {
        mObjectGraph.inject(object);
    }

    public boolean hasPasscodeEnabled() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(getString(R.string.passcode_enabled_key), false);
    }

    public String getPasscode() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getString(getString(R.string.set_passcode_key), "9111");
    }
}

