package interdroid.swan.sensors.impl;

import java.util.HashMap;

import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.PUT;

/**
 * Created by Roshan Bharath Das on 25/11/15.
 */
public interface ServerAPI {

    @GET("/")
    void getInfo(Callback<String> cb);


    @POST("/")
    void postInfo(@Body String content, Callback<String> cb);

    @FormUrlEncoded
    @POST("/")
    void postFormInfo(@Field("api_key") String key, @Field("field1") String field1, Callback<String> cb);

    // @GET("/")
    // void getInfoWithHeaders(@Header("key") String password, Callback<String> cb);

    @POST("/")
    void postJson(@Body HashMap<String, Object> body);


    @PUT("/")
    void putInfo(@Body String content, Callback<String> cb);

}