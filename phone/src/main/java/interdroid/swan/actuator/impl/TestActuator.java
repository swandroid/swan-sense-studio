package interdroid.swan.actuator.impl;

import android.content.Context;
import android.util.Log;

import interdroid.swan.actuator.Actuator;
import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swan.sensors.impl.TestSensor;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

import com.google.firebase.iid.FirebaseInstanceId;

/**
 * Simple {@link Actuator} that prints out a log message.
 */
public class TestActuator extends Actuator {

    private static final String TAG = TestActuator.class.getSimpleName();

    public static final String ENTITY = "test";

    private static final String[] KEYS = new String[]{ "information"};

    private static final String[] PATHS = new String[]{"data"};



    @Override
    public void performAction(Context context, TimestampedValue[] newValues) {
       //TODO: do nothing
        Log.d(TAG,"Received actuation");
        Log.d(TAG,"Firebase token:"+FirebaseInstanceId.getInstance().getToken());
        ;// //Log.d(TAG, "Process:"+android.os.Process.myPid()+",Thread:"+Thread.currentThread().getId()+",Roshan Actuator perform time:"+ System.currentTimeMillis());
        //++TestSensor.testCounter;



    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            return new TestActuator();
        }
    }

    public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[]{
                    "info"
            };
        }

        @Override
        protected String[] getPaths() {
            return PATHS;
        }

        @Override
        protected String getEntity() {
            return ENTITY;
        }
    }
}
