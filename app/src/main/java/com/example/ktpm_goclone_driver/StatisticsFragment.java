package com.example.ktpm_goclone_driver;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link StatisticsFragment} factory method to
 * create an instance of this fragment.
 */
public class StatisticsFragment extends Fragment {

    TextView txt_order_processing, txt_order_delivering, txt_order_delivered,
            txt_total_revenue, txt_inventory;
    String status;
    int totalPrice = 0;
    int countBooking = 0;

    public StatisticsFragment(String status) {
        this.status = status;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_admin_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        txt_total_revenue = view.findViewById(R.id.txt_total_revenue);
        txt_inventory = view.findViewById(R.id.txt_inventory);
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
                        SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
                        Date date = inputFormat.parse(jsonObject.getString("dateTime"));
                        String formattedDate = outputFormat.format(date);
                        jsonObject.getString("dateTime");
                        jsonObject.getString("price");

                        Log.e("datetime", formattedDate);
                        Log.e("price", jsonObject.getString("price"));

                        if (status.equals("0")) {
                            totalPrice += Integer.valueOf(jsonObject.getString("price"));
                            countBooking += 1;

                        } else if (status.equals("1")) {
                            LocalDateTime localDateTime = LocalDateTime.now();
                            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            String formattedCurrentdate = localDateTime.format(myFormatObj);

                            if (formattedDate.equals(formattedCurrentdate)) {
                                totalPrice += Integer.valueOf(jsonObject.getString("price"));
                                countBooking += 1;
                            }
                        } else {
                            LocalDateTime localDateTime = LocalDateTime.now().minusDays(1);
                            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy");
                            String formattedPreviousdate = localDateTime.format(myFormatObj);

                            if (formattedDate.equals(formattedPreviousdate)) {
                                totalPrice += Integer.valueOf(jsonObject.getString("price"));
                                countBooking += 1;
                            }
                        }
                    }
//                    Log.e("totalPrice", String.valueOf(totalPrice));
//                    Log.e("countBooking", String.valueOf(countBooking));


                    requireActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            txt_total_revenue.setText((NumberFormat.getNumberInstance(Locale.US).format(totalPrice)));
                            txt_inventory.setText((NumberFormat.getNumberInstance(Locale.US).format(countBooking)));


                            BarChart barChart = view.findViewById(R.id.bar_chart);

                            List<BarEntry> barEntries = new ArrayList<>();
                            Log.e("totalPrice", String.valueOf(totalPrice));
                            barEntries.add(new BarEntry(1f, totalPrice));

                            BarDataSet barDataSet = new BarDataSet(barEntries, "Bar Data Set");
                            barDataSet.setColors(Color.parseColor("#ffb259"));
                            barDataSet.setValueTextColor(Color.BLACK);

                            BarData barData = new BarData(barDataSet);
                            barChart.setData(barData);

                            barChart.setDrawValueAboveBar(true);
                            barChart.getDescription().setEnabled(false);
                            barChart.getLegend().setEnabled(false);
                            barChart.getAxisRight().setEnabled(false);

                            barChart.invalidate();
                        }
                    });

                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
                catch (ParseException e) {
                    throw new RuntimeException(e);
                }

            }
        }, token);


    }
}