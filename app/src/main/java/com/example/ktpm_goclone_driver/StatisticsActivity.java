package com.example.ktpm_goclone_driver;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


public class StatisticsActivity extends AppCompatActivity {

    ImageView back;
    TextView total, now, yesterday;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        loadFragment(new StatisticsFragment("0"));

        back = findViewById(R.id.imageView);
        total = findViewById(R.id.total);
        now = findViewById(R.id.now);
        yesterday = findViewById(R.id.yesterday);

        back.setOnClickListener(v -> {
            finish();
        });

        total.setOnClickListener(v -> {
            loadFragment(new StatisticsFragment("0"));
            total.setTextColor(Color.parseColor("#FF000000"));
            now.setTextColor(Color.parseColor("#B2AFAF"));
            yesterday.setTextColor(Color.parseColor("#B2AFAF"));
        });
        now.setOnClickListener(v -> {
            loadFragment(new StatisticsFragment("1"));
            now.setTextColor(Color.parseColor("#FF000000"));
            total.setTextColor(Color.parseColor("#B2AFAF"));
            yesterday.setTextColor(Color.parseColor("#B2AFAF"));
        });
        yesterday.setOnClickListener(v -> {
            loadFragment(new StatisticsFragment("-1"));
            yesterday.setTextColor(Color.parseColor("#FF000000"));
            now.setTextColor(Color.parseColor("#B2AFAF"));
            total.setTextColor(Color.parseColor("#B2AFAF"));
        });

    }

    void loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }



}