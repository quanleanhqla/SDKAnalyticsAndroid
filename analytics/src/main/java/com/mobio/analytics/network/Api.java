package com.mobio.analytics.network;

import com.mobio.analytics.client.model.reponse.SendEventResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface Api {
//    String BASE_URL = "https://api-test1.mobio.vn/dynamic-event/api/v1.0/";
    String BASE_URL = "https://t1.mobio.vn/digienty/web/api/v1.1/";

    @POST("{endpoint}")
    Call<Void> sendSync(@HeaderMap Map<String, String> headers, @Body Map<String, Object> sendSyncObject, @Path("endpoint") String endpoint);

    @POST("track.json")
    Call<Void> sendEvent(@Body Map<String, Object> sendEventObject);

    @POST("device.json")
    Call<Void> sendDevice(@Body Map<String, Object> sendDeviceObject);
}
