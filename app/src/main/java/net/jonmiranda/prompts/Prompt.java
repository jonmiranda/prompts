package net.jonmiranda.prompts;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Database model of a Prompt.
 */
public class Prompt extends RealmObject {

    @PrimaryKey
    private String key;

    private String date;

    private String prompt;
    private String response;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrompt() {
        return prompt;
    }

    public void setPrompt(String prompt) {
        this.prompt = prompt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }
}
