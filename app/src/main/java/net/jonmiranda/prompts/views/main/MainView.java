package net.jonmiranda.prompts.views.main;

import net.jonmiranda.prompts.models.Prompt;

import java.util.Calendar;
import java.util.List;

public interface MainView {

    void showDate(Calendar date);

    void showPreviousDate();

    void showNextDate();

    void showDatePicker();

    void initializeAdapter(List<Prompt> prompts);

    void showPrompts();

    void showLogin();

}
