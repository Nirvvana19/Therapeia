package com.example.therapeia.Fragmentos;

import com.example.therapeia.Notificationsc.MyResponse;
import com.example.therapeia.Notificationsc.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AIzaSyDu1ZoeuMMuUjY4l-fE32MQ2v3xpbqPz-U"
    })

    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);
}
