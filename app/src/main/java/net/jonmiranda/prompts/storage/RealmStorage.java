package net.jonmiranda.prompts.storage;

import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.models.UserResponse;

import java.util.Calendar;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class RealmStorage implements Storage {

    final Realm mRealm;

    public RealmStorage(Realm realm) {
        mRealm = realm;
    }

    @Override
    public void initializePrompts(String[] titles) {
        RealmResults<Prompt> results = mRealm.allObjects(Prompt.class);
        if (results.size() == 0) {
            mRealm.beginTransaction();
            for (String title : titles) {
                mRealm.copyToRealm(Prompt.newInstance(title, true));
            }
            mRealm.commitTransaction();
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
    public RealmResults<Prompt> getPrompts() {
        return mRealm.where(Prompt.class).equalTo("isHidden", false).findAll();
    }

    @Override
    public Prompt getPrompt(String title) {
        return mRealm.where(Prompt.class)
                .equalTo("title", title)
                .findFirst();
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
    public RealmResults<UserResponse> getAllResponses() {
        RealmResults<UserResponse> results = mRealm.allObjects(UserResponse.class);
        results.sort("created");
        return results;
    }
}
