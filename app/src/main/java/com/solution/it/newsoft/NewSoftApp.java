package com.solution.it.newsoft;

import com.solution.it.newsoft.dagger.DaggerAppComponent;

import javax.inject.Singleton;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

@Singleton
public class NewSoftApp extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
