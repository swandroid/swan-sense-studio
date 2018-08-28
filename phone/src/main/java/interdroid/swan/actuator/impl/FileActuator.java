package interdroid.swan.actuator.impl;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import interdroid.swan.actuator.Actuator;
import interdroid.swan.actuator.ui.AbstractActuatorActivity;
import interdroid.swancore.swansong.SensorValueExpression;
import interdroid.swancore.swansong.TimestampedValue;

/**
 * An {@link Actuator} the writes the sensor values to a file. Only works with a
 * {@link SensorValueExpression}. The file and any parent directories will be automatically
 * created. The values will be appended to the end of the file, meaning this actuator will
 * corrupt any non-text files.
 */
public class FileActuator extends Actuator {

    private static final String TAG = FileActuator.class.getSimpleName();

    public static final String ENTITY = "file";

    private static final String[] KEYS = new String[]{"file"};

    private static final String[] PATHS = new String[]{"write"};

    private final File file;

    /**
     * Create a {@link FileActuator} object.
     *
     * @param fileName the full absolute path to the file
     */
    private FileActuator(String fileName) {
        this.file = new File(fileName);

        if (!file.getParentFile().mkdirs()) {
            Log.e(TAG, "Directory could not be created");
        }
    }

    @Override
    public void performAction(Context context, TimestampedValue[] newValues) throws IOException {
        TimestampedValue latest = (newValues != null && newValues.length > 0) ? newValues[0] : null;

        if (latest != null) {
            if (!file.exists()) {
                if (!file.createNewFile()) {
                    Log.w(TAG, "Failed to create file");
                }
            }

            try (FileWriter fw = new FileWriter(file, true)) {
                fw.write(latest.toString());
                fw.write('\n');
                fw.flush();
            } catch (IOException e) {
                Log.w(TAG, "Failed to write to file", e);
            }
        }
    }

    public static class Factory implements Actuator.Factory {
        @Override
        public Actuator create(Context context, SensorValueExpression expression) {
            String file = expression.getConfiguration().getString("file");
            return new FileActuator(file);
        }
    }

    // TODO: 2018-07-11 file chooser activity?
    public static class ConfigActivity extends AbstractActuatorActivity {

        @Override
        protected String[] getParameterKeys() {
            return KEYS;
        }

        @Override
        protected String[] getParameterDefaultValues() {
            return new String[]{Environment.getExternalStorageDirectory().getPath()};
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
