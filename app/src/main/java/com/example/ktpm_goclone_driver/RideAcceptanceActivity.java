package com.example.ktpm_goclone_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

public class RideAcceptanceActivity extends AppCompatActivity {
    TextView cusName, srcName, srcDes, destName, destDes, price;
    Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_acceptance);

        cusName = findViewById(R.id.cusName);
        srcName = findViewById(R.id.srcName);
        srcDes = findViewById(R.id.srcDes);
        destName = findViewById(R.id.destName);
        destDes = findViewById(R.id.destDes);
        price = findViewById(R.id.price);
        btn = findViewById(R.id.button);


        Intent intent = getIntent();
        if (intent != null) {
            String customerName = intent.getStringExtra("customerName");
            String sourceAddress = intent.getStringExtra("sourceAddress");
            String desAddress = intent.getStringExtra("desAddress");
            String priceIntent = intent.getStringExtra("price");

            cusName.setText(customerName);
            srcName.setText(sourceAddress);
            destName.setText(desAddress);
            price.setText(priceIntent);

        }

        btn.setOnClickListener(view -> {
            Intent i = new Intent(this, RideFinishActivity.class);
            startActivity(i);
            finish();
        });
    }
}