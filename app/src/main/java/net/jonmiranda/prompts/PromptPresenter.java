package net.jonmiranda.prompts;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.events.DateEvent;
import net.jonmiranda.prompts.models.Prompt;

import org.json.JSONException;
import org.json.JSONObject;

import javax.inject.Inject;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Presentation logic for a {@link PromptView}.
 */
public class PromptPresenter implements DateEvent.Listener {

    private PromptView mView;
    @Inject Realm mRealm;
    @Inject Bus mBus;

    private String mPrompt;
    private String mDate;

    public PromptPresenter(PromptView view, String prompt, String date, int color) {
        mView = view;
        mPrompt = prompt;
        mDate = date;

        mView.setPrompt(mPrompt);
        mView.setColor(color);
    }

    /**
     * Checks the database to see if a response exists,
     * and if true it updates the view with the response.
     */
    public void tryGetResponse() {
        String prompt = mPrompt;
        RealmResults<Prompt> results = mRealm.where(Prompt.class)
                .equalTo("key", mDate + prompt)
                .findAll();

        if (results.size() > 0) {
            mView.setResponse(results.get(0).getResponse());
        } else {
            mView.setResponse("");
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

    @Override @Subscribe
    public void onDateChanged(DateEvent event) {
        mDate = Utils.getRealmDateString(event.date);
        tryGetResponse();
    }

    public void onResume() {
        mBus.register(this);
        tryGetResponse();
    }

    public void onPause() {
        mBus.unregister(this);
    }
}
