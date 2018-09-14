package interdroid.swan.actuator.impl;

import android.content.Context;
import android.telephony.SmsManager;

import interdroid.swan.actuator.Actuator;
//import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * An {@link Actuator} sends an SMS message to a number.
 */
public class SmsActuator extends Actuator {

    public static final String ENTITY = "sms";

    private static final String[] KEYS = new String[]{"number", "message"};

    private static final String[] PATHS = new String[]{"send"};

    private final String phoneNumber;

    private final String text;

    /**
     * Create a {@link SmsActuator} object.
     *
     * @param phoneNumber the number to send the text to.
     * @param text        the message to send
     */
    private SmsActuator(String phoneNumber, String text) {
        this.phoneNumber = phoneNumber;
        this.text = text;
    }

    @Override
    public void performAction(Context context, TimestampedValue[] newValues) {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNumber, null, text, null, null);
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            String phoneNumber = expression.getConfiguration().getString("number");
            String text = expression.getConfiguration().getString("message");
            return new SmsActuator(phoneNumber, text);
        }
    }

  /*  public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[KEYS.length];
        }

        @Override
        protected String[] getPaths() {
            return PATHS;
        }

        @Override
        protected String getEntity() {
            return ENTITY;
        }
    }*/
}
