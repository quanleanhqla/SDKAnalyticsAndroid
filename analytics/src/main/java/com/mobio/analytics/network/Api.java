package com.mobio.analytics.network;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {
    String BASE_URL = "https://api-test1.mobio.vn/dynamic-event/api/v1.0/";
    @POST("{endpoint}")
    Call<Void> sendSync(@HeaderMap Map<String, String> headers, @Body HashMap<String, Object> sendSyncObject, @Path("endpoint") String endpoint);
}
