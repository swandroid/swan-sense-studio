package interdroid.swan.remote;

import java.util.HashMap;
import java.util.Map;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.FieldMap;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by Roshan Bharath Das on 06/07/16.
 */
public interface IServerConnectionMethod {

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
