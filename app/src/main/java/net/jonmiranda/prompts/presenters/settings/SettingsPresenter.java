package net.jonmiranda.prompts.presenters.settings;

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
        List<UserResponse> responses = mStorage.getAllResponses();

        if (responses.size() > 0) {
            Date date = null;
            for (UserResponse response : responses) {
                if (date == null || !date.equals(response.getCreated())) {
                    date = response.getCreated();
                    jsonBuilder.append(Utils.getPrettyDateString(response.getCreated())).append("\n");
                }
                if (response.getPrompt() != null && response.getResponse() != null && !response.getResponse().isEmpty()) {
                    jsonBuilder.append(String.format("\n%s\n%s\n",
                            response.getPrompt().getTitle(),
                            response.getResponse()));
                }
            }
        }
        return jsonBuilder.toString();
    }

    public File createJsonFile(File root) {
        List<UserResponse> responses = mStorage.getAllResponses();
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("[");
        for (UserResponse response : responses) {
            if (response.getPrompt() != null) {
                jsonBuilder.append(
                        String.format("\n  {\n    \"date\" : \"%s\",\n    \"prompt\" : \"%s\",\n    \"response\" : \"%s\"\n  },",
                                response.getCreated(), response.getPrompt().getTitle(), response.getResponse().replace("\n", "\\n").replace("\"", "\\\"")));
            }
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
