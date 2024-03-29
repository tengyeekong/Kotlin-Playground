package com.tengyeekong.kotlinplayground.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import dagger.android.support.DaggerAppCompatActivity

import com.tengyeekong.kotlinplayground.R
import com.tengyeekong.kotlinplayground.databinding.ActivityLoginBinding
import com.tengyeekong.kotlinplayground.viewmodel.ListingViewModel

import javax.inject.Inject

class LoginActivity : DaggerAppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var listingViewModel: ListingViewModel

    @Inject lateinit var viewModelFactory: ViewModelProvider.Factory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // username: admin@advisoryapps.com
        // password: advisoryapps123

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        listingViewModel = ViewModelProvider(this, viewModelFactory).get(ListingViewModel::class.java)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.btnLogin.setOnClickListener {
            if (binding.etUsername.text.toString().isEmpty() || binding.etPassword.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_LONG).show()
            } else {
                val progress = ProgressDialog.show(this, "", "Loading...", true)
                progress.show()
                listingViewModel.login(binding.etUsername.text.toString(), binding.etPassword.text.toString()).observe(this, { login ->
                    if (login != null) {
                        if (!(login.status == null || login.status.code != "200")) {
                            val intent = Intent(this, ListingActivity::class.java)
                            startActivity(intent)
                        }
                        if (login.status != null)
                            Toast.makeText(this, login.status.message, Toast.LENGTH_LONG).show()
                    } else
                        Toast.makeText(this, "Please try again", Toast.LENGTH_LONG).show()
                    progress.cancel()
                })
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
    }
}
