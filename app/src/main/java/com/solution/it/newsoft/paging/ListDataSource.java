package com.solution.it.newsoft.paging;

import android.util.Log;

import com.solution.it.newsoft.datasource.Repository;
import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.NetworkState;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.paging.PageKeyedDataSource;

public class ListDataSource extends PageKeyedDataSource<Long, List> {

    private static final String TAG = ListDataSource.class.getSimpleName();

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
}
