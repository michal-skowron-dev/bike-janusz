package com.am.bikejanusz;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Button.OnClickListener {

    String startStation, finalStation, route;

    ArrayList<String> names = new ArrayList<String>();
    ArrayList<String> ids = new ArrayList<String>();

    ArrayAdapter<String> adapter;

    String[] stations, pair;

    Spinner spinner1, spinner2;
    Button button1, button2, button3;

    Intent intent;

    String url = "http://januszbike.ct8.pl:8303/get-stations";
    String nearestStation = "Proszę spróbować ponownie!";

    RequestQueue queue;

    double currentLat, currentLon, minDistance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, 123);

        spinner1 = findViewById(R.id.spinner1);
        spinner2 = findViewById(R.id.spinner2);

        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        stations = getResources().getStringArray(R.array.stations);
        Arrays.sort(stations);

        for(String station : stations) {
            pair = station.split(":");

            names.add(pair[0]);
            ids.add(pair[1]);
        }

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner1.setAdapter(adapter);
        spinner2.setAdapter(adapter);

        spinner1.setOnItemSelectedListener(this);
        spinner2.setOnItemSelectedListener(this);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.equals(spinner1))
            startStation = ids.get(names.indexOf(parent.getItemAtPosition(position).toString()));
        else
            finalStation = ids.get(names.indexOf(parent.getItemAtPosition(position).toString()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v.equals(button3)) {
            GPStracker gt = new GPStracker(getApplicationContext());
            Location l = gt.getLocation();

            if(l == null)
                Toast.makeText(getApplicationContext(),"GPS unable to get Value",Toast.LENGTH_SHORT).show();
            else {
                currentLat = l.getLatitude();
                currentLon = l.getLongitude();
                minDistance = Double.MAX_VALUE;

                queue = Volley.newRequestQueue(this);
                jsonParse(url);
            }

            Toast.makeText(this, nearestStation, Toast.LENGTH_SHORT).show();
            return;
        }

        if (!startStation.matches(finalStation)) {
            if (v.equals(button1))
                intent = new Intent(this, WebActivity.class);
            else
                intent = new Intent(this, MapActivity.class);

            route = startStation + "/" + finalStation;
            intent.putExtra("ROUTE", route);

            startActivity(intent);
        }
        else
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
    }

    public void jsonParse(String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("stations");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject station = jsonArray.getJSONObject(i);

                        double latitude = Double.valueOf(station.getString("lat"));
                        double longitude = Double.valueOf(station.getString("lng"));

                        double calculation = Math.sqrt(Math.pow((latitude - currentLat), 2) + Math.pow((longitude - currentLon), 2));

                        if (calculation < minDistance) {
                            minDistance = calculation;
                            nearestStation = station.getString("name");
                        }
                    }
                }
                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        queue.add(request);
    }
}
