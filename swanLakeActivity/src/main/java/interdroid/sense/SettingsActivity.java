package interdroid.sense;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.view.KeyEvent;

import interdroid.swan.R;
import nl.sense_os.platform.SensePlatform;

public class SettingsActivity extends PreferenceActivity implements OnSharedPreferenceChangeListener {

    public static String TAG = "SettingsActivity";

    // Sense specific members
    private SensePlatform mSensePlatform;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Loads the XML preferences file
        addPreferencesFromResource(R.xml.sense_preferences);

        mSensePlatform = new SensePlatform(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Registers a listener whenever a key changes            
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Unregisters the listener set in onResume().
        // It's best practice to unregister listeners when your app isn't using them to cut down on
        // unnecessary system overhead. You do this in onPause().
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    // When the user changes the preferences selection, 
    // onSharedPreferenceChanged() restarts the main activity as a new
    // task. Sets the refreshDisplay flag to "true" to indicate that
    // the main activity should update its display.
    // The main activity queries the PreferenceManager to get the latest settings.

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK)
//			return true;
        mSensePlatform.close();
        return super.onKeyUp(keyCode, event);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        mSensePlatform.setPreferenceString(sharedPreferences, key);
    }
}
