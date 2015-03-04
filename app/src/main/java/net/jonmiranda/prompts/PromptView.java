package net.jonmiranda.prompts;

public interface PromptView {

    public static final String PROMPT_KEY = "PROMPT_KEY";
    public static final String COLOR_KEY = "COLOR_KEY";

    void setColor(int color);

    void setPrompt(String prompt);

    void setResponse(String response);
}
