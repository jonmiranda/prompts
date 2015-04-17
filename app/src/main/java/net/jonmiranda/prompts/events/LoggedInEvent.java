package net.jonmiranda.prompts.events;

public class LoggedInEvent {


    public LoggedInEvent() {
    }

    public static interface Listener {
        void onLoggedIn(LoggedInEvent event);
    }
}
