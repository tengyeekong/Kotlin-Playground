package com.solution.it.newsoft

import com.solution.it.newsoft.dagger.DaggerAppComponent

import javax.inject.Singleton

import dagger.android.AndroidInjector
import dagger.android.DaggerApplication

@Singleton
class NewSoftApp : DaggerApplication() {

    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerAppComponent.builder().application(this).build()
    }
}
