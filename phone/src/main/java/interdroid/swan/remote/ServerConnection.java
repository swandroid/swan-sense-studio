package interdroid.swan.remote;

import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import retrofit.Callback;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.converter.GsonConverter;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by Roshan Bharath Das on 27/11/15.
 */
public class ServerConnection {

    Gson gson;
    RestAdapter eventResultsAdapter;
    String serverUrl;
    String serverHttpMethod;
    //String serverHttpAuthorization;
    String serverHttpHeader;
    String serverHttpBody;
    String serverHttpBodyType;
    HashMap<String, String> httpHeaders = new HashMap<String, String>();
    HashMap<String, Object> httpBody = new HashMap<String, Object>();
    IServerConnectionMethod service;


    public class SWANRetrofitInterceptor implements RequestInterceptor {

        @Override
        public void intercept(RequestFacade req) {

            Iterator it = httpHeaders.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                req.addHeader(pair.getKey().toString(), pair.getValue().toString());
                it.remove(); // avoids a ConcurrentModificationException

            }

            // TODO: Authorization
            // req.addHeader("Authorization", string);

        }
    }


    public ServerConnection(Bundle httpConfig) {

        serverUrl = httpConfig.getString(Constant.SERVER_URL);
        serverHttpMethod = httpConfig.getString(Constant.SERVER_HTTP_METHOD);
        serverHttpBodyType = httpConfig.getString(Constant.SERVER_HTTP_BODY_TYPE);


        setHeaderBasedOnBodyType(serverHttpBodyType);

        serverHttpHeader = httpConfig.getString(Constant.SERVER_HTTP_HEADER);

        //value inputed as key1:value1,key2:value2

        if(serverHttpHeader!=null) {
            parseAndSetHeader(serverHttpHeader);
        }


        serverHttpBody = httpConfig.getString(Constant.SERVER_HTTP_BODY);


        if(serverHttpBody!=null) {
            parseAndSetBody(serverHttpBody);
        }

        gson = new GsonBuilder().create();

        eventResultsAdapter = new RestAdapter.Builder()
                .setRequestInterceptor(new SWANRetrofitInterceptor())
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setConverter(new GsonConverter(gson))
                .setEndpoint(serverUrl)
                .build();

        service = eventResultsAdapter.create(IServerConnectionMethod.class);

    }


    public IServerConnectionMethod getService() {

        return this.service;

    }


    public void useHttpMethod(HashMap<String, Object> hashData, Callback<Object> cb) {


        hashData.putAll(httpBody);


        if (serverHttpBodyType.equals(Constant.HTTP_BODY_TYPE_FORM_DATA)) {

            if (serverHttpMethod.equals(Constant.HTTP_METHOD_POST)) {
                service.postFormData(hashData, cb);
            } else if (serverHttpMethod.equals(Constant.HTTP_METHOD_PUT)) {

                service.putFormData(hashData, cb);
            }

        } else if (serverHttpBodyType.equals(Constant.HTTP_BODY_TYPE_APPLICATION_JSON)) {

            if (serverHttpMethod.equals(Constant.HTTP_METHOD_POST)) {
                service.postJSON(hashData, cb);
            } else if (serverHttpMethod.equals(Constant.HTTP_METHOD_PUT)) {
                service.putJSON(hashData, cb);
            }

        }
        //TODO : add fuctionality for xml
        //else if(serverHttpBodyType.equals("application/xml")){

        //}


    }


    public void useHttpMethod(String body, Callback<Object> cb) {

        if (serverHttpMethod.equals(Constant.HTTP_METHOD_POST)) {
            service.postData(body, cb);
        } else if (serverHttpMethod.equals(Constant.HTTP_METHOD_PUT)) {
            service.putData(body, cb);
        }
    }


    public void parseAndSetHeader(String rawData) {

        if (!rawData.equals(Constant.NULL) && !rawData.isEmpty() && rawData!=null) {
            String[] pairs = rawData.split(",");
            for (String pair : pairs) {
                String[] data = pair.split(":");
                httpHeaders.put(data[0], data[1]);
            }

        }

    }


    public void parseAndSetBody(String rawData) {

        if (!rawData.equals(Constant.NULL) && !rawData.isEmpty() && rawData!=null) {
            String[] pairs = rawData.split(",");
            for (String pair : pairs) {
                String[] data = pair.split(":");
                httpBody.put(data[0], data[1]);
            }

        }

    }

    public void setHeaderBasedOnBodyType(String bodyType) {

        if (bodyType.equals(Constant.HTTP_BODY_TYPE_TEXT_PLAIN)) {
            httpHeaders.put(Constant.SERVER_HTTP_HEADER_CONTENT_TYPE, Constant.HTTP_BODY_TYPE_TEXT_PLAIN);
        } else if (bodyType.equals(Constant.HTTP_BODY_TYPE_APPLICATION_JSON)) {
            httpHeaders.put(Constant.SERVER_HTTP_HEADER_CONTENT_TYPE, Constant.HTTP_BODY_TYPE_APPLICATION_JSON);
        } else if (bodyType.equals(Constant.HTTP_BODY_TYPE_APPLICATION_XML)) {
            httpHeaders.put(Constant.SERVER_HTTP_HEADER_CONTENT_TYPE, Constant.HTTP_BODY_TYPE_APPLICATION_XML);
        }

    }


}
