package interdroid.swan.remote.cloud;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import interdroid.swan.remote.Constant;
import interdroid.swan.remote.ServerConnection;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Roshan Bharath Das on 27/06/16.
 */
public class CloudCommunication {

    private static final String TAG = "CloudCommunication";

    ServerConnection serverConnection;

    static String DEFAULT_URL = "http://swan-cloud.herokuapp.com";

    static String SWAN_REGISTER = "/swan/register/";
    static String SWAN_UNREGISTER = "/swan/unregister/";

    Bundle httpConfiguration = new Bundle();


    public CloudCommunication() {

            //httpConfiguration.putString("server_url","http://swan-cloud.herokuapp.com/register-swan/");
            httpConfiguration.putString(Constant.SERVER_HTTP_METHOD,"POST");
            httpConfiguration.putString(Constant.SERVER_HTTP_AUTHORIZATION,Constant.NULL);
            httpConfiguration.putSerializable(Constant.SERVER_HTTP_HEADER,Constant.NULL);
            httpConfiguration.putString(Constant.SERVER_HTTP_BODY,Constant.NULL);
            httpConfiguration.putString(Constant.SERVER_HTTP_BODY_TYPE,"application/json");



    }

    protected Callback<Object> cb = new Callback<Object>() {
        @Override
        public void success(Object o, Response response) {

            Log.d(TAG,"Success:"+response.toString());
        }

        @Override
        public void failure(RetrofitError error) {

            Log.d(TAG,"Failure:"+error);

        }
    };


    void sendRegisterRequest(String id, String expression, String location) {

                if(location.contains("http")){
                    httpConfiguration.putString(Constant.SERVER_URL,location+SWAN_REGISTER);
                }
                else {

                    httpConfiguration.putString(Constant.SERVER_URL, DEFAULT_URL+SWAN_REGISTER);
                }

                serverConnection = new ServerConnection(httpConfiguration);

                HashMap<String, Object> serverData = new HashMap<String, Object>();


                serverData.clear();
                serverData.put(Constant.ID, id);
                serverData.put(Constant.TOKEN, FirebaseInstanceId.getInstance().getToken());
                //serverData.put("channel",valuePath);
                serverData.put(Constant.EXPRESSION, expression);
                serverConnection.useHttpMethod(serverData, cb);


    }


    void sendUnregisterRequest(String id, String location) {


            if(location.contains("http")){
                httpConfiguration.putString(Constant.SERVER_URL,location+SWAN_UNREGISTER);
            }
            else {
                httpConfiguration.putString(Constant.SERVER_URL, DEFAULT_URL+SWAN_UNREGISTER);
            }

            serverConnection = new ServerConnection(httpConfiguration);

            HashMap<String, Object> serverData = new HashMap<String, Object>();


            serverData.clear();
            serverData.put(Constant.ID, id);
            serverData.put(Constant.TOKEN, FirebaseInstanceId.getInstance().getToken());
            serverConnection.useHttpMethod(serverData, cb);


    }



}
