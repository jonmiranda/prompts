package net.jonmiranda.prompts.events;

import java.util.Calendar;

public class DateEvent {

    public final Calendar date;

    public DateEvent(Calendar date) {
        this.date = date;
    }

    public static interface Listener {
        void onDateChanged(DateEvent event);
    }
}
