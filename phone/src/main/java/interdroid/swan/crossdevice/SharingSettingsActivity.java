package interdroid.swan.crossdevice;

import android.app.Activity;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;

import interdroid.swan.R;
import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swancore.swanmain.SensorInfo;

public class SharingSettingsActivity extends Activity {

    public static class SharingSettingsFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.sharing_settings_preferences);
            PreferenceScreen preferenceScreen = this.getPreferenceScreen();

            // initialize preferences
            for(SensorInfo sensor : ExpressionManager.getSensors(preferenceScreen.getContext())) {
                CheckBoxPreference preference = new CheckBoxPreference(preferenceScreen.getContext());
                preference.setKey("sharing." + sensor.getEntity());
                preference.setTitle(sensor.getEntity());
                preference.setSummary("Enable/disable remote sharing for " + sensor.getEntity());
                preferenceScreen.addPreference(preference);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SharingSettingsFragment())
                .commit();
    }
}
