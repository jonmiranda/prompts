package net.jonmiranda.prompts.ui.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;

import com.squareup.otto.Bus;

import net.jonmiranda.prompts.R;
import net.jonmiranda.prompts.events.PromptUpdateEvent;

import javax.inject.Inject;

public class PromptItemDialog extends DialogFragment {

    public static final String TAG = PromptItemDialog.class.getCanonicalName();
    public static final String PROMPT_KEY = "PROMPT_KEY";
    public static final String ORIGINAL_TITLE_KEY = "ORIGINAL_TITLE_KEY";

    @Inject Bus mBus;

    public String mPromptKey;

    @Override
    public void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);

        String title = null;
        Bundle args = getArguments();
        if (args != null) {
            mPromptKey = args.getString(PROMPT_KEY);
            title = args.getString(ORIGINAL_TITLE_KEY);
        }

        final EditText editText = new EditText(getActivity());
        editText.setText(title);
        editText.setSelection(editText.getText().length());

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(editText)
                .setTitle(title == null ? getString(R.string.add_new_prompt)
                        : getString(R.string.edit_prompt_title))
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        mBus.post(new PromptUpdateEvent(mPromptKey, editText.getText().toString()));
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        getDialog().cancel();
                    }
                });
        return builder.create();
    }

    public static PromptItemDialog newInstance(String key, String title) {
        Bundle args = new Bundle();
        args.putString(ORIGINAL_TITLE_KEY, title);
        args.putString(PROMPT_KEY, key);

        PromptItemDialog dialog = new PromptItemDialog();
        dialog.setArguments(args);
        return dialog;
    }
}
