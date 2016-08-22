package interdroid.swan.crossdevice.bluetooth;

/**
 * Created by vladzy on 7/9/2016.
 */
public class BTLogRecord {

    public long startTime;
    public long startSwanTime;
    public long startTimeApp;
    public long totalDuration;
    public long swanDuration; //time taken by swan to process the request
    public long connDuration;
    public int sensors;
    public boolean failed;
    public boolean client;

    public BTLogRecord(long startTimeApp, boolean client) {
        this.startTime = System.currentTimeMillis();
        this.startTimeApp = startTimeApp;
        this.client = client;
    }

    public static String printHeader() {
        return "Time\tWorker\tReq_Duration\tConn_Duration\tSwan_Duration\tComm_Duration\tSensors\tStatus";
    }

    /* if you change this, make the corresponding change in printHeader as well */
    @Override
    public String toString() {
        return (startTime - startTimeApp) + "\t" + (client ? "client" : "server")
                + "\t" + totalDuration + "\t" + connDuration
                + "\t" + swanDuration + "\t" + (totalDuration - swanDuration)
                + "\t" + sensors + "\t" + (failed ? "fail" : "success");
    }
}
