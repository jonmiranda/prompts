package net.jonmiranda.prompts.events;

public class PromptUpdateEvent {

    public final String key;
    public final String title;

    public PromptUpdateEvent(String key, String title) {
        this.key = key;
        this.title = title;
    }

    public static interface Listener {
        void onPromptChanged(PromptUpdateEvent event);
    }
}
