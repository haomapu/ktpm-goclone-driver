package com.example.ktpm_goclone_driver;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.ktpm_goclone_driver.listeners.ItemListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment implements ItemListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private RecyclerView.Adapter adapter;
    private RecyclerView recyclerViewList;
    ArrayList<ListHistoryLocation> locations ;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Geocoder geocoder;


    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PromoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_history, container, false);
    }
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        shopping_badge = view.findViewById(R.id.shopping_badge);
//        shopping_badge.setNumber(3);
        geocoder = new Geocoder(getContext(), Locale.getDefault());
        // This adapter for LOCATIONS
        locations = new ArrayList<>();
        recyclerViewList = view.findViewById(R.id.historyLocation);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        recyclerViewList.setLayoutManager(linearLayoutManager);
        ApiCaller apiCaller = ApiCaller.getInstance();
        String token = getContext().getSharedPreferences("MyToken", Context.MODE_PRIVATE).getString("token", null);

        apiCaller.get("/api/driver/booking", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i++){
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ENGLISH);
                        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMM HH:mm", Locale.ENGLISH);
                        Date date = inputFormat.parse(jsonObject.getString("dateTime"));
                        String formattedDate = outputFormat.format(date);
                        Log.e("Hello", jsonObject.toString());
                        locations.add(new ListHistoryLocation("To: " + getLocationAndConvertToAddress(jsonObject.getJSONObject("destinationLocation").getDouble("latitude"),jsonObject.getJSONObject("destinationLocation").getDouble("longitude")),"From: " + getLocationAndConvertToAddress(jsonObject.getJSONObject("sourceLocation").getDouble("latitude"),jsonObject.getJSONObject("sourceLocation").getDouble("longitude")), formattedDate, jsonObject.getString("price")));
                        Log.e("Hello haompau", String.valueOf(locations.get(0).dateTime));
                    }
                    adapter = new HistoryLocationAdapter(getContext(), locations,  HistoryFragment.this);
                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            recyclerViewList.setAdapter(adapter);
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        }, token);

//        locations.add(new ListHistoryLocation("29 Nguyen Huy Luong St.", "29 Nguyễn Huy Lượng, P.14, Q.Bình Thạnh, Hồ Chí Minh", "8 Feb 16:54", "4"));
//        locations.add(new ListHistoryLocation("62 An Diem St.", "62 An Điềm, P.10, Q.5, Hồ Chí Minh, 7000, Vietnam", "31 Jan 22:04", "4"));
//        locations.add(new ListHistoryLocation("63 Mac Dinh Chi St.", "63 Mạc Đỉnh Chi, P.Đa Kao, Q.1, Hồ Chí Minh, 7000, Vietnam", "31 Jan 20:35", "7"));
//        locations.add(new ListHistoryLocation("227 Nguyen Van Cu St.", "227 Nguyen Van Cu, Q.5, Hồ Chí Minh, 7000, Vietnam", "10 Feb 20:35", "7"));



    }

    public void OnItemPosition(int position) {
        Intent intent = new Intent(getContext(), HistoryLocationDetailActivity.class);
        intent.putExtra("location",  locations.get(position));
        intent.putExtra("srcLocation",  locations.get(position));
        startActivity(intent);
    }
    @Override
    public void onStop() {
        super.onStop();

    }

    public String getLocationAndConvertToAddress(double latitude, double longitude) {
        // Replace with your method to get the current location (latitude and longitude)
        // For example, you can use the FusedLocationProviderClient or LocationManager.
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String addressText = address.getAddressLine(0);
                return addressText;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}