package net.jonmiranda.prompts;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.presenters.PasscodePresenter;
import net.jonmiranda.prompts.views.PasscodeView;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.InjectViews;
import butterknife.OnClick;


public class PasscodeFragment extends Fragment implements PasscodeView {

    @InjectViews({
            R.id.zero, R.id.one, R.id.two, R.id.three, R.id.four,
            R.id.five, R.id.six, R.id.seven, R.id.eight, R.id.nine
    })
    List<TextView> mKeys;

    @InjectView(R.id.user_input) TextView mUserInput;

    PasscodePresenter mPresenter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.passcode_layout, container, false);
        ButterKnife.inject(this, root);
        mPresenter = new PasscodePresenter(this);
        ((PromptApplication) getActivity().getApplication()).inject(mPresenter);
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
        }
    }

    @Override @OnClick(R.id.clear)
    public void clearInput() {
        mUserInput.setText("");
        mPresenter.clearInput();
    }

    @Override
    public void showMessage(String text) {
        Toast.makeText(getActivity(), text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    public static PasscodeFragment newInstance() {
        PasscodeFragment fragment = new PasscodeFragment();
        Bundle bundle = new Bundle();
        fragment.setArguments(bundle);
        return fragment;
    }
}

