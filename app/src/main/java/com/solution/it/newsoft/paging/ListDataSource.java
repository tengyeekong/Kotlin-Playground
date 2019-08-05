package com.solution.it.newsoft.paging;

import android.util.Log;

import com.solution.it.newsoft.datasource.Repository;
import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.NetworkState;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ListDataSource extends PageKeyedDataSource<Long, List> {

    private static final String TAG = ListDataSource.class.getSimpleName();
    private LoadParams<Long> params;
    private LoadCallback<Long, List> callback;

    private MutableLiveData<NetworkState> networkState;
    private Repository repository;

    ListDataSource(Repository repository) {
        this.repository = repository;
        networkState = new MutableLiveData<>();
    }


    public MutableLiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams<Long> params,
                            @NonNull LoadInitialCallback<Long, List> callback) {

        networkState.postValue(NetworkState.LOADING);

        repository.getListing(callback, null, networkState, 2L);
    }

    @Override
    public void loadBefore(@NonNull LoadParams<Long> params,
                           @NonNull LoadCallback<Long, List> callback) {

    }

    @Override
    public void loadAfter(@NonNull LoadParams<Long> params,
                          @NonNull LoadCallback<Long, List> callback) {

        this.params = params;
        this.callback = callback;

        Log.i(TAG, "Loading Page " + params.key + ", Count " + params.requestedLoadSize);

        networkState.postValue(NetworkState.LOADING);

        long nextKey = params.key + 1;
        int itemCount = Integer.valueOf(params.key.toString()) * 10;
        if (params.key > 1) {
            repository.getDummies(itemCount, callback, networkState, nextKey);
        } else {
            repository.getListing(null, callback, networkState, nextKey);
        }
    }

    public void retry() {
        Completable.fromAction(() -> loadAfter(params, callback))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
    }
}
