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

import net.jonmiranda.prompts.app.PromptApplication;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Fragment that displays a prompt.
 */
public class PromptFragment extends Fragment implements PromptView {

    @InjectView(R.id.prompt) TextView mPrompt;
    @InjectView(R.id.editor) EditText mEditor;

    private PromptPresenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.inject(this, root);
        mPresenter = new PromptPresenter(this, getArguments());
        ((PromptApplication) getActivity().getApplication()).inject(mPresenter);
        return root;
    }

    @Override
    public void setColor(int color) {
        GradientDrawable shape = (GradientDrawable) mPrompt.getBackground();
        shape.setColor(color);
        shape.invalidateSelf();
        mEditor.setHighlightColor(color);
    }

    @Override
    public void setPrompt(String prompt) {
        mPrompt.setText(prompt);
    }

    /**
     * The footer takes up the rest of the Editor content area so users may expect
     * that they're clicking within the EditText.
     */
    @OnClick(R.id.footer)
    public void onFooterClicked(View view) {
        showKeyboard();
    }

    /**
     * TODO: Move this to the presenter.. or probably even the Activity
     */
    public void showKeyboard() {
        FragmentActivity context = getActivity();
        context.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditor, InputMethodManager.SHOW_IMPLICIT);
    }


    @OnTextChanged(R.id.editor)
    public void onEditorChanged(CharSequence text) {
        mPresenter.createOrUpdatePrompt(text); // TODO: Heavy(?)
    }

    @Override
    public void setResponse(String response) {
        mEditor.setText(response);
        mEditor.setSelection(mEditor.getText().length());
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.tryGetResponse();
    }
}

