package com.solution.it.newsoft.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModel;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;

import com.solution.it.newsoft.datasource.Repository;
import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.Login;
import com.solution.it.newsoft.model.NetworkState;
import com.solution.it.newsoft.paging.ListDataFactory;
import com.solution.it.newsoft.paging.ListDataSource;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class ListingViewModel extends ViewModel {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ID = "id";
    public static final String TOKEN = "token";
    private Repository repository;

    private ListDataFactory listDataFactory;
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<List>> listLiveData;

    @Inject
    ListingViewModel(Repository repository, ListDataFactory listDataFactory) {
        this.repository = repository;
        this.listDataFactory = listDataFactory;
        Executor executor = Executors.newFixedThreadPool(5);

        networkState = Transformations.switchMap(listDataFactory.getMutableLiveData(),
                ListDataSource::getNetworkState);

        PagedList.Config pagedListConfig = (new PagedList.Config.Builder())
                .setPageSize(10)
                .setInitialLoadSizeHint(10)
                .setEnablePlaceholders(false)
                .build();

        listLiveData = new LivePagedListBuilder<>(listDataFactory, pagedListConfig)
                .setFetchExecutor(executor)
                .build();
    }

    public LiveData<NetworkState> getNetworkState() {
        return networkState;
    }

    public LiveData<PagedList<List>> getListLiveData() {
        return listLiveData;
    }

    public void reload() {
        listDataFactory.reload();
    }

//    public LiveData<ArrayList<List>> getListing(String id, String token) {
//        return repository.getListing(id, token);
//    }

    public LiveData<Login> login(String username, String password) {
        return repository.login(username, password);
    }

    public LiveData<Boolean> updateList(String id, String listName, String distance) {
        return repository.updateList(id, listName, distance);
    }
}
