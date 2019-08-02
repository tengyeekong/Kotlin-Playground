package com.solution.it.newsoft.dagger;

import com.solution.it.newsoft.NewSoftApp;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

@Singleton
@Component(modules = {
        AndroidSupportInjectionModule.class,
        ActivityModules.class,
        ListingModules.class
})
public interface AppComponent extends AndroidInjector<NewSoftApp> {
}
