package com.example.ktpm_goclone_driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements WebsocketConnector.BookingNotificationListener {

    BottomNavigationView navigationView;
    HomeFragment homeFragment;
    PromoFragment promoFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WebsocketConnector websocketConnector = WebsocketConnector.getInstance();
        websocketConnector.setBookingNotificationListener(this);

        navigationView = findViewById(R.id.bottom_navigation);
        if (homeFragment == null){
            homeFragment = new HomeFragment();
        }
        getSupportFragmentManager().beginTransaction().replace(R.id.body_container, homeFragment).commit();
        navigationView.setSelectedItemId(R.id.homeFragment);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment fragment = null;
                if (item.getItemId() == R.id.homeFragment) {
                    if (homeFragment == null){
                        homeFragment = new HomeFragment();
                    }
                    fragment = homeFragment;
                } else if (item.getItemId() == R.id.promo_fragment) {
                    if (promoFragment == null){
                        promoFragment = new PromoFragment();
                    }
                    fragment = promoFragment;
                } else if (item.getItemId() == R.id.profile) {
                    fragment = new ProfileFragment();
                } else if (item.getItemId() == R.id.chat) {
                    fragment = new ChatFragment();
                }
//                switch (item.getItemId()){
//                    case R.id.homeFragment:
//                        fragment = new HomeFragment();
//                        break;
//
//                    case R.id.promo_fragment:
//                        fragment = new PromoFragment();
//                        break;
//
//                    case R.id.riwayat:
//                        fragment = new HistoryFragment();
//                        break;
//
//                    case R.id.chat:
//                        fragment = new ChatFragment();
//                        break;
//                }
                getSupportFragmentManager().beginTransaction().replace(R.id.body_container, fragment).commit();

                return true;
            }
        });
    }

    @Override
    public void onBookingNotificationReceived(JSONObject bookingData) {
        try {
            showBottomSheet(bookingData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void showBottomSheet(JSONObject bookingData) throws JSONException {
        Log.e("Hello", bookingData.toString());

        String customerName = bookingData.getString("senderID");
        String sourceLat = bookingData.getString("latitude");
        String sourceLng = bookingData.getString("longitude");
        String desLat = bookingData.getString("desLat");
        String desLng = bookingData.getString("desLng");
        String price = bookingData.getString("message");
        Log.e("Hello", "Hello map u");
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        String sourceAddress = "", desAddress = "";
        try {
            List<Address> addresses = geocoder.getFromLocation(Double.valueOf(sourceLat), Double.valueOf(sourceLng), 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                sourceAddress = address.getAddressLine(0);
            }
            addresses = geocoder.getFromLocation(Double.valueOf(desLat), Double.valueOf(desLng), 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                desAddress = address.getAddressLine(0);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        RideRequestBottomSheet bottomSheet = new RideRequestBottomSheet(customerName, sourceAddress, desAddress, price);

        bottomSheet.show(getSupportFragmentManager(), bottomSheet.getTag());
        String finalSourceAddress = sourceAddress;
        String finalDesAddress = desAddress;
        bottomSheet.setCallback((message -> {
            if (message == "accept") {
                Intent intent = new Intent(this, RideAcceptanceActivity.class);
                intent.putExtra("customerName", customerName);
                intent.putExtra("sourceAddress", finalSourceAddress);
                intent.putExtra("desAddress", finalDesAddress);
                intent.putExtra("price", price);

                startActivity(intent);
            }
        }));

    }
}