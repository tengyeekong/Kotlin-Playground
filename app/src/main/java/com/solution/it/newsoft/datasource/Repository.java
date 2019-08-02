package com.solution.it.newsoft.datasource;

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
import com.solution.it.newsoft.viewmodel.ViewModel;

import java.io.IOException;

import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class Repository {
    private ApiService service;
    private SharedPreferences prefs;
    private static String id, /*username, password,*/ token;

    @Inject
    public Repository(ApiService service, SharedPreferences prefs) {
        this.service = service;
        this.prefs = prefs;
        id = prefs.getString(ViewModel.ID, "");
        token = prefs.getString(ViewModel.TOKEN, "");
    }

    public void getListing(PageKeyedDataSource.LoadInitialCallback<Long, List> initCallback,
                           PageKeyedDataSource.LoadCallback<Long, List> afterCallback,
                           MutableLiveData networkState, long nextKey) {
        Call<Listing> call = service.getListing(id, token);
        try {
            Listing listing = call.execute().body();
            if (listing.getStatus().getCode().equals("200")) {
                if (initCallback != null)
                    initCallback.onResult(listing.getListing(), null, nextKey);
                else
                    afterCallback.onResult(listing.getListing(), nextKey);
                networkState.postValue(NetworkState.LOADED);
                return;
            }
            else if (listing.getStatus().getCode().equals("400")) {
                //can't use retrofit interceptor in this case to get new token
                //since the token is not passing through header
                String username = prefs.getString(ViewModel.USERNAME, "");
                String password = prefs.getString(ViewModel.PASSWORD, "");
                Call<Login> loginCall = service.login(username, password);
                Login login = loginCall.execute().body();
                if (login != null) {
                    if (login.getStatus() != null && login.getStatus().getCode().equals("200")) {
                        String id = login.getId();
                        String token = login.getToken();
                        prefs.edit().putString(ViewModel.USERNAME, username)
                                .putString(ViewModel.PASSWORD, password)
                                .putString(ViewModel.ID, id)
                                .putString(ViewModel.TOKEN, token)
                                .apply();

                        Call<Listing> listingCall = service.getListing(id, token);
                        Listing listing2 = listingCall.execute().body();
                        if (listing2.getStatus().getCode().equals("200")) {
                            if (initCallback != null)
                                initCallback.onResult(listing2.getListing(), null, nextKey);
                            else
                                afterCallback.onResult(listing2.getListing(), nextKey);
                            networkState.postValue(NetworkState.LOADED);
                            return;
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


//        service.getListing(id, token).enqueue(new Callback<Listing>() {
//            @Override
//            public void onResponse(Call<Listing> call, Response<Listing> response) {
//                if (response.isSuccessful()) {
//                    if (response.body().getStatus().getCode().equals("200")) {
//                        if (initCallback != null)
//                            initCallback.onResult(response.body().getListing(), null, nextKey);
//                        else
//                            afterCallback.onResult(response.body().getListing(), nextKey);
//                        networkState.postValue(NetworkState.LOADED);
//                        return;
//                    }
//                    else if (response.body().getStatus().getCode().equals("400")) {
//                        //can't use retrofit interceptor in this case to get new token
//                        //since the token is not passing through header
//                        String username = prefs.getString(ViewModel.USERNAME, "");
//                        String password = prefs.getString(ViewModel.PASSWORD, "");
//                        service.login(username, password).enqueue(new Callback<Login>() {
//                            @Override
//                            public void onResponse(Call<Login> call, Response<Login> response) {
//                                if (response.isSuccessful()) {
//                                    if (response.body() != null) {
//                                        if (response.body().getStatus() != null && response.body().getStatus().getCode().equals("200")) {
//                                            String id = response.body().getId();
//                                            String token = response.body().getToken();
//                                            prefs.edit().putString(ViewModel.USERNAME, username)
//                                                    .putString(ViewModel.PASSWORD, password)
//                                                    .putString(ViewModel.ID, id)
//                                                    .putString(ViewModel.TOKEN, token)
//                                                    .apply();
//
//                                            service.getListing(id, token).enqueue(new Callback<Listing>() {
//                                                @Override
//                                                public void onResponse(Call<Listing> call, Response<Listing> response) {
//                                                    if (response.isSuccessful()) {
//                                                        if (response.body().getStatus().getCode().equals("200")) {
//                                                            if (initCallback != null)
//                                                                initCallback.onResult(response.body().getListing(), null, nextKey);
//                                                            else
//                                                                afterCallback.onResult(response.body().getListing(), nextKey);
//                                                            networkState.postValue(NetworkState.LOADED);
//                                                            return;
//                                                        }
//                                                    }
//                                                    networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
//                                                }
//
//                                                @Override
//                                                public void onFailure(Call<Listing> call, Throwable t) {}
//                                            });
//                                        }
//                                    }
//                                }
//                            }
//
//                            @Override
//                            public void onFailure(Call<Login> call, Throwable t) {}
//                        });
//                    }
//                }
//                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
//            }
//
//            @Override
//            public void onFailure(Call<Listing> call, Throwable t) {
//                String errorMessage = t == null ? "unknown error" : t.getMessage();
//                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
//            }
//        });
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
                    else if (response.body().getStatus().getCode().equals("400")) {
                        //can't use retrofit interceptor in this case to get new token
                        //since the token is not passing through header
                        String username = prefs.getString(ViewModel.USERNAME, "");
                        String password = prefs.getString(ViewModel.PASSWORD, "");
                        service.login(username, password).enqueue(new Callback<Login>() {
                            @Override
                            public void onResponse(Call<Login> call, Response<Login> response) {
                                if (response.isSuccessful()) {
                                    if (response.body() != null) {
                                        if (response.body().getStatus() != null && response.body().getStatus().getCode().equals("200")) {
                                            String id = response.body().getId();
                                            String token = response.body().getToken();
                                            prefs.edit().putString(ViewModel.USERNAME, username)
                                                    .putString(ViewModel.PASSWORD, password)
                                                    .putString(ViewModel.ID, id)
                                                    .putString(ViewModel.TOKEN, token)
                                                    .apply();

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
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<Login> call, Throwable t) {}
                        });
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
