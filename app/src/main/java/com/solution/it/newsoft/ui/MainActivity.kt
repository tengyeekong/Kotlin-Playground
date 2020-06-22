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
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

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
