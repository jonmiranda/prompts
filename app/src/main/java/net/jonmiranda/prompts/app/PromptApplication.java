package net.jonmiranda.prompts.app;

import android.app.Application;
import android.content.Context;
import android.preference.PreferenceManager;

import net.jonmiranda.prompts.R;
import net.jonmiranda.prompts.storage.Storage;

import dagger.ObjectGraph;

public class PromptApplication extends Application {

    Storage mStorage;

    private ObjectGraph mObjectGraph;

    @Override
    public void onCreate() {
        super.onCreate();
        AppModule module = new AppModule(this);
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

    public int getThemeColor() {
        return PreferenceManager.getDefaultSharedPreferences(this)
                .getInt(getString(R.string.set_theme_key), getResources().getColor(R.color.brand));
    }

    public ObjectGraph createScopedGraph(Object module) {
        return mObjectGraph.plus(module);
    }

    public static PromptApplication get(Context context) {
        return (PromptApplication) context.getApplicationContext();
    }
}

