package com.solution.it.newsoft;

import android.content.Context;
import android.content.SharedPreferences;

import com.solution.it.newsoft.dagger.DaggerAppComponent;

import javax.inject.Singleton;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

@Singleton
public class NewSoftApp extends DaggerApplication {
    public static SharedPreferences prefs;

    @Override
    public void onCreate() {
        super.onCreate();
        prefs = this.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.create();
    }
}
