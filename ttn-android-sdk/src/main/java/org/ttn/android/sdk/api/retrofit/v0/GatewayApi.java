package org.ttn.android.sdk.api.retrofit.v0;

import org.ttn.android.sdk.domain.gateway.Gateway;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface GatewayApi {
    String NODES_API_BASE_URL = "/v0/gateways";

    @GET(NODES_API_BASE_URL)
    void get(@Query("time_span") String timeSpan,
             @Query("limit") int limit,
             @Query("offset") int offset,
             Callback<List<Gateway>> callback);
}
