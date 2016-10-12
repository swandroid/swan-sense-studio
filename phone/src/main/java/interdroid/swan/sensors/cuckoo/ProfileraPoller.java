package interdroid.swan.sensors.cuckoo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.cuckoo_sensors.CuckooPoller;

public class ProfileraPoller implements CuckooPoller {
        public static final String VALUE = "value";
        public static final String CASE = "case";
        public static final String DELAY = "delay";

        private int[] intArray = new int[4];
        private static int i = 0;
        private int caseScenario = 0;
        //private static final String BASE_URL = "http://gps.buienradar.nl/getrr.php?lat=52.3&lon=4.87";
        private static final String BASE_URL = "https://thingspeak.com/channels/45572/field/3.json/";

        public ProfileraPoller() {
            intArray[0] = 0;
            intArray[1] = 1;
            intArray[2] = 2;
            intArray[3] = 3;
        }

        @Override
        public Map<String, Object> poll(String s, Map<String, Object> map) {
            Map<String, Object> result = new HashMap<>();

            if (map.containsKey(CASE)) {
                caseScenario = (Integer) map.get(CASE);
            }


            try {
                String line;
                URLConnection conn = new URL(BASE_URL).openConnection();
                BufferedReader r = new BufferedReader(new InputStreamReader(
                        conn.getInputStream()));
                // String line = r.readLine();
                String jsonData = "";
                while ((line = r.readLine()) != null) {
                    jsonData += line + "\n";
                }


                JSONObject jsonObject = new JSONObject(jsonData);
                Object result1 = null;
                int length = 0;

                length = jsonObject.getJSONArray("feeds").length();
                result1 = jsonObject.getJSONArray("feeds").getJSONObject(length - 1).get("field3");

            } catch (MalformedURLException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e){
//                e.printStackTrace();
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
            if (map != null && map.containsKey(DELAY))
                return (long) map.get(DELAY);
            return 1000;
        }
    }