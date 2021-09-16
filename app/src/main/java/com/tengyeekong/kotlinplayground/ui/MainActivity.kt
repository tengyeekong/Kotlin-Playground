package com.tengyeekong.kotlinplayground.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler

import com.tengyeekong.kotlinplayground.R
import com.tengyeekong.kotlinplayground.viewmodel.ListingViewModel

import javax.inject.Inject

import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import dagger.android.support.DaggerAppCompatActivity

class MainActivity : DaggerAppCompatActivity() {

    @Inject lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
