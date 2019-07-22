package com.solution.it.newsoft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import com.solution.it.newsoft.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        SharedPreferences prefs = viewModel.getPrefs();
        if (!prefs.getString(ViewModel.USERNAME, "").isEmpty()) {
            //jump to listing
        }

        binding.login.setOnClickListener(v -> {
            if (binding.username.getText().toString().isEmpty()
                    || binding.password.getText().toString().isEmpty()) {
                Toast.makeText(this, "Please enter username and password", Toast.LENGTH_LONG).show();
            } else {
                Bundle bundle = viewModel.login(binding.username.getText().toString(), binding.password.getText().toString());
                Toast.makeText(this, bundle.getString(ViewModel.MESSAGE), Toast.LENGTH_LONG).show();
                if (bundle.getBoolean(ViewModel.IS_SUCCESS)) {
                    Intent intent = new Intent(MainActivity.this, ListingActivity.class);
                    startActivity(intent);
                }
            }
        });

    }
}
