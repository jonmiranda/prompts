package net.jonmiranda.prompts.presenters;

import net.jonmiranda.prompts.models.Prompt;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.views.SettingsView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.inject.Inject;

import io.realm.RealmResults;

public class SettingsPresenter implements BasePresenter {

    private SettingsView mView;
    @Inject Storage mStorage;

    public SettingsPresenter(SettingsView view) {
        mView = view;
    }

    /**
     * @return True if passcode is valid, false otherwise.
     */
    public boolean parsePasscode(String passcode) {
        boolean isValid = false;
        try {
            Integer.parseInt(passcode);
            isValid = passcode.length() == 4;
        } catch (NumberFormatException e) {
        }

        if (!isValid) {
            mView.showInvalidPasscodeError();
        }
        return isValid;
    }

    public void passMessageToView(String message) {
        mView.showMessage(message);
    }

    public File createJsonFile(File root) {
        RealmResults<Prompt> prompts = mStorage.getAllResponses();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (Prompt prompt : prompts) {
            jsonBuilder.append(
                    String.format("\n  {\n    \"date\" : \"%s\",\n    \"prompt\" : \"%s\",\n    \"response\" : \"%s\"\n  },",
                    prompt.getDate(), prompt.getPrompt(), prompt.getResponse().replace("\n", "\\n").replace("\"", "\\\"")));
        }
        jsonBuilder.deleteCharAt(jsonBuilder.lastIndexOf(","));
        jsonBuilder.append("\n]");
        final String json = jsonBuilder.toString();
        final File jsonFile = new File(root, "prompts.json");
        jsonFile.setReadable(true);

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(jsonFile);
            out.write(json.getBytes());
            out.close();
        } catch (Exception e) {
            mView.showMessage(("Trouble creating JSON file.")); // TODO: Use string resource
        } finally {
            if (out != null) { // Such ugly
                try { // Much verbose
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return jsonFile;
    }

    @Override
    public void onResume() {
    }

    @Override
    public void onPause() {
    }
}
