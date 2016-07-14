package interdroid.swan.ui;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import interdroid.swan.R;
import nl.sense_os.service.constants.SensePrefs;

public class SwanConfigActivity extends Activity implements OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {
    private static Preference loginPref, logoutPref, settingsPref, registrationPref, cuckooPref;
    private static CheckBoxPreference senseCheckBox, cuckooCheckBox;
    private PreferenceFragment configFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, configFragment = new SwanConfigFragment())
                .commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);
        logoutPref.setOnPreferenceClickListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

        switch (key) {
            case "pref_key_sense":
                boolean senseEnabled = sharedPreferences.getBoolean(key, false);
                senseCheckBox.setTitle(senseEnabled ? "On" : "Off");
                enableSenseSettings(senseEnabled);
                break;

            case "pref_key_cuckoo_on_off":
                boolean cuckooEnabled = sharedPreferences.getBoolean(key, false);
                cuckooCheckBox.setTitle(cuckooEnabled ? "On" : "Off");
                enableCuckooSettings(cuckooEnabled);
                break;
        }
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        switch (preference.getKey()) {
            case "pref_key_sense_logout":
                logoutSense();
                return true;
        }
        return false;
    }

    private static void enableSenseSettings(boolean enable) {
        loginPref.setEnabled(enable);
        logoutPref.setEnabled(enable);
        settingsPref.setEnabled(enable);
        registrationPref.setEnabled(enable);
    }

    private static void enableCuckooSettings(boolean enable) {
        cuckooPref.setEnabled(enable);
    }

    private void logoutSense() {
        // clear cached settings of the previous user (e.g. device id)
        SharedPreferences.Editor authEditor = getSharedPreferences(SensePrefs.AUTH_PREFS, MODE_PRIVATE).edit();
        authEditor.clear();
        authEditor.apply();

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(SwanConfigActivity.this, R.string.logout_success, Toast.LENGTH_LONG)
                        .show();
            }
        });

    }

    public static class SwanConfigFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.swan_configuration);

            getPreferenceScreen().setOnPreferenceClickListener((Preference.OnPreferenceClickListener) getActivity());

            loginPref = findPreference("pref_key_sense_login");
            logoutPref = findPreference("pref_key_sense_logout");
            registrationPref = findPreference("pref_key_sense_register");
            settingsPref = findPreference("pref_key_sense_settings");
            cuckooPref = findPreference("pref_key_cuckoo");
            senseCheckBox = (CheckBoxPreference) findPreference("pref_key_sense");
            cuckooCheckBox = (CheckBoxPreference) findPreference("pref_key_cuckoo_on_off");

            boolean senseEnabled = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("pref_key_sense", false);
            senseCheckBox.setTitle(senseEnabled ? "On" : "Off");
            enableSenseSettings(senseEnabled);

            boolean cuckooEnabled = PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("pref_key_cuckoo_on_off", false);
            cuckooCheckBox.setTitle(cuckooEnabled ? "On" : "Off");
            enableCuckooSettings(cuckooEnabled);
        }
    }
}
