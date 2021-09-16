package com.tengyeekong.kotlinplayground.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*

import com.tengyeekong.kotlinplayground.datasource.Repository
import com.tengyeekong.kotlinplayground.model.Login
import com.tengyeekong.kotlinplayground.paging.ListDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi

@ExperimentalCoroutinesApi
class ListingViewModel
@ViewModelInject constructor(private val repository: Repository, @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {

    val flow = Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(pageSize = 10, initialLoadSize = 20, enablePlaceholders = false)
    ) {
        ListDataSource(repository)
    }.flow.cachedIn(viewModelScope)

    fun login(username: String, password: String): LiveData<Login> = repository.login(username, password).asLiveData(viewModelScope.coroutineContext)

    fun updateList(id: String, listName: String, distance: String): LiveData<Boolean> = repository.updateList(id, listName, distance).asLiveData(viewModelScope.coroutineContext)

    companion object {
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val ID = "id"
        const val TOKEN = "token"
    }
}
