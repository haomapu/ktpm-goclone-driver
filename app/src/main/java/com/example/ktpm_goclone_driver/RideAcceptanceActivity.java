package com.example.ktpm_goclone_driver;

import static com.example.ktpm_goclone_driver.User.currentUser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RideAcceptanceActivity extends AppCompatActivity {
    TextView cusName, srcName, srcDes, destName, destDes, price, cusPhone;
    Button btn;
    String name;
    private Thread locationSendingThread;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    public String customerId;
    ImageView imageView4;

    String phoneNumber;
    String parts[];

    String id;
    private volatile boolean isLocationSending = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_acceptance);
        imageView4 = findViewById(R.id.imageView4);
        imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Getting instance of Intent with action as ACTION_CALL
                Intent phone_intent = new Intent(Intent.ACTION_CALL);

                // Set data of Intent through Uri by parsing phone number
                phone_intent.setData(Uri.parse("tel:" + phoneNumber));

                // start Intent
                startActivity(phone_intent);
            }
        });
        locationRequest = LocationRequest.create();
        locationRequest.setInterval(4000); // Interval in milliseconds between updates
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        Intent intent = getIntent();
        id = intent.getStringExtra("customerName");
        String sourceAddress = intent.getStringExtra("sourceAddress");
        String desAddress = intent.getStringExtra("desAddress");
        String priceIntent = intent.getStringExtra("price");
        // Initialize locationCallback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    JSONObject body = new JSONObject();

                    Location location = locationResult.getLastLocation();
                    if (location != null) {
                        try {
                            body.put("senderID", User.currentUser.getId());
                            body.put("receiverID", id);
                            double currentLatitude = location.getLatitude();
                            double currentLongitude = location.getLongitude();
                            body.put("latitude", currentLatitude);
                            body.put("longitude", currentLongitude);
                            String data = body.toString();
                            WebsocketConnector websocketConnector = WebsocketConnector.getInstance();
                            websocketConnector.send("/app/sendLocationBooking", data);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }
            }
        };

        // Request location updates
        try {
            LocationServices.getFusedLocationProviderClient(this)
                    .requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
        } catch (SecurityException e) {
            // Handle permission denial
        }

        cusName = findViewById(R.id.cusName);
        srcName = findViewById(R.id.srcName);
        srcDes = findViewById(R.id.srcDes);
        destName = findViewById(R.id.destName);
        destDes = findViewById(R.id.destDes);
        price = findViewById(R.id.price);
        btn = findViewById(R.id.button);
        cusPhone = findViewById(R.id.cusPhone);
        getUserStatus(id);

        if (priceIntent.contains("__")){
            parts = priceIntent.split("__");
            Log.e("Hello", parts.toString());
            cusName.setText(parts[0]);
            price.setText(parts[2]);
            cusPhone.setText(parts[1]);
        } else {
            parts = null;
            price.setText(priceIntent);
            cusPhone.setText(phoneNumber);
        }
        srcName.setText(sourceAddress);
        destName.setText(desAddress);


        btn.setOnClickListener(view -> {
            LocationServices.getFusedLocationProviderClient(this).removeLocationUpdates(locationCallback);

            Intent i = new Intent(this, RideFinishActivity.class);
            i.putExtra("customerName", id);
            i.putExtra("sourceAddress", sourceAddress);
            i.putExtra("desAddress", desAddress);
            i.putExtra("price", priceIntent);

            JsonObject jsonObject = new JsonObject();
            Gson gson = new Gson();
            jsonObject.addProperty("senderID", currentUser.getId());
            jsonObject.addProperty("receiverID", id);
            jsonObject.addProperty("message", "pickup");
            String jsonMessage = gson.toJson(jsonObject);
            WebsocketConnector websocketConnector = WebsocketConnector.getInstance();
            websocketConnector.send("/app/accept", jsonMessage);
            startActivity(i);
            finish();
        });

    }
    private String createLocationDataJson(double latitude, double longitude) {
        JSONObject locationJson = new JSONObject();
        try {
            locationJson.put("latitude", latitude);
            locationJson.put("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return locationJson.toString();
    }

    public void getUserStatus(String id){
        ApiCaller apiCaller = ApiCaller.getInstance();
        String token = getApplicationContext().getSharedPreferences("MyToken", Context.MODE_PRIVATE).getString("token", null);
        apiCaller.get("/api/driver/user/" + id, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());

                    // Update UI elements using runOnUiThread
                    runOnUiThread(() -> {
                        try {
                            cusName.setText(jsonObject.getString("username"));
                            name = jsonObject.getString("username");
                            phoneNumber = jsonObject.getString("phoneNumber");
                            cusPhone.setText(phoneNumber);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        }, token);
    }
}