package interdroid.swan.crossdevice.beacon;

import org.altbeacon.beacon.Beacon;

/**
 * Created by slavik on 4/20/16.
 */
public class BeaconUtils {

    public static boolean isEddystoneUID(Beacon beacon) {
        return (beacon.getServiceUuid() == 0xfeaa) && (beacon.getBeaconTypeCode() == 0);
    }

    public static boolean isEddystoneURL(Beacon beacon) {
        return (beacon.getServiceUuid() == 0xfeaa && beacon.getBeaconTypeCode() == 16);
    }

    public static boolean isAltBeacon(Beacon beacon) {
        return beacon.getServiceUuid() == 0xbeac;
    }

    public static boolean isAppleIBeacon(Beacon beacon) {
        return beacon.getBeaconTypeCode() == 533; //0215 in hex
    }

    public static boolean isEstimoteNearable(Beacon beacon) {
        return beacon.getBeaconTypeCode() == 0x0101;
    }

}
