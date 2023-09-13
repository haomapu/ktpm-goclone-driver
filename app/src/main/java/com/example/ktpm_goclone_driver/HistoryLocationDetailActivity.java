package com.example.ktpm_goclone_driver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class HistoryLocationDetailActivity extends AppCompatActivity {

    TextView destName, destDes, srcName, srcDes, price;
    ListHistoryLocation curLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_location_detail);

        destName = findViewById(R.id.destName);
        destDes = findViewById(R.id.destDes);
        srcDes = findViewById(R.id.srcDes);
        srcName = findViewById(R.id.srcName);
        price = findViewById(R.id.price);
        destDes.setText("");
        srcDes.setText("");
        if (getIntent().hasExtra("location")) {
            curLocation = (ListHistoryLocation) getIntent().getSerializableExtra("location");
            destName.setText(curLocation.getTitle());
            srcName.setText(curLocation.getDescript());
            price.setText(curLocation.getVehicleType());
        }

    }
}