package com.siegengel.ping_fct.Notifications

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object Client {
    private var retrofit: Retrofit? = null

    fun getClient(url: String?): Retrofit? {
        if (retrofit == null) {
            retrofit = url?.let {
                Retrofit.Builder()
                    .baseUrl(it)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
            }
        }
        return retrofit
    }
}