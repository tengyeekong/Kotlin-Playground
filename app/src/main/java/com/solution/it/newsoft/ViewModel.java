package com.solution.it.newsoft;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.solution.it.newsoft.model.Listing;
import com.solution.it.newsoft.model.Login;

class ViewModel extends AndroidViewModel {
    public static final String USERNAME = "username";
    public static final String PASSWORD = "password";
    public static final String ID = "id";
    public static final String TOKEN = "token";
    public static final String MESSAGE = "message";
    public static final String IS_SUCCESS = "isSuccess";
    private Repository repository;
    private MutableLiveData<Listing> listing;
    private SharedPreferences prefs;

    public ViewModel(@NonNull Application application) {
        super(application);
        prefs = application.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
        repository = Repository.getInstance();
    }
    public SharedPreferences getPrefs() {
        return prefs;
    }

    public LiveData<Listing> getListing(String id, String token) {
        if (listing != null) return null;
        listing = repository.getListing(id, token);
        return listing;
    }

    public Bundle login(String username, String password) {
        Bundle bundle = new Bundle();
        Login loginData = repository.login(username, password);
        if (loginData != null) {
            if (loginData.getStatus() != null && loginData.getStatus().getCode().equals("200")) {
                prefs.edit().putString(USERNAME, username)
                        .putString(PASSWORD, password)
                        .putString(ID, loginData.getId())
                        .putString(TOKEN, loginData.getToken())
                        .apply();
                bundle.putBoolean(IS_SUCCESS, true);
            }
            else bundle.putBoolean(IS_SUCCESS, false);

            bundle.putString(MESSAGE, loginData.getStatus().getMessage());
            return bundle;
        }
        bundle.putString(MESSAGE, "Please try again");
        bundle.putBoolean(IS_SUCCESS, false);
        return bundle;
    }
}
