package net.jonmiranda.prompts.models;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class Prompt extends RealmObject {

    private String title;

    private boolean isHidden;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean getIsHidden() {
        return isHidden;
    }

    public void setIsHidden(boolean hidden) {
        this.isHidden = hidden;
    }

    public static Prompt newInstance(String title, boolean hide) {
        Prompt prompt = new Prompt();
        prompt.setTitle(title);
        prompt.setIsHidden(hide);
        return prompt;
    }
}
