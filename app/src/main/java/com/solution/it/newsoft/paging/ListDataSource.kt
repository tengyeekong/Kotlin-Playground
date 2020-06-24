package com.solution.it.newsoft.paging

import android.util.Log

import com.solution.it.newsoft.datasource.Repository
import com.solution.it.newsoft.model.List
import com.solution.it.newsoft.model.NetworkState
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.coroutines.*

@ExperimentalCoroutinesApi
class ListDataSource internal constructor(private val repository: Repository) : PageKeyedDataSource<Long, List>() {
    private val completableJob = Job()
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable -> throwable.printStackTrace() }
    private val coroutineScope = CoroutineScope(Dispatchers.IO + completableJob + exceptionHandler)
    private var params: LoadParams<Long>? = null
    private var callback: LoadCallback<Long, List>? = null

    private val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    fun getNetworkState() = networkState

    override fun loadInitial(params: LoadInitialParams<Long>,
                             callback: LoadInitialCallback<Long, List>) {

        networkState.postValue(NetworkState.LOADING)

        coroutineScope.launch {
            val list = repository.getListing()
            if (list == null) {
                networkState.postValue(NetworkState.FAILED)
            } else {
                networkState.postValue(NetworkState.LOADED)
                callback.onResult(list.toList(), null, 2L)
            }
        }
    }

    override fun loadBefore(params: LoadParams<Long>,
                            callback: LoadCallback<Long, List>) {

    }

    override fun loadAfter(params: LoadParams<Long>,
                           callback: LoadCallback<Long, List>) {

        this.params = params
        this.callback = callback

        Log.i(TAG, "Loading Page " + params.key + ", Count " + params.requestedLoadSize)

        networkState.postValue(NetworkState.LOADING)

        val nextKey = params.key + 1
        val itemCount = Integer.valueOf(params.key.toString()) * 10
        coroutineScope.launch {
            if (params.key > 1) {
                val list = repository.getDummies(itemCount)
                delay(1000)
                if (list.isNotEmpty()) {
                    callback.onResult(list, nextKey)
                }
                networkState.postValue(NetworkState.LOADED)
            } else {
                val list = repository.getListing()
                if (list == null) {
                    networkState.postValue(NetworkState.FAILED)
                } else {
                    networkState.postValue(NetworkState.LOADED)
                    callback.onResult(list.toList(), nextKey)
                }
            }
        }
    }

    fun retry() {
        Completable.fromAction { loadAfter(params!!, callback!!) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun clearCoroutineJobs() {
        completableJob.cancel()
    }

    companion object {
        private val TAG = ListDataSource::class.java.simpleName
    }
}
