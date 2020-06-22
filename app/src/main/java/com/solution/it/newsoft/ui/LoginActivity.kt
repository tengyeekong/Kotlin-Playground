package com.solution.it.newsoft.ui

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer

import com.solution.it.newsoft.R
import com.solution.it.newsoft.databinding.ActivityLoginBinding
import com.solution.it.newsoft.viewmodel.ListingViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val listingViewModel: ListingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(false)
        supportActionBar?.setDisplayShowHomeEnabled(false)

        binding.btnLogin.setOnClickListener {
            if (binding.etUsername.text.toString().isEmpty() || binding.etPassword.text.toString().isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_LONG).show()
            } else {
                val progress = ProgressDialog.show(this, "", "Loading...", true)
                progress.show()
                listingViewModel.login(binding.etUsername.text.toString(), binding.etPassword.text.toString()).observe(this, Observer { login ->
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
