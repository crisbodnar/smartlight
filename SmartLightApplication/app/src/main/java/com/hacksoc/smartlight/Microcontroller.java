package com.hacksoc.smartlight;

import com.squareup.okhttp.ResponseBody;

import retrofit.client.Response;
import retrofit.http.*;
import retrofit.*;

public interface Microcontroller {
    @FormUrlEncoded
    @POST("/v1/devices/2d0047001047343339383037/led?access_token=63d5864bae820ba1599a1befe7188c5a7b3f0fb3")
    public void on (
            @Field("arg") String arg,
            Callback<Response> callback
    );

    @FormUrlEncoded
    @POST("/v1/devices/2d0047001047343339383037/led?access_token=63d5864bae820ba1599a1befe7188c5a7b3f0fb3")
    public void off (
            @Field("arg") String arg,
            Callback<Response> callback
    );
}