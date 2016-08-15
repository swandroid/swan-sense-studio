package interdroid.swancore.sensors;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import interdroid.swancore.swansong.ExpressionFactory;
import interdroid.swancore.swansong.ExpressionParseException;
import interdroid.swancore.swansong.HistoryReductionMode;
import interdroid.swancore.swansong.SensorValueExpression;

/**
 * Base for ConfigurationActivities for configuring sensors.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public abstract class AbstractConfigurationActivity extends PreferenceActivity
        implements OnPreferenceChangeListener {
    private static final String TAG = AbstractConfigurationActivity.class
            .getSimpleName();

    private static final long SECOND = 1000;
    private static final long MINUTE = 60 * SECOND;
    private static final long HOUR = 60 * MINUTE;

    /**
     * Returns the id for the sensors preferences XML setup.
     *
     * @return the id for the preferences XML
     */
    public abstract int getPreferencesXML();

    private List<String> keys = new ArrayList<String>();

    private BroadcastReceiver mNameReceiver = new BroadcastReceiver() {

        @SuppressWarnings("deprecation")
        @Override
        public void onReceive(Context context, Intent intent) {
            List<String> names = intent.getStringArrayListExtra("names");
            names.add(0, "self");
            names.add(1, "wear");
            names.add(2, "NEARBY");
            ((ListPreference) findPreference("swan_location")).setEntries(names
                    .toArray(new String[names.size()]));
            ((ListPreference) findPreference("swan_location"))
                    .setEntryValues(names.toArray(new String[names.size()]));
            intentToPrefs();
        }

    };

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    private void intentToPrefs() {
        if (getIntent().hasExtra("expression")) {
            try {
                SensorValueExpression sensor = (SensorValueExpression) ExpressionFactory
                        .parse(getIntent().getStringExtra("expression"));
                updatePref("swan_location", sensor.getLocation());
                updatePref("history_window", "" + sensor.getHistoryLength());
                updatePref("history_reduction_mode", sensor
                        .getHistoryReductionMode().toParseString());
                updatePref("valuepath", sensor.getValuePath());
                for (String key : sensor.getConfiguration().keySet()) {
                    updatePref(key, sensor.getConfiguration().getString(key));
                }
                for (String key : sensor.getHttConfiguration().keySet()) {
                    updatePref(key, sensor.getHttConfiguration().getString(key));
                }
            } catch (ExpressionParseException e) {
                Log.d(TAG, "supplied expression cannot be parsed.", e);
            } catch (ClassCastException e) {
                Log.d(TAG, "supplied expression wrong type.", e);
            }
        } else {
            Log.d(TAG, "no edit");
        }
    }

    @SuppressWarnings("deprecation")
    private void updatePref(String key, String value) {
        findPreference(key).getEditor().putString(key, value).apply();
        findPreference(key).getOnPreferenceChangeListener().onPreferenceChange(
                findPreference(key), value);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onResume() {
        addPreferencesFromIntent(new Intent(
                "interdroid.swan.DEFAULT_PREFERENCES"));
        reAddPrefs(getPreferenceScreen());
        addPreferencesFromResource(getPreferencesXML());
        setupPrefs();

        setResult(RESULT_CANCELED);
        registerReceiver(mNameReceiver, new IntentFilter(
                "interdroid.swan.NAMES"));
        sendBroadcast(new Intent("interdroid.swan.GET_NAMES"));
        super.onResume();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mNameReceiver);
        super.onPause();
    }

    private void reAddPrefs(PreferenceGroup group) {
        // re add the preferences from the intent so that they will be bound
        // with the current context, rather than the context from the intent,
        // which leads to:
        // android.view.WindowManager$BadTokenException: Unable to add window --
        // token null is not for an application
        List<Preference> oldPrefs = new ArrayList<Preference>();
        List<Preference> newPrefs = new ArrayList<Preference>();
        for (int i = 0; i < group.getPreferenceCount(); i++) {

            Preference preference = group.getPreference(i);
            if (preference instanceof EditTextPreference) {
                oldPrefs.add(preference);
                EditTextPreference oldPref = (EditTextPreference) preference;
                EditTextPreference newPref = new EditTextPreference(this);
                newPref.getEditText().setInputType(
                        oldPref.getEditText().getInputType());
                newPref.setDialogMessage(oldPref.getDialogMessage());
                newPref.setDialogIcon(oldPref.getDialogIcon());
                newPref.setDependency(oldPref.getDependency());
                newPref.setDialogTitle(oldPref.getDialogTitle());
                newPref.setEnabled(oldPref.isEnabled());
                newPref.setIntent(oldPref.getIntent());
                newPref.setKey(oldPref.getKey());
                newPref.setOrder(oldPref.getOrder());
                newPref.setSummary(oldPref.getSummary());
                newPref.setText(oldPref.getText());
                newPref.setTitle(oldPref.getTitle());
                newPrefs.add(newPref);
            } else if (preference instanceof ListPreference) {
                oldPrefs.add(preference);
                ListPreference oldPref = (ListPreference) preference;
                ListPreference newPref = new ListPreference(this);
                newPref.setDialogMessage(oldPref.getDialogMessage());
                newPref.setDialogIcon(oldPref.getDialogIcon());
                newPref.setDependency(oldPref.getDependency());
                newPref.setDialogTitle(oldPref.getDialogTitle());
                newPref.setEnabled(oldPref.isEnabled());
                newPref.setIntent(oldPref.getIntent());
                newPref.setKey(oldPref.getKey());
                newPref.setOrder(oldPref.getOrder());
                newPref.setSummary(oldPref.getSummary());
                newPref.setTitle(oldPref.getTitle());
                newPref.setEntries(oldPref.getEntries());
                newPref.setEntryValues(oldPref.getEntryValues());
                newPrefs.add(newPref);
            } else if (preference instanceof PreferenceGroup) {
                reAddPrefs((PreferenceGroup) preference);
            } else if (preference instanceof CheckBoxPreference) {
                CheckBoxPreference oldPref = (CheckBoxPreference) preference;
                CheckBoxPreference newPref = new CheckBoxPreference(this);
                newPref.setChecked(oldPref.isChecked());
            } else {
                group.removePreference(preference);
                Log.d(TAG, "not re adding preference: '" + preference.getKey()
                        + "' not supported");
            }
        }
        for (Preference oldPref : oldPrefs) {
            group.removePreference(oldPref);
        }
        for (Preference newPref : newPrefs) {
            group.addPreference(newPref);
        }
    }


    /**
     * Sets up this activity.
     */
    @SuppressWarnings("deprecation")
    private void setupPrefs() {
        setupPref(null, getPreferenceScreen());
    }

    /**
     * Sets up using the given preferences.
     *
     * @param preference the preferences for the sensor.
     */
    private void setupPref(final PreferenceGroup parent,
                           final Preference preference) {
        if (preference instanceof PreferenceGroup) {
            int nrPrefs = ((PreferenceGroup) preference).getPreferenceCount();
            for (int i = nrPrefs - 1; i >= 0; i--) {
                // setup all sub prefs
                setupPref(((PreferenceGroup) preference),
                        ((PreferenceGroup) preference).getPreference(i));
            }
            // update nr prefs
            nrPrefs = ((PreferenceGroup) preference).getPreferenceCount();
            if (nrPrefs == 0) {
                parent.removePreference(preference);
            }
        } else {
            keys.add(preference.getKey());
            // setup the listener
            preference.setOnPreferenceChangeListener(this);
            // set the summary
            String summary = null;

            // setup location pref
            if (preference instanceof ListPreference) {
                try {
                    summary = ((ListPreference) preference).getValue()
                            .toString();
                } catch (NullPointerException e) {
                    Log.d(TAG, "Got null pointer while getting summary.", e);
                }
            } else if (preference instanceof EditTextPreference) {
                summary = ((EditTextPreference) preference).getText();
            }
            if (summary != null) {
                preference.setSummary(summary);
            }

            if (preference instanceof ListPreference) {
                if (((ListPreference) preference).getEntries() == null) {
                    return;
                }
                if (((ListPreference) preference).getEntries().length == 1) {
                    preference.setEnabled(false);
                }
                ((ListPreference) preference)
                        .setValue(((ListPreference) preference)
                                .getEntryValues()[0].toString());
                preference.setSummary(((ListPreference) preference)
                        .getEntries()[0]);
            }
            if (getIntent().hasExtra(preference.getKey())) {
                PreferenceManager
                        .getDefaultSharedPreferences(getBaseContext())
                        .edit()
                        .putString(
                                preference.getKey(),
                                ""
                                        + getIntent().getExtras().get(
                                        preference.getKey())).commit();

                // hide the pref.
                // parent.removePreference(preference);
                preference.setEnabled(false);
            }

        }
    }

    @Override
    public final void onBackPressed() {
        setResult(RESULT_OK,
                getIntent()
                        .putExtra("Expression", prefsToConfigurationString()));
        finish();
    }

    /**
     * Converts the prefs to a parseable configuration string.
     *
     * @return the prefs as a string.
     */
    private String prefsToConfigurationString() {
        Map<String, ?> map = PreferenceManager.getDefaultSharedPreferences(
                getBaseContext()).getAll();
        String location = map.remove("swan_location").toString();
        String path = map.remove("valuepath").toString();
        HistoryReductionMode mode = HistoryReductionMode.parse(map.remove(
                "history_reduction_mode").toString());
        long timespan = Long.parseLong(map.remove("history_window").toString());
        String timeUnits = (String) map.remove("time_units");
        if (timeUnits.equals("h")) {
            timespan = timespan * HOUR;
        } else if (timeUnits.equals("m")) {
            timespan = timespan * MINUTE;
        } else if (timeUnits.equals("s")) {
            timespan = timespan * SECOND;
        }
//		String storage = map.remove("storage").toString();
//		Log.d(TAG, "Selected storage: " + storage);
//		Log.d(TAG, storage);
//		setSelectedStorage(storage);
        Bundle httpConfiguration = new Bundle();

        if (map.containsKey("server_storage")) {
            httpConfiguration.putString("server_storage", map.remove("server_storage").toString());
        }
        if (map.containsKey("server_use_location")) {
            httpConfiguration.putBoolean("server_use_location", (Boolean) map.remove("server_use_location"));
        }
        if (map.containsKey("server_url")) {
            httpConfiguration.putString("server_url", map.remove("server_url").toString());
        }
        if (map.containsKey("server_http_method")) {
            httpConfiguration.putString("server_http_method", map.remove("server_http_method").toString());
        }
        if (map.containsKey("server_http_authorization")) {
            httpConfiguration.putString("server_http_authorization", map.remove("server_http_authorization").toString());
        }
        if (map.containsKey("server_http_header")) {
            httpConfiguration.putSerializable("server_http_header", map.remove("server_http_header").toString());
        }
        if (map.containsKey("server_http_body")) {
            httpConfiguration.putString("server_http_body", map.remove("server_http_body").toString());
        }
        if (map.containsKey("server_http_body_type")) {
            httpConfiguration.putString("server_http_body_type", map.remove("server_http_body_type").toString());
        }

        String entityId = getIntent().getStringExtra("entityId");

        Bundle configuration = new Bundle();
        for (String key : keys) {
            if (map.containsKey(key)) {
                configuration.putString(key, map.get(key).toString());
            }
        }

        for (String key : httpConfiguration.keySet()) {
            String value = "" + httpConfiguration.get(key);
            Log.e(TAG, "httpConfiguration key :" + key + " value :" + value);

        }

        SensorValueExpression sensor = new SensorValueExpression(location,
                entityId, path, configuration, mode, timespan, httpConfiguration);

        return sensor.toParseString();
    }

    @Override
    public final boolean onPreferenceChange(final Preference preference,
                                            final Object newValue) {
        if (preference instanceof ListPreference) {
            for (int i = 0; i < ((ListPreference) preference).getEntryValues().length; i++) {
                if (((ListPreference) preference).getEntryValues()[i]
                        .toString().equals(newValue.toString())) {
                    preference.setSummary(((ListPreference) preference)
                            .getEntries()[i]);
                    return true;
                }
            }

        } else {
            preference.setSummary(newValue.toString());
        }
        return true;
    }

}
