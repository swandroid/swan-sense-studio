package interdroid.swan.sensors.impl;

/**
 * Created by Roshan Bharath Das on 11/11/15.
 */

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;


import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import interdroid.swan.R;
import interdroid.swan.sensors.AbstractConfigurationActivity;
import interdroid.swan.sensors.AbstractSwanSensor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;

/**
 * Created by Roshan Bharath Das on 05/11/15.
 */

        //entity id = server
        //valuepath url = "http://fs0.das4.cs.vu.nl:8090"
        //configuration type = "pull"/"push"
        //configuration content_url = "http://ovapi.nl"
        //configuration content


public class ServerSensor extends AbstractSwanSensor {



    public static class ConfigurationActivity extends
            AbstractConfigurationActivity {

        @Override
        public int getPreferencesXML() {
            return R.xml.server_preferences;
        }

    }



    public static final String SAMPLE_INTERVAL = "sample_interval";
    public static final long DEFAULT_SAMPLE_INTERVAL = 5 * 60 * 1000;

    public  static final String MY_SERVER_ADDRESS = "https://api.thingspeak.com/update?api_key=VA1FPOVBRSVBF8LV&field1=0";

    public static final String URL = "url";

    public static final String VALUE = "value";


    private Map<String, ServerPoller> activeThreads = new HashMap<String, ServerPoller>();


    @Override
    public void initDefaultConfiguration(Bundle DEFAULT_CONFIGURATION) {
        DEFAULT_CONFIGURATION.putLong(SAMPLE_INTERVAL, DEFAULT_SAMPLE_INTERVAL);

    }

    @Override
    public void register(String id, String valuePath, Bundle configuration, final Bundle httpConfiguration) {//throws IOException {


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


                //String url= configuration.getString("url");
                String url = MY_SERVER_ADDRESS;
                String apiKey= configuration.getString("api_key");
                String requestType = configuration.getString("type");
                String sensorValupath = configuration.getString("sensor");


                Gson gson = new GsonBuilder().create();

                RestAdapter eventResultsAdapter = new RestAdapter.Builder()
                        .setLogLevel(RestAdapter.LogLevel.FULL)
                        .setConverter(new GsonConverter(gson))
                        .setEndpoint(url)
                        .build();


                    if(requestType.equalsIgnoreCase("post")){


                        String content = "name="+sensorValupath;


                        eventResultsAdapter.create(ServerAPI.class).postInfo(content, new Callback<String>(){
                            @Override
                            public void success(String result, Response response) {

                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println(error);
                            }
                        });


                    }

                    else if(requestType.equalsIgnoreCase("get")){
                        Log.e("Roshan",url);

                        eventResultsAdapter.create(ServerAPI.class).getInfo(new Callback<String>(){
                            @Override
                            public void success(String result, Response response) {
                                Log.e("Roshan", result);
                            }

                            @Override
                            public void failure(RetrofitError error) {
                                System.out.println(error);
                            }
                        });
                    }

                    //putValueTrimSize(valuePath, id, start, content.toString());




                try {
                    Thread.sleep(DEFAULT_SAMPLE_INTERVAL);
                } catch (InterruptedException e) {

                }
            }
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



