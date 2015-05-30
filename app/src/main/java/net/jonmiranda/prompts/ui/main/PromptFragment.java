package net.jonmiranda.prompts.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import net.jonmiranda.prompts.R;
import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.app.Utils;
import net.jonmiranda.prompts.modules.PromptModule;
import net.jonmiranda.prompts.presenters.main.PromptPresenter;
import net.jonmiranda.prompts.views.main.PromptView;

import java.util.Calendar;
import java.util.Date;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dagger.ObjectGraph;

/**
 * Fragment that displays a prompt.
 */
public class PromptFragment extends Fragment implements PromptView {

    @InjectView(R.id.prompt) TextView mPrompt;
    @InjectView(R.id.editor) EditText mEditor;
    @InjectView(R.id.border) View mBorder;

    @Inject PromptApplication mApplication;
    @Inject PromptPresenter mPresenter;
    ObjectGraph mGraph;

    private TextWatcher mTextWatcher;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.prompt_layout, container, false);
        ButterKnife.inject(this, root);

        mPrompt.setEllipsize(TextUtils.TruncateAt.END);
        mPrompt.setMaxLines(2);

        String promptKey = getString(R.string.untitled); // TODO
        Date date = Utils.stripDate(Calendar.getInstance());

        Bundle arguments = getArguments();
        if (arguments != null) {
            promptKey = arguments.getString(PromptView.PROMPT_KEY, promptKey);
            date = (Date) arguments.getSerializable(PromptView.DATE_KEY);
        }

        mGraph = PromptApplication.get(getActivity()).createScopedGraph(new PromptModule(this));
        mGraph.inject(this);
        mPresenter.bind(promptKey, date);

        mTextWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                mPresenter.createOrUpdatePrompt(s.toString()); // TODO: Heavy(?)
            }
        };
        return root;
    }

    @Override
    public void setColor(int color) {
        mBorder.setBackgroundColor(color);
        mEditor.setHighlightColor(color);
    }

    @Override
    public void setPromptTitle(String prompt) {
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

    @Override
    public void showKeyboard() {
        mEditor.requestFocus();
        FragmentActivity context = getActivity();
        context.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        InputMethodManager imm = (InputMethodManager)
                context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(mEditor, InputMethodManager.SHOW_IMPLICIT);
    }

    @Override
    public void setResponse(String response) {
        mEditor.removeTextChangedListener(mTextWatcher);
        mEditor.setText(response);
        mEditor.setSelection(mEditor.getText().length());
        mEditor.addTextChangedListener(mTextWatcher);
    }

    private void applyThemeColor(int color) {
        mBorder.setBackgroundColor(color);
        mEditor.setHighlightColor(Utils.modifyColor(color, 1.7f));
    }

    @Override
    public void onResume() {
        applyThemeColor(mApplication.getThemeColor());
        mPresenter.onResume();
        mEditor.addTextChangedListener(mTextWatcher);
        super.onResume();
    }

    @Override
    public void onPause() {
        mPresenter.onPause();
        mEditor.removeTextChangedListener(mTextWatcher);
        super.onPause();
    }

    @Override
    public void onDestroy() {
        mGraph = null;
        super.onDestroy();
    }

    public static PromptFragment newInstance(String promptKey, Date date) {
        PromptFragment fragment = new PromptFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PROMPT_KEY, promptKey);
        bundle.putSerializable(DATE_KEY, date);
        fragment.setArguments(bundle);
        return fragment;
    }
}

