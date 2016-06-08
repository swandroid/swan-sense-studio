package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import interdroid.swan.R;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * Sensor for phone state.
 *
 * @author nick &lt;palmer@cs.vu.nl&gt;
 */
public class CallSensor extends AbstractSwanSensor {
    private static final String TAG = "Call Sensor";

    /**
     * The configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.call_preferences;
        }
    }

    /**
     * The call state.
     */
    public static final String STATE_FIELD = "call_state";

    /**
     * The phone number associated with the state if any.
     */
    public static final String PHONE_NUMBER_FIELD = "phone_number";

    /**
     * The telephony manager we use.
     */
    private TelephonyManager telephonyManager;

    /**
     * The phone state listener which gets notified on call state changed.
     */
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {
        @Override
        public void onCallStateChanged(final int state, final String incomingNumber) {
            Log.d(TAG, "Call State: " + state + " " + incomingNumber);

            long now = System.currentTimeMillis();

            putValueTrimSize(STATE_FIELD, null, now, state);
            if (incomingNumber != null && incomingNumber.length() > 0) {
                putValueTrimSize(PHONE_NUMBER_FIELD, null, now, incomingNumber);
            }
        }
    };

    @Override
    public final String[] getValuePaths() {
        return new String[]{STATE_FIELD, PHONE_NUMBER_FIELD};
    }

    @Override
    public final void initDefaultConfiguration(final Bundle defaults) {
    }

    @Override
    public final void onConnected() {
        SENSOR_NAME = "Call Sensor";
    }

    @Override
    public final void register(final String id, final String valuePath,
                               final Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        if (registeredConfigurations.size() == 1) {
            telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    @Override
    public final void unregister(final String id) {
        if (registeredConfigurations.size() == 0) {
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_NONE);
        }
    }

    @Override
    public void onDestroySensor() {
        super.onDestroySensor();
    }

}
