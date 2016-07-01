package interdroid.swan.remote.cloud;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import interdroid.swan.sensors.impl.ServerConnection;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Roshan Bharath Das on 27/06/16.
 */
public class CloudCommunication {

    private static final String TAG = "CloudCommunication";

    ServerConnection serverConnection;


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


    void sendRegisterRequest(String id, String expression) {


                httpConfiguration.putString(Constant.SERVER_URL,"http://swan-cloud.herokuapp.com/swan/register/");

                serverConnection = new ServerConnection(httpConfiguration);

                HashMap<String, Object> serverData = new HashMap<String, Object>();


                serverData.clear();
                serverData.put(Constant.ID, id);
                serverData.put(Constant.TOKEN, FirebaseInstanceId.getInstance().getToken());
                //serverData.put("channel",valuePath);
                serverData.put(Constant.EXPRESSION, expression);
                serverConnection.useHttpMethod(serverData, cb);


    }


    void sendUnregisterRequest(String id) {



            httpConfiguration.putString(Constant.SERVER_URL,"http://swan-cloud.herokuapp.com/swan/unregister/");

            serverConnection = new ServerConnection(httpConfiguration);

            HashMap<String, Object> serverData = new HashMap<String, Object>();


            serverData.clear();
            serverData.put(Constant.ID, id);
            serverData.put(Constant.TOKEN, FirebaseInstanceId.getInstance().getToken());
            serverConnection.useHttpMethod(serverData, cb);


    }



}