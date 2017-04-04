package interdroid.swan.crossdevice.queueapp;

/**
 * Created by vladimir on 3/13/17.
 */

public class QueuyLogRecord {
    public long timestamp;
    public long estWaitingTime;

    public QueuyLogRecord(long timestamp, long estWaitingTime) {
        this.timestamp = timestamp;
        this.estWaitingTime = estWaitingTime;
    }

    public static String printHeader() {
        return "#Relative_Time\tTime\tEst_Waiting_Time\tReal_Waiting_Time";
    }

    public String print(long startTime, long stopTime) {
        return (timestamp - startTime) + "\t" + timestamp + "\t" + estWaitingTime + "\t" + (stopTime - timestamp);
    }
}
