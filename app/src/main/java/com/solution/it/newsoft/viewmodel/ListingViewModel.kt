package com.solution.it.newsoft.viewmodel

import androidx.lifecycle.*
import androidx.paging.*

import com.solution.it.newsoft.datasource.Repository
import com.solution.it.newsoft.model.List
import com.solution.it.newsoft.model.Login
import com.solution.it.newsoft.model.NetworkState
import com.solution.it.newsoft.paging.ListDataSource

import java.util.concurrent.Executors

import javax.inject.Inject

class ListingViewModel @Inject
internal constructor(private val repository: Repository) : ViewModel() {

    val flow = Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(pageSize = 10, initialLoadSize = 20, enablePlaceholders = false)
    ) {
        ListDataSource(repository)
    }.flow.cachedIn(viewModelScope)

    fun login(username: String, password: String): LiveData<Login?> = repository.login(username, password).asLiveData(viewModelScope.coroutineContext)

    fun updateList(id: String, listName: String, distance: String): LiveData<Boolean> = repository.updateList(id, listName, distance).asLiveData(viewModelScope.coroutineContext)

    companion object {
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val ID = "id"
        const val TOKEN = "token"
    }
}
