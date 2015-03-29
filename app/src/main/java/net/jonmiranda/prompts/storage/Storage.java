package net.jonmiranda.prompts.storage;

public interface Storage {

    public String getResponse(CharSequence date, CharSequence prompt);

    public void save(CharSequence date, CharSequence prompt, CharSequence response);

}
