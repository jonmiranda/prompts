package net.jonmiranda.prompts.models;

import java.util.Calendar;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.RealmClass;

@RealmClass
public class UserResponse extends RealmObject {

    private Prompt prompt;

    private String response;

    private Date created;

    private Date lastModified;

    public Prompt getPrompt() {
        return prompt;
    }

    public void setPrompt(Prompt prompt) {
        this.prompt = prompt;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public static UserResponse newInstance(Prompt prompt, Date created) {
        UserResponse response = new UserResponse();
        response.setPrompt(prompt);
        response.setResponse("");
        response.setCreated(created);
        response.setLastModified(Calendar.getInstance().getTime());
        return response;
    }
}
