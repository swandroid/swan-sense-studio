package nl.sense_os.service.commonsense;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import nl.sense_os.service.constants.SensePrefs;

/**
 * Service that checks if all the sensors on this phone are registered at CommonSense.<br/>
 * <br/>
 * TODO: Also registers the app for GCM notifications, but this should probably be moved somewhere
 * else to keep the code transparent.
 *
 * @author Steven Mulder <steven@sense-os.nl>
 * @see DefaultSensorRegistrator
 */
public class DefaultSensorRegistrationService extends IntentService {

    private static final String TAG = "DefaultSensorRegistrationService";
    private SensorRegistrator verifier = new DefaultSensorRegistrator(this);

    public DefaultSensorRegistrationService() {
        super("DefaultSensorRegistrationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences mainPrefs = getSharedPreferences(SensePrefs.MAIN_PREFS, MODE_PRIVATE);
        long lastVerified = mainPrefs.getLong(SensePrefs.Main.LAST_VERIFIED_SENSORS, 0);
        if (System.currentTimeMillis() - lastVerified < 1000l * 60) { // 1 minute
            // registered sensors were already recently checked
            Log.v(TAG, "Sensor IDs were just verified already");
            return;
        }

        String deviceType = SenseApi.getDefaultDeviceType(this);
        String deviceUuid = SenseApi.getDefaultDeviceUuid(this);

        if (verifier.verifySensorIds(deviceType, deviceUuid)) {
            Log.v(TAG, "Sensor IDs verified");
            mainPrefs.edit()
                    .putLong(SensePrefs.Main.LAST_VERIFIED_SENSORS, System.currentTimeMillis())
                    .commit();
        } else {
            // hopefully the IDs will be checked again
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
