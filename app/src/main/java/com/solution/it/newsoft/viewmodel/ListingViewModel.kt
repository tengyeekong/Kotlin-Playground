package com.solution.it.newsoft.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList

import com.solution.it.newsoft.datasource.Repository
import com.solution.it.newsoft.model.List
import com.solution.it.newsoft.model.Login
import com.solution.it.newsoft.model.NetworkState
import com.solution.it.newsoft.paging.ListDataFactory
import com.solution.it.newsoft.paging.ListDataSource

import java.util.concurrent.Executor
import java.util.concurrent.Executors

class ListingViewModel
@ViewModelInject constructor(private val repository: Repository, private val listDataFactory: ListDataFactory, @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val networkState: LiveData<NetworkState>
    val listLiveData: LiveData<PagedList<List>>

    init {
        val executor = Executors.newFixedThreadPool(5)

        networkState = Transformations.switchMap(listDataFactory.mutableLiveData, ListDataSource::getNetworkState)

        val pagedListConfig = PagedList.Config.Builder()
                .setPageSize(10)
                .setInitialLoadSizeHint(20)
                .setEnablePlaceholders(false)
                .build()

        listLiveData = LivePagedListBuilder(listDataFactory, pagedListConfig)
                .setFetchExecutor(executor)
                .build()
    }

    fun reload() {
        listDataFactory.reload()
    }

    fun retry() {
        listDataFactory.retry()
    }

    //    public LiveData<ArrayList<List>> getListing(String id, String token) {
    //        return repository.getListing(id, token);
    //    }

    fun login(username: String, password: String): LiveData<Login> {
        return repository.login(username, password)
    }

    fun updateList(id: String, listName: String, distance: String): LiveData<Boolean> {
        return repository.updateList(id, listName, distance)
    }

    companion object {
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val ID = "id"
        const val TOKEN = "token"
    }
}
