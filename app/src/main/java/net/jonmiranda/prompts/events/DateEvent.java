package net.jonmiranda.prompts.events;

import java.util.Date;

public class DateEvent {

    public final Date date;

    public DateEvent(Date date) {
        this.date = date;
    }

    public static interface Listener {
        void onDateChanged(DateEvent event);
    }
}
