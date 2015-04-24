package net.jonmiranda.prompts.views;

public interface PromptView {

    public static final String PROMPT_KEY = "PROMPT_KEY";
    public static final String COLOR_KEY = "COLOR_KEY";
    public static final String DATE_KEY = "DATE_KEY";

    void setColor(int color);

    void setPromptTitle(String prompt);

    void setResponse(String response);

    void showKeyboard();
}
