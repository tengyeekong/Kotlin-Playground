package com.solution.it.newsoft;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_main);

        new Handler().postDelayed(() -> {
            SharedPreferences prefs = getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE);
            if (!prefs.getString(ViewModel.USERNAME, "").isEmpty()) {
                Intent intent = new Intent(MainActivity.this, ListingActivity.class);
                startActivity(intent);
            } else {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        }, 2000);
    }
}
