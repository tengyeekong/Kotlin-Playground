package com.solution.it.newsoft.datasource;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import com.solution.it.newsoft.api.ApiService;
import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.Listing;
import com.solution.it.newsoft.model.Login;
import com.solution.it.newsoft.model.NetworkState;
import com.solution.it.newsoft.model.UpdateStatus;
import com.solution.it.newsoft.viewmodel.ListingViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private ApiService service;
    private SharedPreferences prefs;
    private static String id, /*username, password,*/ token;

    public Repository(ApiService service, SharedPreferences prefs) {
        this.service = service;
        this.prefs = prefs;
        id = prefs.getString(ListingViewModel.ID, "");
        token = prefs.getString(ListingViewModel.TOKEN, "");
    }

    public void getListing(PageKeyedDataSource.LoadInitialCallback<Long, List> initCallback,
                           PageKeyedDataSource.LoadCallback<Long, List> afterCallback,
                           MutableLiveData<NetworkState> networkState, long nextKey) {
        Call<Listing> call = service.getListing(id, token);
        try {
            Listing listing = call.execute().body();
            if (listing != null) {
                if (listing.getStatus().getCode().equals("200")) {
                    if (initCallback != null)
                        initCallback.onResult(listing.getListing(), null, nextKey);
                    else
                        afterCallback.onResult(listing.getListing(), nextKey);
                    networkState.postValue(NetworkState.LOADED);
                } else if (listing.getStatus().getCode().equals("400")) {
                    //can't use retrofit interceptor in this case to get new token
                    //since the token is not passing through header
                    String username = prefs.getString(ListingViewModel.USERNAME, "");
                    String password = prefs.getString(ListingViewModel.PASSWORD, "");
                    Call<Login> loginCall = service.login(username, password);
                    Login login = loginCall.execute().body();
                    if (login != null) {
                        if (login.getStatus() != null && login.getStatus().getCode().equals("200")) {
                            String id = login.getId();
                            String token = login.getToken();
                            prefs.edit().putString(ListingViewModel.USERNAME, username)
                                    .putString(ListingViewModel.PASSWORD, password)
                                    .putString(ListingViewModel.ID, id)
                                    .putString(ListingViewModel.TOKEN, token)
                                    .apply();

                            Call<Listing> listingCall = service.getListing(id, token);
                            Listing listing2 = listingCall.execute().body();
                            if (listing2 != null) {
                                if (listing2.getStatus().getCode().equals("200")) {
                                    if (initCallback != null)
                                        initCallback.onResult(listing2.getListing(), null, nextKey);
                                    else
                                        afterCallback.onResult(listing2.getListing(), nextKey);
                                    networkState.postValue(NetworkState.LOADED);
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            networkState.postValue(NetworkState.FAILED);
        }
    }

    @SuppressLint("CheckResult")
    public void getDummies(int itemCount, PageKeyedDataSource.LoadCallback<Long, List> callback,
                           MutableLiveData<NetworkState> networkState, long nextKey) {
        Observable.fromCallable(() -> {
            ArrayList<List> lists = new ArrayList<>();
            int count = 10000 + itemCount;
            for (int i = 0; i < 10 /*&& count < 10105*/; i++) {
                String value = String.valueOf(++count);
                List list = new List(value, value, value);
                lists.add(list);
            }
            return lists;
        })
                .delay(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(lists -> {
                    if (lists.size() > 0) callback.onResult(lists, nextKey);
                    networkState.postValue(NetworkState.LOADED);
                }, throwable -> throwable.printStackTrace());
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
                            prefs.edit().putString(ListingViewModel.USERNAME, username)
                                    .putString(ListingViewModel.PASSWORD, password)
                                    .putString(ListingViewModel.ID, id)
                                    .putString(ListingViewModel.TOKEN, token)
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
                    if (response.body() != null) {
                        if (response.body().getStatus().getCode().equals("200")) {
                            isUpdated.setValue(true);
                            return;
                        } else if (response.body().getStatus().getCode().equals("400")) {
                            //can't use retrofit interceptor in this case to get new token
                            //since the token is not passing through header
                            String username = prefs.getString(ListingViewModel.USERNAME, "");
                            String password = prefs.getString(ListingViewModel.PASSWORD, "");
                            service.login(username, password).enqueue(new Callback<Login>() {
                                @Override
                                public void onResponse(Call<Login> call, Response<Login> response) {
                                    if (response.isSuccessful()) {
                                        if (response.body() != null) {
                                            if (response.body().getStatus() != null && response.body().getStatus().getCode().equals("200")) {
                                                String id = response.body().getId();
                                                String token = response.body().getToken();
                                                prefs.edit().putString(ListingViewModel.USERNAME, username)
                                                        .putString(ListingViewModel.PASSWORD, password)
                                                        .putString(ListingViewModel.ID, id)
                                                        .putString(ListingViewModel.TOKEN, token)
                                                        .apply();

                                                service.updateList(id, token, listing_id, listing_name, distance).enqueue(new Callback<UpdateStatus>() {
                                                    @Override
                                                    public void onResponse(Call<UpdateStatus> call, Response<UpdateStatus> response) {
                                                        if (response.isSuccessful()) {
                                                            if (response.body() != null) {
                                                                if (response.body().getStatus().getCode().equals("200")) {
                                                                    isUpdated.setValue(true);
                                                                    return;
                                                                }
                                                            }
                                                        }
                                                        isUpdated.setValue(false);
                                                    }

                                                    @Override
                                                    public void onFailure(Call<UpdateStatus> call, Throwable t) {
                                                        isUpdated.setValue(false);
                                                    }
                                                });
                                            }
                                        }
                                    }
                                }

                                @Override
                                public void onFailure(Call<Login> call, Throwable t) {
                                }
                            });
                        }
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
