package net.jonmiranda.prompts.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.RealmClass;

@RealmClass
public class Prompt extends RealmObject {

    @PrimaryKey private String key;

    private String title;

    private boolean isVisible;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(boolean hidden) {
        this.isVisible = hidden;
    }

    public static Prompt newInstance(String key, String title, boolean visible) {
        Prompt prompt = new Prompt();
        prompt.setKey(key);
        prompt.setTitle(title);
        prompt.setIsVisible(visible);
        return prompt;
    }
}
