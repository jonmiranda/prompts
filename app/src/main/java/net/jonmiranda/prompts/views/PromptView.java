package net.jonmiranda.prompts.views;

public interface PromptView {

    String PROMPT_KEY = "PROMPT_KEY";
    String COLOR_KEY = "COLOR_KEY";
    String DATE_KEY = "DATE_KEY";

    void setColor(int color);

    void setPromptTitle(String prompt);

    void setResponse(String response);

    void showKeyboard();
}
