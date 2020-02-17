package com.solution.it.newsoft.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity

import com.solution.it.newsoft.R
import com.solution.it.newsoft.viewmodel.ListingViewModel

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.solution.it.newsoft.koin.injectFeature
import org.koin.android.ext.android.inject

class MainActivity : AppCompatActivity() {

    private val prefs by inject<SharedPreferences>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        injectFeature()

        DataBindingUtil.setContentView<ViewDataBinding>(this, R.layout.activity_main)

        Handler().postDelayed({
            if (!prefs.getString(ListingViewModel.USERNAME, "").isNullOrEmpty()) {
                val intent = Intent(this, ListingActivity::class.java)
                startActivity(intent)
            } else {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            }
        }, 2000)
    }
}
