package net.jonmiranda.prompts.events;

public class ThemeChangeEvent {

    public final int themeColor;

    public ThemeChangeEvent(int themeColor) {
        this.themeColor = themeColor;
    }

    public static interface Listener {
        void onThemeChanged(ThemeChangeEvent event);
    }
}
