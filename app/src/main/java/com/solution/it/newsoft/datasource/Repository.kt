package com.solution.it.newsoft.datasource

import android.content.SharedPreferences

import com.solution.it.newsoft.api.ApiService
import com.solution.it.newsoft.model.List
import com.solution.it.newsoft.model.Login
import com.solution.it.newsoft.viewmodel.ListingViewModel

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import java.util.*
import javax.inject.Inject

class Repository(private val service: ApiService, private val prefs: SharedPreferences) {

    init {
        id = prefs.getString(ListingViewModel.ID, "") ?: ""
        token = prefs.getString(ListingViewModel.TOKEN, "") ?: ""
    }

    suspend fun getListing(): ArrayList<List>? {
        val apiResponse = service.getListing(id, token)
        val responseBody = apiResponse.body()

        return if (apiResponse.isSuccessful && responseBody?.status?.code == "200") {
            responseBody.listing
        } else if (responseBody?.status?.code == "400") {
            if (relogin()) {
                val apiResponse2 = service.getListing(id, token)
                val responseBody2 = apiResponse2.body()
                if (apiResponse2.isSuccessful && responseBody2?.status?.code == "200") {
                    responseBody2.listing
                } else null
            } else null
        } else null
    }

    suspend fun getDummies(itemCount: Int): ArrayList<List> {
        val lists = ArrayList<List>()
        var count = 10000 + itemCount
        for (i in 0..9 /*&& count < 10105*/) {
            val value = (++count).toString()
            val list = List(value, value, value)
            lists.add(list)
        }
        delay(1000)
        return lists
    }

    fun login(username: String, password: String): Flow<Login?> {
        return flow<Login?>
        {
            val apiResponse = service.login(username, password)
            val responseBody = apiResponse.body()
            if (responseBody?.status?.code == "200") {
                storeLoginData(responseBody, username, password)
                emit(responseBody)
            } else {
                emit(null)
            }
        }.flowOn(Dispatchers.IO)
    }

    fun updateList(listing_id: String, listing_name: String, distance: String): Flow<Boolean> {
        return flow<Boolean>
        {
            val apiResponse = service.updateList(id, token, listing_id, listing_name, distance)
            val responseBody = apiResponse.body()
            if (apiResponse.isSuccessful && responseBody?.status?.code == "200") {
                emit(true)
            } else if (responseBody?.status?.code == "400") {
                if (relogin()) {
                    val apiResponse3 = service.updateList(id, token, listing_id, listing_name, distance)
                    val responseBody3 = apiResponse3.body()
                    if (apiResponse3.isSuccessful && responseBody3?.status?.code == "200") {
                        emit(true)
                    } else emit(false)
                } else emit(false)
            } else emit(false)
        }.flowOn(Dispatchers.IO)
    }

    private suspend fun relogin(): Boolean {
        //can't use retrofit interceptor in this case to get new token
        //since the token is not passing through header
        val username = prefs.getString(ListingViewModel.USERNAME, "") ?: ""
        val password = prefs.getString(ListingViewModel.PASSWORD, "") ?: ""
        val apiResponse = service.login(username, password)
        val responseBody = apiResponse.body()

        return if (responseBody?.status?.code == "200") {
            storeLoginData(responseBody, username, password)
            true
        } else {
            false
        }
    }

    private fun storeLoginData(responseBody: Login, username: String, password: String) {
        id = responseBody.id
        token = responseBody.token
        prefs.edit().putString(ListingViewModel.USERNAME, username)
                .putString(ListingViewModel.PASSWORD, password)
                .putString(ListingViewModel.ID, id)
                .putString(ListingViewModel.TOKEN, token)
                .apply()
    }

    companion object {
        private var id: String = ""
        /*username, password,*/ private var token: String = ""
    }
}
