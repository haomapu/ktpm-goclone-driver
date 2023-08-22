package com.example.ktpm_goclone_driver;

import static com.example.ktpm_goclone_driver.User.currentUser;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.stomped.stomped.client.StompedClient;
import com.stomped.stomped.component.StompedFrame;
import com.stomped.stomped.listener.StompedListener;

import org.json.JSONException;
import org.json.JSONObject;

public class WebsocketConnector {
    private static WebsocketConnector websocketConnector;
    private StompedClient stompedClient;
    private double latitude, longitude;
    private boolean isWaitingForResponse = false;
    public boolean driver = false;
    private Handler responseHandler; // For handling response timeout
    private BookingNotificationListener bookingNotificationListener;
    public interface BookingNotificationListener {
        void onBookingNotificationReceived(JSONObject bookingData);
    }


    private WebsocketConnector(){
        stompedClient = new StompedClient.StompedClientBuilder().build("ws://ktpm-goride.onrender.com/ws");
        stompedClient.subscribe("/topic/user/" + currentUser.getId() + "/chat", new StompedListener(){
            @Override
            public void onNotify(final StompedFrame frame){
                Log.e("Hello", frame.getStompedBody().toString());
            }
        });

        stompedClient.subscribe("/topic/driver/" + currentUser.getId() + "/booking", new StompedListener() {
            @Override
            public void onNotify(final StompedFrame frame) {
                JSONObject jsonObject = null;
                try {

                    jsonObject = new JSONObject(frame.getStompedBody().toString());
                    Log.e("Hello/Location", jsonObject.toString());
                    websocketConnector.setLatitude(jsonObject.getDouble("latitude"));
                    websocketConnector.setLongitude(jsonObject.getDouble("longitude"));
                    driver = true;
                    MapsActivity.checkStatus = false;

                    // Notify the listener when a booking notification is received
                    websocketConnector.notifyBookingNotification(jsonObject);

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
        });

        stompedClient.subscribe("/topic/user/" + currentUser.getId() + "/pickup", new StompedListener(){
            @Override
            public void onNotify(final StompedFrame frame){
                driver = false;
                MapsActivity.checkStatus = false;
            }
        });
    }

    // ...

    public void setBookingNotificationListener(BookingNotificationListener listener) {
        this.bookingNotificationListener = listener;
    }

    public void notifyBookingNotification(JSONObject bookingData) {
        if (bookingNotificationListener != null) {
            bookingNotificationListener.onBookingNotificationReceived(bookingData);
        }
    }


    public static WebsocketConnector getInstance() {
        if (websocketConnector == null) {
            websocketConnector = new WebsocketConnector();
        }
        return websocketConnector;
    }
    public void send(String destination, String body){
        stompedClient.send(destination, body);
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
