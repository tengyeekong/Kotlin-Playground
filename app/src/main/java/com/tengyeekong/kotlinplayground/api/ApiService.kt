package com.tengyeekong.kotlinplayground.api

import com.tengyeekong.kotlinplayground.model.Listing
import com.tengyeekong.kotlinplayground.model.Login
import com.tengyeekong.kotlinplayground.model.UpdateStatus

import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
            @Field("email") username: String,
            @Field("password") password: String
    ): Response<Login>

    @GET("listing")
    suspend fun getListing(
            @Query("id") id: String,
            @Query("token") token: String
    ): Response<Listing>

    @FormUrlEncoded
    @POST("listing/update")
    suspend fun updateList(
            @Field("id") id: String,
            @Field("token") token: String,
            @Field("listing_id") listing_id: String,
            @Field("listing_name") listing_name: String,
            @Field("distance") distance: String
    ): Response<UpdateStatus>
}
