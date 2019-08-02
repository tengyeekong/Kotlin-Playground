package com.solution.it.newsoft.dagger;

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

@Module
public abstract class ListingModules {

//    @Singleton
//    @Provides
//    public static SharedPreferences provideSharedPreferences(Context context) {
//        return context.getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
//    }

//    @Singleton
//    @Provides
//    public static Context bindContext(Application application) {
//        return application.getApplicationContext();
//    };

    @Singleton
    @Provides
    public static ApiService provideApiService() {
        return RetrofitClientInstance.getRetrofitInstance().create(ApiService.class);
    }

    @Binds
    @IntoMap
    @ViewModelKey(com.solution.it.newsoft.ViewModel.class)
    abstract ViewModel bindUserViewModel(com.solution.it.newsoft.ViewModel viewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
