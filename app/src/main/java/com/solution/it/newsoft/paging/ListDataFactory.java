package com.solution.it.newsoft.paging;

import com.solution.it.newsoft.datasource.Repository;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import androidx.paging.DataSource;

public class ListDataFactory extends DataSource.Factory {

    private MutableLiveData<ListDataSource> mutableLiveData;
    private ListDataSource feedDataSource;
    private Repository repository;

    @Inject
    public ListDataFactory(Repository repository) {
        this.repository = repository;
        this.mutableLiveData = new MutableLiveData<>();
    }

    @Override
    public ListDataSource create() {
        feedDataSource = new ListDataSource(repository);
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
