package com.example.ktpm_goclone_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class SignupInputDriverdataActivity extends AppCompatActivity {

    Button buttonContinue, btnChangeVehicleType;
    ImageView imgVehicleType;
    TextView txtVehicleType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_input_driverdata);

        buttonContinue = findViewById(R.id.buttonContinue);
        btnChangeVehicleType = findViewById(R.id.btnChangeVehicleType);
        imgVehicleType = findViewById(R.id.imgVehicleType);
        txtVehicleType = findViewById(R.id.txtVehicleType);

        Intent preIntent = getIntent(); // get data from previous Activity
        String email = preIntent.getStringExtra("email");
        String vehicleType = preIntent.getStringExtra("vehicleType");

        if (Objects.equals(vehicleType, "7")) {
            imgVehicleType.setImageResource(R.drawable.ic_7_seated_car_logo);
            txtVehicleType.setText("Seven-wheeler car driver");
        } else {
            imgVehicleType.setImageResource(R.drawable.ic_4_seated_car_logo);
            txtVehicleType.setText("Four-wheeler car driver");
        }

        btnChangeVehicleType.setOnClickListener(view -> {
            finish();
        });

        buttonContinue.setOnClickListener(view -> {
            Intent i = new Intent(SignupInputDriverdataActivity.this, WelcomeActivity.class);
            startActivity(i);
            finishAffinity();
        });
    }
}