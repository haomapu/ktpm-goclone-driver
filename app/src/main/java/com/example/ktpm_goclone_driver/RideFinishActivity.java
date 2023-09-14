package com.example.ktpm_goclone_driver;

import static com.example.ktpm_goclone_driver.User.currentUser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RideFinishActivity extends AppCompatActivity {
    Button button;
    String id, sourceAddress, desAddress, priceIntent;
    TextView destName, price;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ride_finish);
        button = findViewById(R.id.button);
        destName = findViewById(R.id.destName);
        price = findViewById(R.id.price);
        Intent intent = getIntent();
        id = intent.getStringExtra("customerName");
        sourceAddress = intent.getStringExtra("sourceAddress");
        desAddress = intent.getStringExtra("desAddress");
        priceIntent = intent.getStringExtra("price");
        destName.setText(desAddress);
        price.setText(priceIntent);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(RideFinishActivity.this, MainActivity.class);
                i.putExtra("customerName", id);
                i.putExtra("sourceAddress", sourceAddress);
                i.putExtra("desAddress", desAddress);
                i.putExtra("price", priceIntent);

                JsonObject jsonObject = new JsonObject();
                Gson gson = new Gson();
                jsonObject.addProperty("senderID", currentUser.getId());
                jsonObject.addProperty("receiverID", id);
                jsonObject.addProperty("message", "done");
                String jsonMessage = gson.toJson(jsonObject);
                WebsocketConnector websocketConnector = WebsocketConnector.getInstance();
                websocketConnector.send("/app/accept", jsonMessage);

                startActivity(i);
                finish();
            }
        });
    }
}