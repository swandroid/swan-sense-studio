package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;

import interdroid.swan.R;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

public class SignalStrengthSensor extends AbstractSwanSensor {
    private static final String TAG = "SignalStrengthSensor";

    /**
     * The configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public final int getPreferencesXML() {
            return R.xml.gsm_preferences;
        }

    }

    /**
     * The gsm signal strength field.
     */
    public static final String GSM_SIGNAL_STRENGTH_FIELD = "gsm_signal_strength";

    /**
     * Is this a gsm or cdma reading?
     */
    public static final String IS_GSM_FIELD = "is_gsm";

    /**
     * The gsm bit error rate field.
     */
    public static final String GSM_BIT_ERROR_RATE_FIELD = "gsm_bit_error_rate";

    /**
     * The cdma dbm field.
     */
    public static final String CDMA_DBM_FIELD = "cdma_dbm";

    /**
     * The cdma EC/IO value in dB*10
     */
    public static final String CDMA_ECIO_FIELD = "cdma_ecio";

    /**
     * The evdo dbm field.
     */
    public static final String EVDO_DBM_FIELD = "evdo_dbm";

    /**
     * The evdo EC/IO value in dB*10
     */
    public static final String EVDO_ECIO_FIELD = "evdo_ecio";

    /**
     * The evdo signal to noise field.
     */
    public static final String EVDO_SNR_FIELD = "evdo_snr";

    /**
     * The telephony manager.
     */
    private TelephonyManager telephonyManager;

    /**
     * The phone state listener we use.
     */
    private PhoneStateListener phoneStateListener = new PhoneStateListener() {

        @Override
        public void onSignalStrengthsChanged(SignalStrength signalStrength) {
            long now = System.currentTimeMillis();
            if (signalStrength.isGsm()) {
                Log.d(TAG,
                        "GSM Signal Strength: "
                                + signalStrength.getGsmSignalStrength() + ", "
                                + signalStrength.getGsmBitErrorRate());
            } else {
                Log.d(TAG,
                        "CDMA Signal Strength: " + signalStrength.getCdmaDbm()
                                + ", " + signalStrength.getCdmaEcio());
            }

            putValueTrimSize(IS_GSM_FIELD, null, now, signalStrength.isGsm());
            putValueTrimSize(GSM_SIGNAL_STRENGTH_FIELD, null, now, signalStrength.getGsmSignalStrength());
            putValueTrimSize(GSM_BIT_ERROR_RATE_FIELD, null, now, signalStrength.getGsmBitErrorRate());
            putValueTrimSize(CDMA_DBM_FIELD, null, now, signalStrength.getCdmaDbm());
            putValueTrimSize(CDMA_ECIO_FIELD, null, now, signalStrength.getCdmaEcio());
            putValueTrimSize(EVDO_DBM_FIELD, null, now, signalStrength.getEvdoDbm());
            putValueTrimSize(EVDO_ECIO_FIELD, null, now, signalStrength.getEvdoEcio());
            putValueTrimSize(EVDO_SNR_FIELD, null, now, signalStrength.getEvdoSnr());
        }
    };

    @Override
    public final String[] getValuePaths() {
        return new String[]{IS_GSM_FIELD, GSM_SIGNAL_STRENGTH_FIELD,
                GSM_BIT_ERROR_RATE_FIELD, CDMA_DBM_FIELD, CDMA_ECIO_FIELD,
                EVDO_DBM_FIELD, EVDO_ECIO_FIELD, EVDO_SNR_FIELD};
    }

    @Override
    public final void initDefaultConfiguration(Bundle defaults) {
    }

    @Override
    public final void onConnected() {
        SENSOR_NAME = "Signal Strength Sensor";
    }

    @Override
    public final void register(final String id, final String valuePath,
                               final Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        super.register(id, valuePath, configuration, httpConfiguration, extraConfiguration);
        if (registeredConfigurations.size() == 1) {
            telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
            telephonyManager.listen(phoneStateListener,
                    PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
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
    public final void onDestroySensor() {
        super.onDestroySensor();
    }

}
