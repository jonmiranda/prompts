package net.jonmiranda.prompts.presenters;

import com.squareup.otto.Bus;

import net.jonmiranda.prompts.events.LoggedInEvent;
import net.jonmiranda.prompts.views.PasscodeView;

import javax.inject.Inject;

public class PasscodePresenter implements BasePresenter {

    public static final int PASSCODE_LENGTH = 4;

    private PasscodeView mView;
    @Inject Bus mBus;

    StringBuilder mInput = null;
    String mPasscode;

    public PasscodePresenter(PasscodeView view, String passcode) {
        mView = view;
        mInput = new StringBuilder(PASSCODE_LENGTH);
        mPasscode = passcode;
    }

    public void clearInput() {
        mInput.setLength(0);
    }

    public void addKey(int key) {
        mInput.append(key);
    }

    public void tryPasscode() {
        String input = mInput.toString();
        if (input.equals(mPasscode)) {
            mBus.post(new LoggedInEvent());
        } else if (input.length() == PASSCODE_LENGTH) {
            mView.showErrorMessage();
            mView.clearInput();
            clearInput();
        }
    }

    @Override
    public void onResume() {
        mBus.register(this);
    }

    @Override
    public void onPause() {
        mBus.unregister(this);
    }
}
