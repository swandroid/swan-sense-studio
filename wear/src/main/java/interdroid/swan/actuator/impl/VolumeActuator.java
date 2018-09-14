package interdroid.swan.actuator.impl;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;

import interdroid.swan.actuator.Actuator;
//import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * An {@link Actuator} that controls the volume of the device.
 */
public class VolumeActuator extends Actuator {

    public static final String ENTITY = "volume";

    private static final String PATH_SET = "set";
    private static final String PATH_RAISE = "raise";
    private static final String PATH_LOWER = "lower";

    private static final String[] KEYS = new String[]{"stream", "volume"};

    private static final String[] PATHS = new String[]{"set", "raise", "lower"};

    private final AudioManager audioManager;

    private final String path;

    private final int stream;

    private final Integer volume;

    /**
     * Create a {@link VolumeActuator} object.
     *
     * @param context the context
     * @param path    the value path of the {@link SensorValueExpression}
     * @param stream  the audio channel, only works with the 'set' path
     * @param volume  the volume to set, only works with the 'set' path
     */
    private VolumeActuator(Context context, String path, Integer stream, Integer volume) {
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        this.path = path;
        this.stream = stream == null ? AudioManager.STREAM_MUSIC : stream;
        this.volume = volume;
    }

    @Override
    public void performAction(Context context, String expressionId, TimestampedValue[] newValues) {
        switch (path) {
            case PATH_SET:
                audioManager.setStreamVolume(stream, volume, 0);
                break;
            case PATH_RAISE:
                audioManager.adjustVolume(AudioManager.ADJUST_RAISE, 0);
                break;
            case PATH_LOWER:
                audioManager.adjustVolume(AudioManager.ADJUST_LOWER, 0);
                break;
        }
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            String path = expression.getValuePath();
            Bundle config = expression.getConfiguration();

            Integer stream = null;
            if (config.containsKey("stream")) {
                stream = Integer.parseInt(config.getString("stream"));
            }

            Integer volume = null;
            if (config.containsKey("volume")) {
                volume = Integer.parseInt(config.getString("volume"));
            }

            return new VolumeActuator(context, path, stream, volume);
        }
    }

/*    public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[]{"3", "50"};
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
