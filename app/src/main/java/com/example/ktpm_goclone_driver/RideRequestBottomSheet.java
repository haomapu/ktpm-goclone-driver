package com.example.ktpm_goclone_driver;

import static com.example.ktpm_goclone_driver.User.currentUser;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.common.api.Api;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class RideRequestBottomSheet extends BottomSheetDialogFragment {

    private Context context;
    private Callback callback;
    private String customerName;
    private String sourceLocation;
    private String destinationLocation;
    private String price;
    private String bookingId;

    public RideRequestBottomSheet(Context context, String customerName, String sourceLocation, String destinationLocation, String price, String bookingId) {
        this.context = context;
        this.customerName = customerName;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.price = price;
        this.bookingId = bookingId;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_ride_request, container, false);

        TextView tvCustomerName = view.findViewById(R.id.tvCustomerName);
        TextView tvSourceLocation = view.findViewById(R.id.tvSourceLocation);
        TextView tvDestinationLocation = view.findViewById(R.id.tvDestinationLocation);
        TextView tvPrice = view.findViewById(R.id.tvPrice);
        Button btnAccept = view.findViewById(R.id.btnAccept);
        Button btnDeny = view.findViewById(R.id.btnDeny);

        tvCustomerName.setText(customerName);
        tvSourceLocation.setText(sourceLocation);
        tvDestinationLocation.setText(destinationLocation);
        tvPrice.setText(price);
        ApiCaller apiCaller = ApiCaller.getInstance();
        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle accept button click
                // Send the message to the WebSocket server.
                JsonObject jsonObject = new JsonObject();
                Gson gson = new Gson();
                jsonObject.addProperty("senderID", currentUser.getId());
                jsonObject.addProperty("receiverID", tvCustomerName.getText().toString());
                jsonObject.addProperty("message", "pickup");
                String jsonMessage = gson.toJson(jsonObject);
                WebsocketConnector websocketConnector = WebsocketConnector.getInstance();
                websocketConnector.send("/app/accept", jsonMessage);
                ApiCaller apiCaller = ApiCaller.getInstance();
                String token = context.getSharedPreferences("MyToken", Context.MODE_PRIVATE).getString("token", null);

                apiCaller.patch("/api/driver/booking/" + bookingId, jsonMessage, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {

                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Log.e("Hello", response.body().string());
                    }
                }, token);

                callback.onActionClick("accept");
                dismiss();
            }
        });

        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle deny button click
                callback.onActionClick("deny");
                dismiss();
            }
        });

        return view;
    }
    public interface Callback {

        void onActionClick(String message);

    }
}
