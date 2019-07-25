package com.solution.it.newsoft;

import android.content.SharedPreferences;
import android.util.Log;

import com.solution.it.newsoft.api.ApiService;
import com.solution.it.newsoft.api.RetrofitClientInstance;
import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.Listing;
import com.solution.it.newsoft.model.NetworkState;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListDataSource extends PageKeyedDataSource<Long, List> {

    private static final String TAG = ListDataSource.class.getSimpleName();

    private MutableLiveData networkState;
    private ApiService service;
    private SharedPreferences prefs;

    public ListDataSource(SharedPreferences prefs) {
        this.prefs = prefs;

        networkState = new MutableLiveData();
        service = RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
    }


    public MutableLiveData getNetworkState() {
        return networkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<Long, List> callback) {

        networkState.postValue(NetworkState.LOADING);

        service.getListing(prefs.getString(ViewModel.ID, ""), prefs.getString(ViewModel.TOKEN, "")).enqueue(new Callback<Listing>() {
            @Override
            public void onResponse(Call<Listing> call, Response<Listing> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getCode().equals("200")) {
                        callback.onResult(response.body().getListing(), null, 2l);
                        networkState.postValue(NetworkState.LOADED);
                        return;
                    }
                }
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
            }

            @Override
            public void onFailure(Call<Listing> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params,
                           @NonNull LoadCallback<Long, List> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params,
                          @NonNull LoadCallback<Long, List> callback) {

        Log.i(TAG, "Loading Page " + params.key + ", Count " + params.requestedLoadSize);

        networkState.postValue(NetworkState.LOADING);

        long nextKey = params.key + 1;
//        callback.onResult(getDummies(Integer.valueOf(params.key.toString()) * 10), nextKey);
//        networkState.postValue(NetworkState.LOADED);

        service.getListing(prefs.getString(ViewModel.ID, ""), prefs.getString(ViewModel.TOKEN, "")).enqueue(new Callback<Listing>() {
            @Override
            public void onResponse(Call<Listing> call, Response<Listing> response) {
                if (response.isSuccessful()) {
                    if (response.body().getStatus().getCode().equals("200")) {
                        callback.onResult(response.body().getListing(), nextKey);
                        networkState.postValue(NetworkState.LOADED);
                        return;
                    }
                }
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, response.message()));
            }

            @Override
            public void onFailure(Call<Listing> call, Throwable t) {
                String errorMessage = t == null ? "unknown error" : t.getMessage();
                networkState.postValue(new NetworkState(NetworkState.Status.FAILED, errorMessage));
            }
        });
    }

    public ArrayList<List> getDummies(int itemCount) {
        ArrayList<List> lists = new ArrayList<>();
        Observable.fromCallable(() -> {
            if (itemCount < 100)
                for (int i = 0; i < 10; i++) {
                    List list = new List("100" + (itemCount + (i + 1)),
                            "100" + (itemCount + (i + 1)), "100" + (itemCount + (i + 1)));
                    lists.add(list);
                }
            return lists;
        })
                .delay(1, TimeUnit.SECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();

        return lists;
    }
}
