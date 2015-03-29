package net.jonmiranda.prompts.storage;

import net.jonmiranda.prompts.models.Prompt;

import org.json.JSONException;
import org.json.JSONObject;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmStorage implements Storage {

    final Realm mRealm;

    public RealmStorage(Realm realm) {
        mRealm = realm;
    }

    @Override
    public String getResponse(CharSequence date, CharSequence prompt) {
        RealmResults<Prompt> results = mRealm.where(Prompt.class)
                .equalTo("key", date.toString() + prompt.toString())
                .findAll();

        return results.size() <= 0 ? "" : results.get(0).getResponse();
    }

    @Override
    public void save(CharSequence date, CharSequence prompt, CharSequence response) {
        JSONObject object = new JSONObject();
        try {
            object.put("date", date);
            object.put("prompt", prompt);
            object.put("key", object.getString("date") + object.getString("prompt"));
            object.put("response", response);
        } catch (JSONException e) {
        }
        mRealm.beginTransaction();
        mRealm.createOrUpdateObjectFromJson(Prompt.class, object);
        mRealm.commitTransaction();
    }
}
