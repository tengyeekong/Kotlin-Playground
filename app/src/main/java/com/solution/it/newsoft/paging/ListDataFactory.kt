package com.solution.it.newsoft.paging

import com.solution.it.newsoft.datasource.Repository

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.solution.it.newsoft.model.List
import javax.inject.Inject

class ListDataFactory @Inject constructor(private val repository: Repository) : DataSource.Factory<Long, List>() {

    val mutableLiveData: MutableLiveData<ListDataSource> = MutableLiveData()
    private lateinit var feedDataSource: ListDataSource

    override fun create(): ListDataSource {
        feedDataSource = ListDataSource(repository)
        mutableLiveData.postValue(feedDataSource)
        return feedDataSource
    }

    fun reload() = feedDataSource.invalidate()

    fun retry() = feedDataSource.retry()
}
