package net.jonmiranda.prompts.app;

import android.app.Application;

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
}

