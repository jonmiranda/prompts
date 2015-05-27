package net.jonmiranda.prompts.views.main;

public interface PromptView {

    String PROMPT_KEY = "PROMPT_KEY";
    String DATE_KEY = "DATE_KEY";

    void setColor(int color);

    void setPromptTitle(String prompt);

    void setResponse(String response);

    void showKeyboard();
}
