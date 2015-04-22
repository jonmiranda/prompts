package net.jonmiranda.prompts.storage;

import net.jonmiranda.prompts.models.Prompt;

import io.realm.RealmResults;

public interface Storage {

    String getResponse(CharSequence date, CharSequence prompt);

    void save(CharSequence date, CharSequence prompt, CharSequence response);

    RealmResults<Prompt> getAllResponses();
}
