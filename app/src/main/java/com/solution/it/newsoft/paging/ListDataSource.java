package com.solution.it.newsoft.paging;

import android.util.Log;

import com.solution.it.newsoft.datasource.Repository;
import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.NetworkState;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ListDataSource extends PageKeyedDataSource<Long, List> {

    private static final String TAG = ListDataSource.class.getSimpleName();

    private MutableLiveData networkState;
    private Repository repository;

    public ListDataSource(Repository repository) {
        this.repository = repository;
        networkState = new MutableLiveData();
    }


    public MutableLiveData getNetworkState() {
        return networkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<Long, List> callback) {

        networkState.postValue(NetworkState.LOADING);

        repository.getListing(callback, null, networkState, 2l);
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
        int itemCount = Integer.valueOf(params.key.toString()) * 10;
        if (params.key > 1) {
            callback.onResult(getDummies(itemCount), nextKey);
            networkState.postValue(NetworkState.LOADED);
        } else {
            repository.getListing(null, callback, networkState, nextKey);
        }
    }

    public ArrayList<List> getDummies(int itemCount) {
        ArrayList<List> lists = new ArrayList<>();
//        if (itemCount < 100)
        Observable.fromCallable(() -> {
            for (int i = 0; i < 10; i++) {
                String value = String.valueOf(10000 + itemCount + i + 1);
                List list = new List(value, value, value);
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
