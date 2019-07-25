package com.solution.it.newsoft;

import android.content.SharedPreferences;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class ListDataFactory extends DataSource.Factory {

    private MutableLiveData<ListDataSource> mutableLiveData;
    private ListDataSource feedDataSource;
    private SharedPreferences prefs;

    public ListDataFactory(SharedPreferences prefs) {
        this.prefs = prefs;
        this.mutableLiveData = new MutableLiveData<>();
    }

    @Override
    public ListDataSource create() {
        feedDataSource = new ListDataSource(prefs);
        mutableLiveData.postValue(feedDataSource);
        return feedDataSource;
    }

    public void reload() {
        if (feedDataSource != null)
            feedDataSource.invalidate();
    }


    public MutableLiveData<ListDataSource> getMutableLiveData() {
        return mutableLiveData;
    }
}
