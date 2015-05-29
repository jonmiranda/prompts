package net.jonmiranda.prompts.app;

import android.graphics.Color;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class Utils {

    public static String getPrettyDateString(Calendar date) {
        return String.format("%1$tA %1$tB %1$te, %1$tY", date);
    }

    public static String getPrettyDateString(Date date) {
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        return getPrettyDateString(calendar);
    }

    public static Date stripDate(Calendar date) {
        date.set(Calendar.HOUR_OF_DAY, 0);
        date.set(Calendar.MINUTE, 0);
        date.set(Calendar.SECOND, 0);
        date.set(Calendar.MILLISECOND, 0);
        return date.getTime();
    }

    public static int darkenColor(int color, float factor) {
        return Color.rgb(
                Math.min((int) (Color.red(color) * factor), 255),
                Math.min((int) (Color.green(color) * factor), 255),
                Math.min((int) (Color.blue(color) * factor), 255));
    }
}
