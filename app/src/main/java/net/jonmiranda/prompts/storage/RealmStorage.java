package net.jonmiranda.prompts.storage;

import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.models.UserResponse;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 *  One of the cool things about Realm is that the queries return live data.
 *  So RealmResults are updated automagically.
 */
public class RealmStorage implements Storage {

    final Realm mRealm;

    public RealmStorage(Realm realm) {
        mRealm = realm;
    }

    @Override
    public void initializePrompts(String[] titles) {
        RealmResults<Prompt> results = mRealm.allObjects(Prompt.class);
        if (results.size() == 0) {
            for (String title : titles) {
                createPrompt(title, true);
            }
        }
    }

    @Override
    public UserResponse getResponse(Date date, Prompt prompt) {
        UserResponse response = mRealm.where(UserResponse.class)
                .equalTo("prompt.title", prompt.getTitle())
                .equalTo("created", date)
                .findFirst();

        if (response == null) {
            response = UserResponse.newInstance(prompt, date);
        }

        return response;
    }

    @Override
    public void updatePrompt(String key, String newTitle) {
            Prompt prompt = mRealm.where(Prompt.class).equalTo("key", key).findFirst();
            mRealm.beginTransaction();
            prompt.setTitle(newTitle);
            mRealm.commitTransaction();
    }

    @Override
    public Prompt createPrompt(String title, boolean visible) {
        Prompt prompt = Prompt.newInstance(UUID.randomUUID().toString(), title, visible);
        mRealm.beginTransaction();
        prompt = mRealm.copyToRealm(prompt);
        mRealm.commitTransaction();
        return prompt;
    }

    @Override
    public List<Prompt> getPrompts() {
        return mRealm.where(Prompt.class).equalTo("isVisible", true).findAll();
    }

    @Override
    public List<Prompt> getAllPrompts() {
        return mRealm.allObjects(Prompt.class);
    }

    @Override
    public Prompt getPrompt(String key) {
        return mRealm.where(Prompt.class)
                .equalTo("key", key)
                .findFirst();
    }

    @Override
    public Prompt save(Prompt prompt, boolean visible) {
        mRealm.beginTransaction();
        prompt.setIsVisible(visible);

        if (!prompt.isValid()) {
            prompt = mRealm.copyToRealm(prompt);
        }
        mRealm.commitTransaction();
        return prompt;
    }

    @Override
    public UserResponse save(UserResponse userResponse, String response) {
        mRealm.beginTransaction();
        userResponse.setResponse(response);
        userResponse.setLastModified(Calendar.getInstance().getTime());
        if (!userResponse.isValid()) {
            userResponse = mRealm.copyToRealm(userResponse);
        }
        mRealm.commitTransaction();
        return userResponse;
    }

    @Override
    public List<UserResponse> getAllResponses() {
        RealmResults<UserResponse> results = mRealm.allObjects(UserResponse.class);
        results.sort("created");
        return results;
    }
}
