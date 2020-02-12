package com.solution.it.newsoft.koin

import android.content.Context
import com.solution.it.newsoft.BuildConfig
import com.solution.it.newsoft.api.RetrofitClientInstance
import com.solution.it.newsoft.datasource.Repository
import com.solution.it.newsoft.paging.ListDataFactory
import com.solution.it.newsoft.paging.ListingAdapter
import com.solution.it.newsoft.viewmodel.ListingViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    single { RetrofitClientInstance.apiService }
    single { androidContext().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE) }
    single { Repository(service = get(), prefs = get()) }
}

val listingModule = module {
    factory { ListingAdapter() }
    factory { ListDataFactory(repository = get()) }
    viewModel { ListingViewModel(repository = get(), listDataFactory = get()) }
}