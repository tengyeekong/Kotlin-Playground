package com.solution.it.newsoft.paging

import com.solution.it.newsoft.datasource.Repository
import com.solution.it.newsoft.model.List
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.solution.it.newsoft.model.NetworkState
import kotlinx.coroutines.*

class ListDataSource internal constructor(private val repository: Repository) : PagingSource<Int, List>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, List> {

        val nextPageNumber = params.key ?: 1
        val itemCount = nextPageNumber * 10
        if (nextPageNumber == 1) {
            val list = repository.getListing()
            return if (!list.isNullOrEmpty()) {
                LoadResult.Page(
                        data = list,
                        prevKey = null,
                        nextKey = nextPageNumber + 1
                )
            } else {
                LoadResult.Page(
                        data = ArrayList(),
                        prevKey = null,
                        nextKey = null
                )
            }
        } else {
            val list = repository.getDummies(itemCount)
            return if (list.isNotEmpty()) {
                LoadResult.Page(
                        data = list,
                        prevKey = null,
                        nextKey = nextPageNumber + 1
                )
            } else {
                LoadResult.Page(
                        data = ArrayList(),
                        prevKey = null,
                        nextKey = null
                )
            }
        }
    }

    companion object {
        private val TAG = ListDataSource::class.java.simpleName
    }

    override fun getRefreshKey(state: PagingState<Int, List>): Int? {
        return null
    }
}
