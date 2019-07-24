package com.solution.it.newsoft.api;

import com.solution.it.newsoft.model.Listing;
import com.solution.it.newsoft.model.Login;
import com.solution.it.newsoft.model.UpdateStatus;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ApiService {

    @FormUrlEncoded
    @POST("login")
    Call<Login> login(
            @Field("email") String username,
            @Field("password") String password
    );

    @GET("listing")
    Call<Listing> getListing(
            @Query("id") String id,
            @Query("token") String token
    );

    @FormUrlEncoded
    @POST("listing/update")
    Call<UpdateStatus> updateList(
            @Field("id") String id,
            @Field("token") String token,
            @Field("listing_id") String listing_id,
            @Field("listing_name") String listing_name,
            @Field("distance") String distance
    );
}
