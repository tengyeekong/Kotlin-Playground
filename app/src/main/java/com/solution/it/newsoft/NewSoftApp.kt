package com.solution.it.newsoft

import android.app.Application
import com.solution.it.newsoft.koin.*

import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class NewSoftApp : Application() {

    override fun onCreate() {
        super.onCreate()
        setupKoin()
    }

    private fun setupKoin() {
        startKoin {
            androidLogger()
            androidContext(this@NewSoftApp)
            modules(listOf(
                    appModule,
                    listingModule
            ))
        }
    }
}
