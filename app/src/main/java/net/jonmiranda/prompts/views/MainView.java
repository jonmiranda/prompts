package net.jonmiranda.prompts.views;

import java.util.Calendar;

public interface MainView {

    void showDate(Calendar date);

    void showPreviousDate();

    void showNextDate();

    void showDatePicker();

}
