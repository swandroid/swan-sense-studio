package nl.sense_os.service.ctrl;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import nl.sense_os.service.DataTransmitter;
import nl.sense_os.service.constants.SensePrefs;
import nl.sense_os.service.constants.SensePrefs.Main;

/**
 * A singleton that includes all the intelligent methods of the sense-android-library that define
 * the function of specific sensors. The two different extensions, Default and Extended, implement
 * the default and the energy-saving behavior respectively.
 */
public class Controller {

    private class Intervals {
        static final long ECO = AlarmManager.INTERVAL_HALF_HOUR;
        static final long RARELY = 1000 * 60 * 15;
        static final long NORMAL = 1000 * 60 * 5;
        static final long OFTEN = 1000 * 60 * 1;
    }

    private static final String TAG = "Controller";
    private static Controller sInstance;

    /**
     * Returns a controller instance
     *
     * @param context Context of the Sense service
     * @return the existed controller if any, otherwise the one which has just been created
     */
    public static synchronized Controller getController(Context context) {
        if (sInstance == null) {
            sInstance = new Controller(context);
        }
        return sInstance;
    }

    private Context mContext;

    protected Controller(Context context) {
        mContext = context;
    }

    /**
     * Starts periodic transmission of the buffered sensor data.
     */
    public void scheduleTransmissions() {
        SharedPreferences mainPrefs = mContext.getSharedPreferences(SensePrefs.MAIN_PREFS,
                Context.MODE_PRIVATE);
        int syncRate = Integer.parseInt(mainPrefs.getString(Main.SYNC_RATE, "0"));
        int sampleRate = Integer.parseInt(mainPrefs.getString(Main.SAMPLE_RATE, "0"));

        // pick transmission interval
        long txInterval;
        switch (syncRate) {
            case 2: // rarely, every 15 minutes
                txInterval = Intervals.RARELY;
                break;
            case 1: // eco-mode
                txInterval = Intervals.ECO;
                break;
            case 0: // 5 minute
                txInterval = Intervals.NORMAL;
                break;
            case -1: // 60 seconds
                txInterval = Intervals.OFTEN;
                break;
            case -2: // real-time: schedule transmission based on sample time
                switch (sampleRate) {
                    case 1: // rarely
                        txInterval = Intervals.ECO * 3;
                        break;
                    case 0: // normal
                        txInterval = Intervals.NORMAL * 3;
                        break;
                    case -1: // often
                        txInterval = Intervals.OFTEN * 3;
                        break;
                    case -2: // real time
                        txInterval = Intervals.OFTEN;
                        break;
                    default:
                        Log.w(TAG, "Unexpected sample rate value: " + sampleRate);
                        return;
                }
                break;
            default:
                Log.w(TAG, "Unexpected sync rate value: " + syncRate);
                return;
        }

        // pick transmitter task interval
        long txTaskInterval;
        switch (sampleRate) {
            case -2: // real time
                txTaskInterval = 0;
                break;
            case -1: // often
                txTaskInterval = 10 * 1000;
                break;
            case 0: // normal
                txTaskInterval = 60 * 1000;
                break;
            case 1: // rarely (15 minutes)
                txTaskInterval = 15 * 60 * 1000;
                break;
            default:
                Log.w(TAG, "Unexpected sample rate value: " + sampleRate);
                return;
        }

        DataTransmitter transmitter = DataTransmitter.getInstance(mContext);
        transmitter.startTransmissions(txInterval, txTaskInterval);
    }
}
