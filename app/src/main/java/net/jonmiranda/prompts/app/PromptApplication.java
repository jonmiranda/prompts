package net.jonmiranda.prompts.app;

import android.app.Application;
import android.preference.PreferenceManager;

import net.jonmiranda.prompts.R;

import dagger.ObjectGraph;

public class PromptApplication extends Application {

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        mObjectGraph = ObjectGraph.create(new PromptModule(this));
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

