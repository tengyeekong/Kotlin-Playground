package com.solution.it.newsoft.dagger;

import com.solution.it.newsoft.ListingActivity;
import com.solution.it.newsoft.LoginActivity;
import com.solution.it.newsoft.MainActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModules {

    @ContributesAndroidInjector
    abstract MainActivity contributeMainActivity();

    @ContributesAndroidInjector
    abstract LoginActivity contributeLoginActivity();

    @ContributesAndroidInjector
    abstract ListingActivity contributeListingActivity();
}
