package interdroid.swan.crossdevice.ble;

/**
 * Created by vladzy on 7/9/2016.
 */
public class BLELogRecord {

    public long startTime;
    public long startTimeApp;
    public long reqDuration;
    public long connTime;
    public long discoveryTime;
    public int sensors;
    public boolean failed;

    public BLELogRecord(long startTime, long startTimeApp, long reqDuration, long connTime, long discoveryTime, int sensors, boolean failed) {
        this.startTime = startTime;
        this.startTimeApp = startTimeApp;
        this.reqDuration = reqDuration;
        this.connTime = connTime;
        this.discoveryTime = discoveryTime;
        this.failed = failed;
        this.sensors = sensors;
    }

    public long getSetupDuration() {
        return discoveryTime - startTime;
    }

    public static String printHeader() {
        return "Time\tReq_Duration\tConn_Duration\tDiscovery_Duration\tSetup_Duration\tSensors\tStatus";
    }

    /* if you change this, make the corresponding change in printHeader as well */
    @Override
    public String toString() {
        return (startTime - startTimeApp) + "\t" + reqDuration
                + "\t" + (connTime - startTime) + "\t" + (discoveryTime - connTime)
                + "\t" + (discoveryTime - startTime) + "\t" + sensors + "\t" + (failed ? "fail" : "success");
    }
}
