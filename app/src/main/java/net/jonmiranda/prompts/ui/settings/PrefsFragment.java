package net.jonmiranda.prompts.ui.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;

import com.squareup.otto.Bus;
import com.squareup.otto.Subscribe;

import net.jonmiranda.prompts.R;
import net.jonmiranda.prompts.app.PromptApplication;
import net.jonmiranda.prompts.events.ThemeChangeEvent;
import net.jonmiranda.prompts.modules.SettingsModule;
import net.jonmiranda.prompts.presenters.settings.SettingsPresenter;

import java.io.File;

import javax.inject.Inject;

import dagger.ObjectGraph;

public class PrefsFragment extends android.preference.PreferenceFragment implements ThemeChangeEvent.Listener {

    @Inject SettingsPresenter mPresenter;
    @Inject Bus mBus;

    @Inject PromptApplication mApplication;
    ObjectGraph mGraph;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mGraph = PromptApplication.get(getActivity())
                .createScopedGraph(new SettingsModule((SettingsActivity) getActivity()));
        mGraph.inject(this);

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

        final Preference setThemesPref = findPreference(getString(R.string.set_theme_key));
        setThemesPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {

                ColorPickerDialog dialog = ColorPickerDialog.newInstance(mApplication.getThemeColor());
                ((PromptApplication) getActivity().getApplication()).inject(dialog);
                dialog.show(getFragmentManager(), ColorPickerDialog.TAG);
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

    @Subscribe @Override
    public void onThemeChanged(ThemeChangeEvent event) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(getString(R.string.set_theme_key), event.themeColor);
        editor.apply();
    }

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
    public void onDestroy() {
        super.onDestroy();
        mGraph = null;
    }
}