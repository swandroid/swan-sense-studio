package interdroid.swan.actuator.impl;

import android.content.Context;
import android.os.Vibrator;
import android.util.Log;

import interdroid.swan.DeviceClient;
import interdroid.swan.actuator.Actuator;
import interdroid.swancore.swansong.Result;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;
import interdroid.swancore.swansong.TriState;

//import interdroid.swan.actuator.ui.AbstractActuatorActivity;

/**
 * The actuator that vibrates the phone.
 */
public class PhoneActuator extends Actuator {

    public static final String ENTITY = "phone";

    private static final String[] KEYS = new String[]{"bluetooth"};

    private static final String[] PATHS = new String[]{"send"};



    @Override
    public void performAction(Context context, String expressionId, TimestampedValue[] newValues) {

        if(newValues!=null) {
            Log.d("RoshanPhone","Sending new values to phone");
            DeviceClient.getInstance(context).sendExpressionData(expressionId, new Result(newValues,
                    newValues[newValues.length - 1].getTimestamp()));
        }else{
            Log.d("RoshanPhone","Sending new state to phone");
            TriState state = TriState.TRUE;
            Result result = new Result(System.currentTimeMillis(), state);
            result.setDeferUntilGuaranteed(false);
            DeviceClient.getInstance(context).sendExpressionData(expressionId, result);
        }

    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            return new PhoneActuator();
        }
    }

  /*  public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[]{"500"};
        }

        @Override
        protected String[] getPaths() {
            return PATHS;
        }

        @Override
        protected String getEntity() {
            return ENTITY;
        }
    } */
}
