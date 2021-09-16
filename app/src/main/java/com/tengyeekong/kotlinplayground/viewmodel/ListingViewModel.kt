package com.tengyeekong.kotlinplayground.viewmodel

import androidx.lifecycle.*
import androidx.paging.*

import com.tengyeekong.kotlinplayground.datasource.Repository
import com.tengyeekong.kotlinplayground.model.Login
import com.tengyeekong.kotlinplayground.paging.ListDataSource

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
