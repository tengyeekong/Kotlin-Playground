package com.tengyeekong.kotlinplayground.dagger

import com.tengyeekong.kotlinplayground.ui.ListingActivity
import com.tengyeekong.kotlinplayground.ui.LoginActivity
import com.tengyeekong.kotlinplayground.ui.MainActivity

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
