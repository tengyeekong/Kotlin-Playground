package com.solution.it.newsoft;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.solution.it.newsoft.model.List;
import com.solution.it.newsoft.model.Login;

import java.util.ArrayList;

class ViewModel extends AndroidViewModel {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ID = "id";
    public static final String TOKEN = "token";
    private Repository repository;
    private SharedPreferences prefs;

    public ViewModel(@NonNull Application application) {
        super(application);
        prefs = application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        repository = Repository.getInstance(prefs);
    }

    public SharedPreferences getPrefs() {
        return prefs;
    }

    public LiveData<ArrayList<List>> getListing(String id, String token) {
        if (!checkInternetConnection(getApplication())) return null;
        return repository.getListing(id, token);
    }

    public LiveData<Login> login(String username, String password) {
        if (!checkInternetConnection(getApplication())) return null;
        return repository.login(username, password);
    }

    public LiveData<Boolean> updateList(String id, String listName, String distance) {
        if (!checkInternetConnection(getApplication())) return null;
        return repository.updateList(id, listName, distance);
    }

    public static boolean checkInternetConnection(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            return true;
        } else {
            return false;
        }
    }
}
