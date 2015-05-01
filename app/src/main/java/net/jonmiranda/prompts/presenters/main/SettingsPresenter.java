package net.jonmiranda.prompts.presenters.main;

import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.models.UserResponse;
import net.jonmiranda.prompts.presenters.BasePresenter;
import net.jonmiranda.prompts.storage.Storage;
import net.jonmiranda.prompts.views.settings.SettingsView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public class SettingsPresenter implements BasePresenter {

    private SettingsView mView;
    private Storage mStorage;

    public SettingsPresenter(SettingsView view, Storage storage) {
        mView = view;
        mStorage = storage;
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

    public String createTextBody() {
        StringBuilder jsonBuilder = new StringBuilder();
        List<UserResponse> prompts = mStorage.getAllResponses();

        if (prompts.size() > 0) {
            Date date = null;
            for (UserResponse prompt : prompts) {
                if (date == null || !date.equals(prompt.getCreated())) {
                    date = prompt.getCreated();
                    jsonBuilder.append(Utils.getPrettyDateString(prompt.getCreated()) + "\n");
                }
                if (prompt.getResponse() != null && !prompt.getResponse().isEmpty()) {
                    jsonBuilder.append(String.format("\n%s\n%s\n",
                            prompt.getPrompt().getTitle(),
                            prompt.getResponse()));
                }
            }
        }
        return jsonBuilder.toString();
    }

    public File createJsonFile(File root) {
        List<UserResponse> prompts = mStorage.getAllResponses();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (UserResponse prompt : prompts) {
            jsonBuilder.append(
                    String.format("\n  {\n    \"date\" : \"%s\",\n    \"prompt\" : \"%s\",\n    \"response\" : \"%s\"\n  },",
                    prompt.getCreated(), prompt.getPrompt().getTitle(), prompt.getResponse().replace("\n", "\\n").replace("\"", "\\\"")));
        }
        if (jsonBuilder.lastIndexOf(",") >= 0) {
            jsonBuilder.deleteCharAt(jsonBuilder.lastIndexOf(","));
        }
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
