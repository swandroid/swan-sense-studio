package interdroid.swan.sensors.impl;

import android.os.Bundle;

import interdroid.swancore.swanmain.ExpressionManager;
import interdroid.swan.R;
import interdroid.swancore.swanmain.SwanException;
import interdroid.swancore.swanmain.ValueExpressionListener;
import interdroid.swancore.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;
import interdroid.swancore.swansong.Expression;
import interdroid.swancore.swansong.HistoryReductionMode;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

public class IntentSensor extends AbstractSwanSensor {

    public static final String TAG = "Intent";

    /**
     * The configuration activity for this sensor.
     *
     * @author nick &lt;palmer@cs.vu.nl&gt;
     */
    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setResult(
                    RESULT_OK,
                    getIntent().putExtra("configuration",
                            IntentSensor.STARTED_FIELD));
            finish();
        }

        @Override
        public final int getPreferencesXML() {
            return R.xml.intent_preferences;
        }

    }

    public static final String STARTED_FIELD = "started";

    protected static final int HISTORY_SIZE = 300;

    private static final String MAGIC_RELAY = "INTENTS_FROM_LOGCAT";

    @Override
    public String[] getValuePaths() {
        return new String[]{STARTED_FIELD};
    }

    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
    }

    @Override
    public void onConnected() {
        SENSOR_NAME = "Intent Sensor";
    }

    @Override
    public void register(final String id, final String valuePath,
                         final Bundle configuration, final Bundle httpConfiguration, Bundle extraConfiguration) {
        configuration.putString("logcat_parameters", "'ActivityManager:I'");
        try {
            ExpressionManager.registerValueExpression(this, id + "."
                            + MAGIC_RELAY, new SensorValueExpression(
                            Expression.LOCATION_SELF, "logcat", "log", configuration,
                            HistoryReductionMode.ANY, 0, httpConfiguration),
                    new ValueExpressionListener() {

                        @Override
                        public void onNewValues(String id,
                                                TimestampedValue[] newValues) {
                            for (TimestampedValue value : newValues) {
                                // if (value.getValue().toString()
                                // .contains("Starting: Intent {")) {
                                if (value.getValue().toString()
                                        .startsWith("START")) {
                                    putValueTrimSize(valuePath, id,
                                            value.getTimestamp(),
                                            getIntentFrom(value.getValue()));
                                }
                            }
                        }
                    });
        } catch (SwanException e) {
            e.printStackTrace();
        }
    }

    private String getIntentFrom(final Object value) {
        String string = value.toString();
        string = string.substring(string.indexOf("cmp=") + 4);
        string = string.substring(0, string.indexOf(" "));
        System.out.println("got intent: " + string);
        return string;
    }

    @Override
    public void unregister(final String id) {
        ExpressionManager.unregisterExpression(this, id + "." + MAGIC_RELAY);
    }

    @Override
    public void onDestroySensor() {
        super.onDestroySensor();
    }
}
