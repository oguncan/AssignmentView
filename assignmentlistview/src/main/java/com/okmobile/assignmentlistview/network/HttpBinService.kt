package com.okmobile.assignmentview.network

import retrofit2.Call
import retrofit2.http.GET

import retrofit2.http.POST


interface HttpBinService {

    @GET("get")
    fun get(): Call<Map<String?, Any?>?>?

    @POST("post")
    fun post(): Call<Map<String?, Any?>?>?

}