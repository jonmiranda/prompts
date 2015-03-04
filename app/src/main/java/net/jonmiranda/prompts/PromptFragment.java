package net.jonmiranda.prompts;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Fragment that displays a prompt.
 */
public class PromptFragment extends Fragment {

    @InjectView(R.id.prompt) TextView mPrompt;
    @InjectView(R.id.editor) EditText mEditor;

    public static final String PROMPT_KEY = "PROMPT_KEY";
    public static final String COLOR_KEY = "COLOR_KEY";

    private Realm mRealm;
    private PromptPresenter mPresenter;

    public PromptFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, root);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mPrompt.setText(arguments.getString(PROMPT_KEY, "No prompt."));

            GradientDrawable shape = (GradientDrawable) mPrompt.getBackground();
            int color = arguments.getInt(COLOR_KEY);
            shape.setColor(color);
            shape.invalidateSelf();

            mEditor.setHighlightColor(color);
        }

        mRealm = Realm.getInstance(getActivity());
        return root;
    }

    /**
     * @return Today's date as a string.
     */
    private String getTodaysDate() {
        Calendar date = Calendar.getInstance();
        return String.format("%d-%d-%d",
                date.get(Calendar.DATE), date.get(Calendar.MONTH), date.get(Calendar.YEAR));
    }

    private void createOrUpdatePrompt() {
        JSONObject object = new JSONObject();
        try {
            object.put("date", getTodaysDate());
            object.put("prompt", mPrompt.getText());
            object.put("key", object.getString("date") + object.getString("prompt"));
            object.put("response", mEditor.getText());
        } catch (JSONException e) {
        }
        mRealm.beginTransaction();
        mRealm.createOrUpdateObjectFromJson(Prompt.class, object);
        mRealm.commitTransaction();
    }

    /**
     * The footer takes up the rest of the Editor content area so users may expect
     * that they're clicking within the EditText.
     */
    @OnClick(R.id.footer)
    public void onFooterClicked(View view) {
        showKeyboard();
    }

    @OnTextChanged(R.id.editor)
    public void onEditorChanged(CharSequence text) {
        createOrUpdatePrompt(); // TODO: Heavy(?)
    }

    @Override
    public void onResume() {
        super.onResume();

        String date = getTodaysDate();
        String prompt = mPrompt.getText().toString();
        RealmResults<Prompt> results = mRealm.where(Prompt.class)
                .equalTo("date", date)
                .equalTo("prompt", prompt)
                .equalTo("key", date + prompt)
                .findAll();

        if (results.size() > 0) {
            mEditor.setText(results.get(0).getResponse());
            mEditor.setSelection(mEditor.getText().length());
        }

        showKeyboard();
    }

    private void showKeyboard() {
        FragmentActivity context = getActivity();
        context.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditor, InputMethodManager.SHOW_IMPLICIT);
    }
}

