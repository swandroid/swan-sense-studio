package interdroid.swan.sensors.impl;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.okhttp.OkHttpClient;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Client;
import retrofit.client.OkClient;
import retrofit.client.Response;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.mime.TypedInput;

/**
 * Created by Roshan Bharath Das on 27/11/15.
 */
public class ServerConnection {

    Gson gson;
    RestAdapter eventResultsAdapter;
    private ServerCallbackInterface serverCallbackInterface;
    String serverStorage;
    String serverUrl;
    String serverHttpMethod;
    String serverHttpAuthorization;
    String serverHttpHeader;
    String serverHttpBody;
    String serverHttpBodyType;
    HashMap<String, String> httpHeaders =  new HashMap<String,String>();
    HashMap<String, Object> httpBody =  new HashMap<String,Object>();
    GenericAPIInterface service;
    boolean bodyDataExist = true;

    public interface GenericAPIInterface {

        @GET("/")
        void getData(Callback<Object> cb);

        //@POST("/")
        //void postData(@Body TypedInput body);

        @POST("/")
        void postData(@Body String body, Callback<Object> cb);


        @POST("/")
        void postJSON(@Body HashMap<String, Object> body, Callback<Object> cb);


        @FormUrlEncoded
        @POST("/")
        void postFormData(
                @FieldMap Map<String, Object> formParams, Callback<Object> cb
        );

        @PUT("/")
        void putJSON(@Body HashMap<String, Object> body, Callback<Object> cb);

        @FormUrlEncoded
        @PUT("/")
        void putFormData(
                @FieldMap Map<String, Object> formParams, Callback<Object> cb
        );

        @PUT("/")
        void putData(@Body String body, Callback<Object> cb);


    }

    public class MyRetrofitInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestFacade req) {

            Iterator it = httpHeaders.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry)it.next();
                req.addHeader(pair.getKey().toString(),pair.getValue().toString());
                it.remove(); // avoids a ConcurrentModificationException

            }

            // TODO: Authorization
            // req.addHeader("Authorization", string);

        }
    }

    public ServerConnection(Bundle httpConfig){


        serverUrl = httpConfig.getString("server_url");
        serverHttpMethod = httpConfig.getString("server_http_method");
        serverHttpBodyType = httpConfig.getString("server_http_body_type");

        setHeaderBasedOnBodyType(serverHttpBodyType);

        serverHttpHeader = httpConfig.getString("server_http_header");

        //value inputed as key1:value1,key2:value2
        parseAndSetHeader(serverHttpHeader);

        serverHttpBody = httpConfig.getString("server_http_body");

        parseAndSetBody(serverHttpBody);

        gson = new GsonBuilder().create();

        eventResultsAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(new MyRetrofitInterceptor())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .setEndpoint(serverUrl)
                .build();

        service = eventResultsAdapter.create(GenericAPIInterface.class);

        //if(!serverHttpBody.equals("null") && !serverHttpMethod.equals("GET")){
        //    bodyDataExist = false;
       // }


        Log.e("Roshan"," serverUrl "+serverUrl+" serverHttpMethod "+serverHttpMethod);

    }


    public GenericAPIInterface getService() {

        return this.service;

    }


    public void useHttpMethod(HashMap<String,Object> hashData, Callback<Object> cb){


        hashData.putAll(httpBody);

        for (String key : hashData.keySet()) {
            Log.e("Roshan","final hashData "+key+":"+hashData.get(key));
        }


        if(serverHttpBodyType.equals("formdata")){

            if(serverHttpMethod.equals("POST")) {
                service.postFormData(hashData,cb);
            }
            else if(serverHttpMethod.equals("PUT")){

                service.putFormData(hashData, cb);
            }

        }

        else if(serverHttpBodyType.equals("application/json")){

            if(serverHttpMethod.equals("POST")) {
                service.postJSON(hashData,cb);
            }
            else if(serverHttpMethod.equals("PUT")){
                service.putJSON(hashData,cb);
            }

        }
        //TODO : add fuctionality for xml
        //else if(serverHttpBodyType.equals("application/xml")){

        //}




    }


    public void useHttpMethod(String body, Callback<Object> cb){

            if(serverHttpMethod.equals("POST")) {
                service.postData(body,cb);
            }
            else if(serverHttpMethod.equals("PUT")){
                service.putData(body,cb);
            }
   }



    public void parseAndSetHeader(String rawData){

        if(!rawData.equals("null")) {
            String[] pairs = rawData.split(",");
            for(String pair : pairs){
                String[] data = pair.split(":");
                httpHeaders.put(data[0],data[1]);
            }

        }

    }


    public void parseAndSetBody(String rawData){

        if(!rawData.equals("null")) {
            String[] pairs = rawData.split(",");
            for(String pair : pairs){
                String[] data = pair.split(":");
                httpBody.put(data[0],data[1]);
            }

        }

    }

    public void setHeaderBasedOnBodyType(String bodyType){

        if(bodyType.equals("text/plain")){
            httpHeaders.put("Content-Type","text/plain");
        }
        else if(bodyType.equals("application/json")){
            httpHeaders.put("Content-Type","application/json");
        }
        else if(bodyType.equals("application/xml")){
            httpHeaders.put("Content-Type","application/xml");
        }

    }



}
