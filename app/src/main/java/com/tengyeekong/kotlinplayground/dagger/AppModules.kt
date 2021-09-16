package com.tengyeekong.kotlinplayground.dagger

import android.app.Application
import android.content.Context
import android.content.SharedPreferences

import com.tengyeekong.kotlinplayground.BuildConfig
import com.tengyeekong.kotlinplayground.datasource.Repository
import com.tengyeekong.kotlinplayground.viewmodel.ListingViewModel
import com.tengyeekong.kotlinplayground.viewmodel.ListingViewModelFactory
import com.tengyeekong.kotlinplayground.api.ApiService
import com.tengyeekong.kotlinplayground.api.RetrofitClientInstance

import javax.inject.Singleton

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.multibindings.IntoMap

@Module
internal abstract class AppModules {

    @Singleton
    @Binds
    internal abstract fun bindContext(application: Application): Context

    @Binds
    @IntoMap
    @ViewModelKey(ListingViewModel::class)
    internal abstract fun bindUserViewModel(listingViewModel: ListingViewModel): ViewModel

    @Binds
    internal abstract fun bindViewModelFactory(factory: ListingViewModelFactory): ViewModelProvider.Factory

    companion object {

        @Singleton
        @Provides
        fun provideSharedPreferences(context: Context): SharedPreferences {
            return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
        }

        @Singleton
        @Provides
        fun provideApiService(): ApiService {
            return RetrofitClientInstance.apiService
        }

        @Singleton
        @Provides
        fun provideRepository(service: ApiService, prefs: SharedPreferences): Repository {
            return Repository(service, prefs)
        }
    }
}
