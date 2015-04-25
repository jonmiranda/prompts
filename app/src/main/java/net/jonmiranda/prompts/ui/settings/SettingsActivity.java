package net.jonmiranda.prompts.ui.settings;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import net.jonmiranda.prompts.R;
import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.presenters.SettingsPresenter;
import net.jonmiranda.prompts.views.SettingsView;

import java.io.File;

public class SettingsActivity extends ActionBarActivity implements SettingsView {

    static SettingsPresenter mPresenter; // TODO: Make non static?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mPresenter = new SettingsPresenter(this);
        ((PromptApplication) getApplication()).inject(mPresenter);

        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showInvalidPasscodeError() {
        showMessage(getString(R.string.invalid_passcode_error));
    }
    @Override
    public void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter = null;
    }

    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.preferences);

            final Preference editPromptsPref = findPreference(getString(R.string.edit_prompts_key));
            editPromptsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent intent = new Intent(getActivity(), EditPromptsActivity.class);
                    startActivity(intent);
                    return true;
                }
            });

            final EditTextPreference setPasscodePref =
                    (EditTextPreference) findPreference(getString(R.string.set_passcode_key));
            setPasscodePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    return mPresenter.parsePasscode(newValue.toString());
                }
            });

            final Preference json = findPreference(getString(R.string.export_json_key));
            json.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    boolean error = false;
                    StringBuilder errorMessage = new StringBuilder();

                    File root = Environment.getExternalStorageDirectory();
                    error = !root.canWrite();
                    if (!error) {
                        File file = mPresenter.createJsonFile(root);

                        Intent sendIntent = new Intent(Intent.ACTION_SEND);
                        sendIntent.putExtra(Intent.EXTRA_SUBJECT, "prompts.json");
                        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
                        sendIntent.setType("text/html");

                        error = error || sendIntent.resolveActivity(getActivity().getPackageManager()) == null;
                        if (!error) {
                            startActivity(Intent.createChooser(sendIntent,
                                    getActivity().getString(R.string.share)));
                        } else {
                            errorMessage.append(
                                    getActivity().getString(R.string.cant_export_json_error));
                        }
                    } else {
                        errorMessage.append(getActivity().getString(
                                R.string.cant_export_json_permissions_error));
                    }

                    if (error) {
                        mPresenter.passMessageToView(errorMessage.toString());
                    }
                    return true;
                }
            });

            final Preference exportTextKey = findPreference(getString(R.string.export_text_key));
            exportTextKey.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    Intent sendIntent = new Intent(Intent.ACTION_SEND);
                    sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                    sendIntent.putExtra(Intent.EXTRA_TEXT, mPresenter.createTextBody());
                    sendIntent.setType("plain/text");
                    boolean error = sendIntent.resolveActivity(getActivity().getPackageManager()) == null;
                    if (!error) {
                        startActivity(Intent.createChooser(sendIntent, getActivity().getString(R.string.share)));
                    }
                    return error;
                }
            });
        }
    }

}
