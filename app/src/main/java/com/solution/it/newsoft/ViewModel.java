package com.solution.it.newsoft;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import io.reactivex.disposables.CompositeDisposable;

import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.Login;
import com.solution.it.newsoft.model.NetworkState;
import com.solution.it.newsoft.paging.ListDataFactory;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Inject;

public class ViewModel extends androidx.lifecycle.ViewModel {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ID = "id";
    public static final String TOKEN = "token";
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Repository repository;

    private Executor executor;
    private ListDataFactory listDataFactory;
    private PagedList.Config pagedListConfig;
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<List>> listLiveData;

    @Inject
    public ViewModel(Repository repository) {
        this.repository = repository;
        executor = Executors.newFixedThreadPool(5);
        listDataFactory = new ListDataFactory(repository);

        networkState = Transformations.switchMap(listDataFactory.getMutableLiveData(),
                dataSource -> dataSource.getNetworkState());

        pagedListConfig = (new PagedList.Config.Builder())
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
//        if (!checkInternetConnection(getApplication())) return null;
//        return repository.getListing(id, token);
//    }

    public LiveData<Login> login(String username, String password) {
        return repository.login(username, password);
    }

    public LiveData<Boolean> updateList(String id, String listName, String distance) {
        return repository.updateList(id, listName, distance);
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
