package com.solution.it.newsoft;

import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.solution.it.newsoft.api.ApiService;
import com.solution.it.newsoft.api.RetrofitClientInstance;
import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.Listing;
import com.solution.it.newsoft.model.Login;
import com.solution.it.newsoft.model.UpdateStatus;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private ApiService service;
    private static Repository repository;
    private static String id, username, password, token;
    private static SharedPreferences prefs;

    public static Repository getInstance(SharedPreferences prefs) {
        Repository.prefs = prefs;
        id = prefs.getString(ViewModel.ID, "");
        username = prefs.getString(ViewModel.USERNAME, "");
        password = prefs.getString(ViewModel.PASSWORD, "");
        token = prefs.getString(ViewModel.TOKEN, "");
        if (repository == null) {
            repository = new Repository();
        }
        return repository;
    }

    public Repository() {
        service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
    }

//    public LiveData<ArrayList<List>> getListing(String id, String token) {
//        MutableLiveData<ArrayList<List>> listingData = new MutableLiveData<>();
//        service.getListing(id, token).enqueue(new Callback<Listing>() {
//            @Override
//            public void onResponse(Call<Listing> call, Response<Listing> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().getCode().equals("200")) {
//                        listingData.setValue(response.body().getListing());
//                        return;
//                    }
//                }
//                listingData.setValue(null);
//            }
//
//            @Override
//            public void onFailure(Call<Listing> call, Throwable t) {
//                listingData.setValue(null);
//            }
//        });
//        return listingData;
//    }

    public LiveData<Login> login(String username, String password) {
        MutableLiveData<Login> loginData = new MutableLiveData<>();
        service.login(username, password).enqueue(new Callback<Login>() {
            @Override
            public void onResponse(Call<Login> call, Response<Login> response) {
                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        if (response.body().getStatus() != null && response.body().getStatus().getCode().equals("200")) {
                            id = response.body().getId();
                            token = response.body().getToken();
                            prefs.edit().putString(ViewModel.USERNAME, username)
                                    .putString(ViewModel.PASSWORD, password)
                                    .putString(ViewModel.ID, id)
                                    .putString(ViewModel.TOKEN, token)
                                    .apply();
                        }
                    }
                    loginData.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<Login> call, Throwable t) {
                loginData.setValue(null);
            }
        });
        return loginData;
    }

    public LiveData<Boolean> updateList(String listing_id, String listing_name, String distance) {
        MutableLiveData<Boolean> isUpdated = new MutableLiveData<>();
        service.updateList(id, token, listing_id, listing_name, distance).enqueue(new Callback<UpdateStatus>() {
            @Override
            public void onResponse(Call<UpdateStatus> call, Response<UpdateStatus> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getCode().equals("200")) {
                        isUpdated.setValue(true);
                        return;
                    }
                }
                isUpdated.setValue(false);
            }

            @Override
            public void onFailure(Call<UpdateStatus> call, Throwable t) {
                isUpdated.setValue(false);
            }
        });
        return isUpdated;
    }
}
