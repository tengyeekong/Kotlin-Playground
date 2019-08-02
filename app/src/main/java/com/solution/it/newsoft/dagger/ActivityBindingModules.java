package com.solution.it.newsoft.dagger;

import com.solution.it.newsoft.ui.ListingActivity;
import com.solution.it.newsoft.ui.LoginActivity;
import com.solution.it.newsoft.ui.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
abstract class ActivityBindingModules {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract ListingActivity contributeListingActivity();
}
