package com.example.ktpm_goclone_driver;

import static com.example.ktpm_goclone_driver.User.currentUser;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

public class RideRequestBottomSheet extends BottomSheetDialogFragment {

    private String customerName;
    private String sourceLocation;
    private String destinationLocation;
    private String price;

    public RideRequestBottomSheet(String customerName, String sourceLocation, String destinationLocation, String price) {
        this.customerName = customerName;
        this.sourceLocation = sourceLocation;
        this.destinationLocation = destinationLocation;
        this.price = price;
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

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle accept button click
                // Send the message to the WebSocket server.
                JsonObject jsonObject = new JsonObject();
                Gson gson = new Gson();
                jsonObject.addProperty("senderID", currentUser.getId());
                jsonObject.addProperty("receiverID", tvCustomerName.getText().toString());
                jsonObject.addProperty("latitude", "10.794142037110948");
                jsonObject.addProperty("longitude", "106.69327113648772");
                jsonObject.addProperty("message", "pickup");
                String jsonMessage = gson.toJson(jsonObject);
                WebsocketConnector websocketConnector = WebsocketConnector.getInstance();
                websocketConnector.send("/app/accept", jsonMessage);
                dismiss();
            }
        });

        btnDeny.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle deny button click
                dismiss();
            }
        });

        return view;
    }
}
