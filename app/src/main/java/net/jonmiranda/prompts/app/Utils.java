package net.jonmiranda.prompts.app;

import java.util.Calendar;

public class Utils {

    public static String getRealmDateString(Calendar date) {
        return String.format("%d-%d-%d",
                date.get(Calendar.DATE), date.get(Calendar.MONTH), date.get(Calendar.YEAR));
    }

    public static String getPrettyDateString(Calendar date) {
        return String.format("%1$tA %1$tB %1$te, %1$tY", date);
    }
}
