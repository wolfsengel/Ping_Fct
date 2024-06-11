package com.siegengel.ping_fct.Notifications


import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface APIService {
    @Headers(
        "Content-Type:application/json",
        "Authorization:key=BBE2iAhDki5SVYaNqCzqzF4VtK0iFcS08MXWJTNZBgKNgG8rrrqMH2hZRgkytLG8f_FusHcILxvaBaQv9MwdCAA"
    )
    @POST("fcm/send")
    fun sendNotification(@Body body: Sender?): Call<MyResponse?>?
}