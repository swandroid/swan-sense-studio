package interdroid.swan.sensors.cuckoo;

import java.util.HashMap;
import java.util.Map;

import interdroid.swan.cuckoo_sensors.CuckooPoller;

public class ProfilerPoller implements CuckooPoller {
    public static final String VALUE = "value";

    private int[] intArray = new int[4];
    private static int i = 0;
    private int caseScenario = 0;

    public ProfilerPoller() {
        intArray[0] = 0;
        intArray[1] = 1;
        intArray[2] = 2;
        intArray[3] = 3;
    }

    @Override
    public Map<String, Object> poll(String s, Map<String, Object> map) {
        Map<String, Object> result = new HashMap<>();

        if (map.containsKey("case")) {
            caseScenario = (Integer) map.get("case");
        }

        if (caseScenario == 0) {
            if (i == 0) {
                i = 2;
            } else {
                i = 0;
            }
        } else {
            if (i == 0) {
                i = 1;
            } else {
                i = 0;
            }

        }

        result.put(VALUE, i);
        return result;
    }

    @Override
    public long getInterval(Map<String, Object> map, boolean b) {
        return Long.valueOf(map.get("delay").toString());
    }
}
