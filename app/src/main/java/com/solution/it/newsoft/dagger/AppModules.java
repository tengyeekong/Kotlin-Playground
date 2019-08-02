package com.solution.it.newsoft.dagger;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import com.solution.it.newsoft.BuildConfig;
import com.solution.it.newsoft.ViewModelFactory;
import com.solution.it.newsoft.api.ApiService;
import com.solution.it.newsoft.api.RetrofitClientInstance;

import javax.inject.Singleton;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

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
    @ViewModelKey(com.solution.it.newsoft.ViewModel.class)
    abstract ViewModel bindUserViewModel(com.solution.it.newsoft.ViewModel viewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
