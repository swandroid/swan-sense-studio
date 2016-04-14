package org.ttn.android.sdk.api.retrofit.v0;

import org.ttn.android.sdk.domain.packet.Packet;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

public interface PacketApi {
    String NODES_API_BASE_URL = "/v0/nodes";

    @GET(NODES_API_BASE_URL + "/{node_eui}")
    void get(@Path("node_eui") String nodeEui,
             @Query("time_span") String timeSpan,
             @Query("limit") Integer limit,
             @Query("offset") Integer offset,
             Callback<List<Packet>> callback);
}
