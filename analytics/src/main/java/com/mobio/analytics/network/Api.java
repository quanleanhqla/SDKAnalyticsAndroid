package com.mobio.analytics.network;

import com.mobio.analytics.client.models.SendSyncObject;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

public interface Api {
    String BASE_URL = "https://api-test1.mobio.vn/dynamic-event/api/v1.0/";
    @POST("sync")
    Call<Void> sendSync(@HeaderMap Map<String, String> headers, @Body SendSyncObject sendSyncObject);
}
