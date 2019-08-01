package com.solution.it.newsoft;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Transformations;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import io.reactivex.disposables.CompositeDisposable;

import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.Login;
import com.solution.it.newsoft.model.NetworkState;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ViewModel extends AndroidViewModel {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ID = "id";
    public static final String TOKEN = "token";
    private final CompositeDisposable disposables = new CompositeDisposable();
    private Repository repository;
    private SharedPreferences prefs;

    private Executor executor;
    private ListDataFactory listDataFactory;
    private PagedList.Config pagedListConfig;
    private LiveData<NetworkState> networkState;
    private LiveData<PagedList<List>> listLiveData;

    public ViewModel(@NonNull Application application) {
        super(application);
        prefs = application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        repository = Repository.getInstance(prefs);
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

    public SharedPreferences getPrefs() {
        return prefs;
    }

//    public LiveData<ArrayList<List>> getListing(String id, String token) {
//        if (!checkInternetConnection(getApplication())) return null;
//        return repository.getListing(id, token);
//    }

    public LiveData<Login> login(String username, String password) {
        if (!checkInternetConnection(getApplication())) return null;
        return repository.login(username, password);
    }

    public LiveData<Boolean> updateList(String id, String listName, String distance) {
        if (!checkInternetConnection(getApplication())) return null;
        return repository.updateList(id, listName, distance);
    }

    public boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onCleared() {
        disposables.clear();
    }
}
