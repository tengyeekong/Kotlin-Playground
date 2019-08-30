package com.solution.it.newsoft.datasource

import android.annotation.SuppressLint
import android.content.SharedPreferences

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PageKeyedDataSource

import com.solution.it.newsoft.api.ApiService
import com.solution.it.newsoft.model.List
import com.solution.it.newsoft.model.Listing
import com.solution.it.newsoft.model.Login
import com.solution.it.newsoft.model.NetworkState
import com.solution.it.newsoft.model.UpdateStatus
import com.solution.it.newsoft.viewmodel.ListingViewModel

import java.io.IOException
import java.util.ArrayList
import java.util.concurrent.TimeUnit

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Repository(private val service: ApiService, private val prefs: SharedPreferences) {

    init {
        id = prefs.getString(ListingViewModel.ID, "") ?: ""
        token = prefs.getString(ListingViewModel.TOKEN, "") ?: ""
    }

    fun getListing(initCallback: PageKeyedDataSource.LoadInitialCallback<Long, List>?,
                   afterCallback: PageKeyedDataSource.LoadCallback<Long, List>?,
                   networkState: MutableLiveData<NetworkState>, nextKey: Long) {
        val call = service.getListing(id, token)
        try {
            val listing = call.execute().body()
            if (listing != null) {
                if (listing.status?.code == "200") {
                    if (initCallback != null)
                        initCallback.onResult(listing.listing, null, nextKey)
                    else
                        afterCallback?.onResult(listing.listing, nextKey)
                    networkState.postValue(NetworkState.LOADED)
                } else if (listing.status?.code == "400") {
                    //can't use retrofit interceptor in this case to get new token
                    //since the token is not passing through header
                    val username = prefs.getString(ListingViewModel.USERNAME, "") ?: ""
                    val password = prefs.getString(ListingViewModel.PASSWORD, "") ?: ""
                    val loginCall = service.login(username, password)
                    val login = loginCall.execute().body()
                    if (login != null) {
                        if (login.status?.code == "200") {
                            val id = login.id
                            val token = login.token
                            prefs.edit().putString(ListingViewModel.USERNAME, username)
                                    .putString(ListingViewModel.PASSWORD, password)
                                    .putString(ListingViewModel.ID, id)
                                    .putString(ListingViewModel.TOKEN, token)
                                    .apply()

                            val listingCall = service.getListing(id, token)
                            val listing2 = listingCall.execute().body()
                            if (listing2 != null) {
                                if (listing2.status?.code == "200") {
                                    if (initCallback != null)
                                        initCallback.onResult(listing2.listing, null, nextKey)
                                    else
                                        afterCallback?.onResult(listing2.listing, nextKey)
                                    networkState.postValue(NetworkState.LOADED)
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            networkState.postValue(NetworkState.FAILED)
        }

    }

    @SuppressLint("CheckResult")
    fun getDummies(itemCount: Int, callback: PageKeyedDataSource.LoadCallback<Long, List>,
                   networkState: MutableLiveData<NetworkState>, nextKey: Long) {
        Observable.fromCallable {
            val lists = ArrayList<List>()
            var count = 10000 + itemCount
            for (i in 0..9 /*&& count < 10105*/) {
                val value = (++count).toString()
                val list = List(value, value, value)
                lists.add(list)
            }
            lists
        }
                .delay(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ lists ->
                    if (lists.size > 0) callback.onResult(lists, nextKey)
                    networkState.postValue(NetworkState.LOADED)
                }, { throwable -> throwable.printStackTrace() })
    }

    //    public LiveData<ArrayList<List>> getListing(String id, String token) {
    //        MutableLiveData<ArrayList<List>> listingData = new MutableLiveData<>();
    //        service.getListing(id, token).enqueue(new Callback<Listing>() {
    //            @Override
    //            public void onResponse(Call<Listing> call, Response<Listing> response) {
    //                if (response.isSuccessful()) {
    //                    if (response.body().getStatus().getCode().equals("200")) {
    //                        listingData.setValue(response.body().getListing());
    //                        return;
    //                    }
    //                }
    //                listingData.setValue(null);
    //            }
    //
    //            @Override
    //            public void onFailure(Call<Listing> call, Throwable t) {
    //                listingData.setValue(null);
    //            }
    //        });
    //        return listingData;
    //    }

    fun login(username: String, password: String): LiveData<Login> {
        val loginData = MutableLiveData<Login>()
        service.login(username, password).enqueue(object : Callback<Login> {
            override fun onResponse(call: Call<Login>, response: Response<Login>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody?.status?.code == "200") {
                        id = responseBody.id
                        token = responseBody.token
                        prefs.edit().putString(ListingViewModel.USERNAME, username)
                                .putString(ListingViewModel.PASSWORD, password)
                                .putString(ListingViewModel.ID, id)
                                .putString(ListingViewModel.TOKEN, token)
                                .apply()
                    }
                    loginData.value = responseBody
                }
            }

            override fun onFailure(call: Call<Login>, t: Throwable) {
                loginData.value = null
            }
        })
        return loginData
    }

    fun updateList(listing_id: String, listing_name: String, distance: String): LiveData<Boolean> {
        val isUpdated = MutableLiveData<Boolean>()
        service.updateList(id, token, listing_id, listing_name, distance).enqueue(object : Callback<UpdateStatus> {
            override fun onResponse(call: Call<UpdateStatus>, response: Response<UpdateStatus>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        if (responseBody.status?.code == "200") {
                            isUpdated.value = true
                            return
                        } else if (responseBody.status?.code == "400") {
                            //can't use retrofit interceptor in this case to get new token
                            //since the token is not passing through header
                            val username = prefs.getString(ListingViewModel.USERNAME, "") ?: ""
                            val password = prefs.getString(ListingViewModel.PASSWORD, "") ?: ""
                            service.login(username, password).enqueue(object : Callback<Login> {
                                override fun onResponse(call: Call<Login>, response: Response<Login>) {
                                    if (response.isSuccessful) {
                                        val responseBody = response.body()
                                        if (responseBody != null) {
                                            if (responseBody.status != null && responseBody.status?.code == "200") {
                                                val id = responseBody.id
                                                val token = responseBody.token
                                                prefs.edit().putString(ListingViewModel.USERNAME, username)
                                                        .putString(ListingViewModel.PASSWORD, password)
                                                        .putString(ListingViewModel.ID, id)
                                                        .putString(ListingViewModel.TOKEN, token)
                                                        .apply()

                                                service.updateList(id, token, listing_id, listing_name, distance).enqueue(object : Callback<UpdateStatus> {
                                                    override fun onResponse(call: Call<UpdateStatus>, response: Response<UpdateStatus>) {
                                                        if (response.isSuccessful) {
                                                            if (response.body() != null) {
                                                                if (response.body()?.status?.code == "200") {
                                                                    isUpdated.value = true
                                                                    return
                                                                }
                                                            }
                                                        }
                                                        isUpdated.value = false
                                                    }

                                                    override fun onFailure(call: Call<UpdateStatus>, t: Throwable) {
                                                        isUpdated.value = false
                                                    }
                                                })
                                            }
                                        }
                                    }
                                }

                                override fun onFailure(call: Call<Login>, t: Throwable) {}
                            })
                        }
                    }
                }
                isUpdated.value = false
            }

            override fun onFailure(call: Call<UpdateStatus>, t: Throwable) {
                isUpdated.value = false
            }
        })
        return isUpdated
    }

    companion object {
        private var id: String = ""
        /*username, password,*/ private var token: String = ""
    }
}
