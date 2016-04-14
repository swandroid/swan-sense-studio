package org.ttn.android.sdk.api.retrofit.v0;

import org.ttn.android.sdk.domain.node.Node;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Query;

public interface NodeApi {
    String NODES_API_BASE_URL = "/v0/nodes";

    @GET(NODES_API_BASE_URL)
    void get(@Query("time_span") String timeSpan,
             Callback<List<Node>> callback);

}
