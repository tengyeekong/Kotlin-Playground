package com.solution.it.newsoft.dagger

import com.solution.it.newsoft.ui.ListingActivity
import com.solution.it.newsoft.ui.LoginActivity
import com.solution.it.newsoft.ui.MainActivity

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBindingModules {

    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun contributeLoginActivity(): LoginActivity

    @ContributesAndroidInjector
    internal abstract fun contributeListingActivity(): ListingActivity
}
