package com.solution.it.newsoft.viewmodel

import androidx.hilt.Assisted
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import androidx.paging.*

import com.solution.it.newsoft.datasource.Repository
import com.solution.it.newsoft.model.Login
import com.solution.it.newsoft.paging.ListDataSource
import kotlinx.coroutines.ExperimentalCoroutinesApi

import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
class ListingViewModel
@ViewModelInject constructor(private val repository: Repository, @Assisted private val savedStateHandle: SavedStateHandle) : ViewModel() {
    val flow = Pager(
            // Configure how data is loaded by passing additional properties to
            // PagingConfig, such as prefetchDistance.
            PagingConfig(pageSize = 10, initialLoadSize = 20, enablePlaceholders = false)
    ) {
        ListDataSource(repository)
    }.flow
            .cachedIn(viewModelScope)

    fun login(username: String, password: String): LiveData<Login> = repository.login(username, password).asLiveData(viewModelScope.coroutineContext)

    fun updateList(id: String, listName: String, distance: String): LiveData<Boolean> = repository.updateList(id, listName, distance).asLiveData(viewModelScope.coroutineContext)

    companion object {
        const val USERNAME = "username"
        const val PASSWORD = "password"
        const val ID = "id"
        const val TOKEN = "token"
    }
}
