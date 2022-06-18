package com.mobio.analytics.network;

import com.mobio.analytics.client.model.reponse.SendEventResponse;

import java.util.Map;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface Api {
//    String BASE_URL = "https://api-test1.mobio.vn/dynamic-event/api/v1.0/";
   // String BASE_URL = "https://t1.mobio.vn/digienty/web/api/v1.1/";

    @POST("{endpoint}")
    Call<Void> sendSync(@HeaderMap Map<String, String> headers, @Body Map<String, Object> sendSyncObject, @Path("endpoint") String endpoint);

    @POST("track.json")
    Call<SendEventResponse> sendEvent(@Body Map<String, Object> sendEventObject);

    @POST("device.json")
    Call<SendEventResponse> sendDevice(@Body Map<String, Object> sendDeviceObject);

    @POST("device/notification.json")
    Call<SendEventResponse> sendNotification(@Body Map<String, Object> sendNotificationObject);
}
