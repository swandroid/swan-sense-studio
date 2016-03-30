package interdroid.swan.sensors.impl;

/**
 * Created by Roshan Bharath Das on 11/11/15.
 */

import android.os.Bundle;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;

/**
 * Created by Roshan Bharath Das on 05/11/15.
 */

        //entity id = server
        //valuepath url = "http://fs0.das4.cs.vu.nl:8090"
        //configuration type = "pull"/"push"
        //configuration content_url = "http://ovapi.nl"
        //configuration content


public class ServerSensor1 extends AbstractSwanSensor {



    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public int getPreferencesXML() {
            return R.xml.server_preferences;
        }

    }



    public static final String SAMPLE_INTERVAL = "sample_interval";
    public static final long DEFAULT_SAMPLE_INTERVAL = 5 * 60 * 1000;

    public  static final String MY_SERVER_ADDRESS = "http://fs0.das5.cs.vu.nl:3000/";

    public static final String URL = "url";

    public static final String VALUE = "value";


    private Map<String, ServerPoller> activeThreads = new HashMap<String, ServerPoller>();


    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putLong(SAMPLE_INTERVAL, DEFAULT_SAMPLE_INTERVAL);

    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration){// throws IOException {


        ServerPoller serverPoller = new ServerPoller(id, valuePath,
                configuration);
        activeThreads.put(id, serverPoller);
        serverPoller.start();

    }


    @Override
    public void unregister(String id) {

        activeThreads.remove(id).interrupt();

    }


    class ServerPoller extends Thread {

        private Bundle configuration;
        private String valuePath;
        private String id;
        InputStream response;

        ServerPoller(String id, String valuePath, Bundle configuration) {
            this.id = id;
            this.configuration = configuration;
            this.valuePath = valuePath;
        }

        public void run() {
            while (!isInterrupted()) {
                long start = System.currentTimeMillis();

                //String url = valuePath;
                String url= configuration.getString("url");
                String apiKey= configuration.getString("api_key");
                String requestType = configuration.getString("type");
                String sensorValupath = configuration.getString("sensor");


                    if(requestType.equalsIgnoreCase("post")){


                        String content = "name="+sensorValupath;
                        response = HttpConnection.postRequest(url, content);
                        //Log.e("Roshan",response.toString());

                    }

                    else if(requestType.equalsIgnoreCase("get")){
                        Log.e("Roshan",url);
                        response = HttpConnection.getRequest(url);
                        //Log.e("Roshan",response.toString());
                    }

                    //putValueTrimSize(valuePath, id, start, content.toString());




                try {
                    Thread.sleep(DEFAULT_SAMPLE_INTERVAL);
                } catch (InterruptedException e) {

                }
            }
        }


    }




    public static class HttpConnection {


        public static InputStream getRequest(String stringURL) {


            URL url = null;
            InputStream in = null;

            try {
                url = new URL(stringURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            HttpURLConnection urlConnection = null;

            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                in = new BufferedInputStream(urlConnection.getInputStream());
                Log.e("Roshan",in.toString());
                readStream(in);


            } catch (IOException e) {
                e.printStackTrace();
            }

            return in;
        }


        private static String readStream(InputStream is) {
            try {
                ByteArrayOutputStream bo = new ByteArrayOutputStream();
                int i = is.read();
                while(i != -1) {
                    bo.write(i);
                    i = is.read();
                }
                return bo.toString();
            } catch (IOException e) {
                return "";
            }
        }

        private static void writeStream(OutputStream os, String data){

            try {
                os.write(data.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        public static InputStream postRequest(String stringURL, String content){

            URL url = null;

            try {
                url = new URL(stringURL);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }


            HttpURLConnection urlConnection = null;
            try {
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setChunkedStreamingMode(0);
                urlConnection.setRequestMethod("POST");

            } catch (IOException e) {
                e.printStackTrace();
            }


            OutputStream out = null;
            try {
                out = new BufferedOutputStream(urlConnection.getOutputStream());
                writeStream(out, content);
            } catch (IOException e) {
                e.printStackTrace();
            }


            InputStream in = null;
            try {
                in = new BufferedInputStream(urlConnection.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            readStream(in);


            return in;
        }




    }





    @Override
    public String[] getValuePaths() {
        return new String[] { URL };
    }

    @Override
    public void onConnected() {

    }







}



