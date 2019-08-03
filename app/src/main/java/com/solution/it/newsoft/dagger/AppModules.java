package com.solution.it.newsoft.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.solution.it.newsoft.BuildConfig;
import com.solution.it.newsoft.datasource.Repository;
import com.solution.it.newsoft.viewmodel.ListingViewModel;
import com.solution.it.newsoft.viewmodel.ListingViewModelFactory;
import com.solution.it.newsoft.api.ApiService;
import com.solution.it.newsoft.api.RetrofitClientInstance;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;

@Module
abstract class AppModules {

    @Singleton
    @Binds
    abstract Context bindContext(Application application);

    @Singleton
    @Provides
    static SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

    @Singleton
    @Provides
    static ApiService provideApiService() {
        return RetrofitClientInstance.getApiService();
    }

    @Singleton
    @Provides
    static Repository provideRepository(ApiService service, SharedPreferences prefs) {
        return new Repository(service, prefs);
    }

    @Binds
    @IntoMap
    @ViewModelKey(ListingViewModel.class)
    abstract ViewModel bindUserViewModel(ListingViewModel listingViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ListingViewModelFactory factory);
}
