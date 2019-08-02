package com.solution.it.newsoft.dagger;

import com.solution.it.newsoft.ListingActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityModules {

    @ContributesAndroidInjector
    abstract ListingActivity contributeListingActivity();
}
