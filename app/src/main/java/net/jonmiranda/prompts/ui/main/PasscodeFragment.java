package net.jonmiranda.prompts.ui.main;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import net.jonmiranda.prompts.R;
import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.modules.PasscodeModule;
import net.jonmiranda.prompts.presenters.main.PasscodePresenter;
import net.jonmiranda.prompts.views.main.PasscodeView;

import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;
import dagger.ObjectGraph;


public class PasscodeFragment extends Fragment implements PasscodeView {

    @InjectViews({
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four,
            R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
    })
    List<TextView> mKeys;

    @InjectView(R.id.user_input) TextView mUserInput;
    @InjectView(R.id.border) View mBorder;

    ObjectGraph mGraph;
    @Inject PromptApplication mApplication;
    @Inject PasscodePresenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.passcode_layout, container, false);
        ButterKnife.inject(this, root);
        mGraph = PromptApplication.get(getActivity()).createScopedGraph(new PasscodeModule(this));
        mGraph.inject(this);
        initListeners();
        return root;
    }

    private void initListeners() {
        for (int i = 0; i < mKeys.size(); ++i) {
            final int key = i;
            mKeys.get(key).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View view) {
                    mUserInput.setText(mUserInput.getText() + Integer.toString(key));
                    mPresenter.addKey(key);
                    mPresenter.tryPasscode();
                    ValueAnimator animator = new ValueAnimator();
                    animator.setDuration(300).addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            view.getBackground().setColorFilter(0xFFEEEEEE, PorterDuff.Mode.MULTIPLY);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            view.getBackground().clearColorFilter();
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    animator.setFloatValues(0, 1);
                    animator.start();
                }
            });
            mKeys.get(i).setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_DOWN) {
                        v.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY);
                    }
                    return false;
                }
            });
        }
    }

    @Override @OnClick(R.id.clear)
    public void clearInput() {
        mUserInput.setText("");
        mPresenter.clearInput();
    }

    @Override
    public void showErrorMessage() {
        Toast.makeText(getActivity(), getString(R.string.wrong_passcode),
                Toast.LENGTH_SHORT).show();
    }

    private void hideKeyboard() {
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager im = (InputMethodManager)
                    getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            im.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    private void applyThemeColor(int color) {
        mBorder.setBackgroundColor(color);
    }

    @Override
    public void onResume() {
        super.onResume();
        applyThemeColor(mApplication.getThemeColor());
        hideKeyboard();
    }
    
    @Override
    public void onDestroy() {
        mGraph = null;
        super.onDestroy();
    }

    public static PasscodeFragment newInstance() {
        PasscodeFragment fragment = new PasscodeFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}

