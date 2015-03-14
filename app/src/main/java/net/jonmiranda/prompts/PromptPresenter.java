package net.jonmiranda.prompts;

import android.os.Bundle;

import net.jonmiranda.prompts.models.Prompt;

import org.json.JSONException;
import org.json.JSONObject;

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
    private String mDate;

    public PromptPresenter(PromptView view, Bundle arguments) {
        mView = view;

        if (arguments != null) {
            mPrompt = arguments.getString(PromptView.PROMPT_KEY, "No prompt.");
            mView.setPrompt(mPrompt);
            mView.setColor(arguments.getInt(PromptView.COLOR_KEY, 0));
            mDate = arguments.getString(PromptView.DATE_KEY, "No date.");
        }
    }

    /**
     * Checks the database to see if a response exists,
     * and if true it updates the view with the response.
     */
    public void tryGetResponse() {
        String prompt = mPrompt;
        RealmResults<Prompt> results = mRealm.where(Prompt.class)
                .equalTo("date", mDate)
                .equalTo("prompt", prompt)
                .equalTo("key", mDate + prompt)
                .findAll();

        if (results.size() > 0) {
            mView.setResponse(results.get(0).getResponse());
        }
    }

    public void createOrUpdatePrompt(CharSequence response) {
        JSONObject object = new JSONObject();
        try {
            object.put("date", mDate);
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
