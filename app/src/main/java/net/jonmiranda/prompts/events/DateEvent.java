package net.jonmiranda.prompts.events;

public class DateEvent {

    public final String date; // TODO: Use dates properly so we can avoid this 'pretty' nonsense
    public final String pretty; // Pretty String representation of the Date.

    public DateEvent(String date, String pretty) {
        this.date = date;
        this.pretty = pretty;
    }

    public static interface Listener {
        void onDateChanged(DateEvent event);
    }
}
