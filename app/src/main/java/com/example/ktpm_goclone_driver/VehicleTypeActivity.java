package com.example.ktpm_goclone_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

public class VehicleTypeActivity extends AppCompatActivity {

    RelativeLayout relativeLayout4Seated,relativeLayout7Seated;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vehicle_type);

        relativeLayout4Seated = findViewById(R.id.relativeLayout4Seated);
        relativeLayout7Seated = findViewById(R.id.relativeLayout7Seated);

        Intent preIntent = getIntent(); // get data from previous Activity
        String email = preIntent.getStringExtra("email");

        relativeLayout4Seated.setOnClickListener(view -> {
            Intent i = new Intent(VehicleTypeActivity.this, SignupInputDriverdataActivity.class);
            i.putExtra("email", email);
            i.putExtra("vehicleType", "4");
            startActivity(i);
        });

        relativeLayout7Seated.setOnClickListener(view -> {
            Intent i = new Intent(VehicleTypeActivity.this, SignupInputDriverdataActivity.class);
            i.putExtra("email", email);
            i.putExtra("vehicleType", "7");
            startActivity(i);
        });

    }
}