package net.jonmiranda.prompts;

import android.os.Bundle;

import net.jonmiranda.prompts.models.Prompt;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Presentation logic for a {@link PromptView}.
 */
public class PromptPresenter {

    private PromptView mView;
    @Inject Realm mRealm;

    private String mPrompt;

    public PromptPresenter(PromptView view, Bundle arguments) {
        mView = view;

        if (arguments != null) {
            mPrompt = arguments.getString(PromptView.PROMPT_KEY, "No prompt.");
            mView.setPrompt(mPrompt);
            mView.setColor(arguments.getInt(PromptView.COLOR_KEY, 0));
        }
    }

    /**
     * Checks the database to see if a response exists,
     * and if true it updates the view with the response.
     */
    public void tryGetResponse() {
        String date = getTodaysDate();
        String prompt = mPrompt;
        RealmResults<Prompt> results = mRealm.where(Prompt.class)
                .equalTo("date", date)
                .equalTo("prompt", prompt)
                .equalTo("key", date + prompt)
                .findAll();

        if (results.size() > 0) {
            mView.setResponse(results.get(0).getResponse());
        }
    }

    /**
     * @return Today's date as a string.
     */
    private String getTodaysDate() {
        Calendar date = Calendar.getInstance();
        return String.format("%d-%d-%d",
                date.get(Calendar.DATE), date.get(Calendar.MONTH), date.get(Calendar.YEAR));
    }

    public void createOrUpdatePrompt(CharSequence response) {
        JSONObject object = new JSONObject();
        try {
            object.put("date", getTodaysDate());
            object.put("prompt", mPrompt);
            object.put("key", object.getString("date") + object.getString("prompt"));
            object.put("response", response);
        } catch (JSONException e) {
        }
        mRealm.beginTransaction();
        mRealm.createOrUpdateObjectFromJson(Prompt.class, object);
        mRealm.commitTransaction();
    }
}
