package com.solution.it.newsoft.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.solution.it.newsoft.BuildConfig;
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
public abstract class AppModules {

    @Singleton
    @Binds
    abstract Context bindContext(Application application);

    @Singleton
    @Provides
    public static SharedPreferences provideSharedPreferences(Context context) {
        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
    }

    @Singleton
    @Provides
    public static ApiService provideApiService() {
        return RetrofitClientInstance.getApiService();
    }

    @Binds
    @IntoMap
    @ViewModelKey(ListingViewModel.class)
    abstract ViewModel bindUserViewModel(ListingViewModel listingViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ListingViewModelFactory factory);
}
