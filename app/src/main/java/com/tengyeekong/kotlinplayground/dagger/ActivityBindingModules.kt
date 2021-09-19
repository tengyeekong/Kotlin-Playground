package com.tengyeekong.kotlinplayground.dagger

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.ui.ExperimentalComposeUiApi
import com.tengyeekong.kotlinplayground.ui.ListingActivity
import com.tengyeekong.kotlinplayground.ui.LoginActivity
import com.tengyeekong.kotlinplayground.ui.MainActivity

import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
internal abstract class ActivityBindingModules {

    @ContributesAndroidInjector
    internal abstract fun contributeMainActivity(): MainActivity

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @ExperimentalFoundationApi
    @ContributesAndroidInjector
    internal abstract fun contributeLoginActivity(): LoginActivity

    @ExperimentalAnimationApi
    @ExperimentalComposeUiApi
    @ExperimentalFoundationApi
    @ContributesAndroidInjector
    internal abstract fun contributeListingActivity(): ListingActivity
}
