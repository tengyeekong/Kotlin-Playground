package com.solution.it.newsoft;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.solution.it.newsoft.model.Listing;
import com.solution.it.newsoft.model.Login;

import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private GetDataService service;
    private MutableLiveData<Listing> listing;
    private static Repository repository;

    public static Repository getInstance() {
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }


    public Repository() {
        service = RetrofitClientInstance.getRetrofitInstance().create(GetDataService.class);
    }

    public MutableLiveData<Listing> getListing(String id, String token) {
        MutableLiveData<Listing> listingData = new MutableLiveData<>();
        service.getListing(id, token).enqueue(new Callback<Listing>() {
            @Override
            public void onResponse(Call<Listing> call, Response<Listing> response) {
                if (response.isSuccessful()) {
                    listingData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Listing> call, Throwable t) {
                listingData.setValue(null);
            }
        });
        return listingData;
    }

    public Login login(String username, String password) {
        try {
            Call<Login> call = service.login(username, password);
            return call.execute().body();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
