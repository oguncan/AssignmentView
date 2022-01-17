package com.okmobile.assignmentview.network

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

class ServicesManager {

    companion object{

        private val BASE_URL = "https://httpbin.org/"

        fun getHttpBinService(): HttpBinService? {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HttpBinService::class.java)
        }


        fun getHttpBinService(client: OkHttpClient): HttpBinService? {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(HttpBinService::class.java)
        }
    }



}