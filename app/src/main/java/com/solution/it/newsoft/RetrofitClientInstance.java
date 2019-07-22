package com.solution.it.newsoft;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClientInstance {

    private static String BASE_URL = "http://interview.advisoryapps.com/index.php/";
    private static Retrofit retrofit;
    private static Gson gson;

    public static Retrofit getRetrofitInstance() {

        if (gson == null) {
            gson = new GsonBuilder()
                    .setLenient()
                    .create();
        }
        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
