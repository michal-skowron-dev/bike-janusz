package com.am.bikejanusz;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@SuppressLint("SetJavaScriptEnabled")
public class MapActivity extends AppCompatActivity {

    StringBuilder markers = new StringBuilder();

    WebView webView1;
    String route, url;
    Intent intent;

    RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        webView1 = findViewById(R.id.webView1);

        WebSettings webSettings = webView1.getSettings();
        webSettings.setJavaScriptEnabled(true);

        intent = getIntent();
        route = intent.getStringExtra("ROUTE");

        url = "http://januszbike.ct8.pl:8303/get-route/" + route;

        queue = Volley.newRequestQueue(this);
        jsonParse(url);
    }

    public void jsonParse(String url){
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray jsonArray = response.getJSONArray("stations");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject station = jsonArray.getJSONObject(i);

                        String latitude = station.getString("lat");
                        String longitude = station.getString("lng");

                        markers.append("/");
                        markers.append(latitude);
                        markers.append(",");
                        markers.append(longitude);
                    }

                    String map = "https://www.google.com/maps/dir" + markers.toString();
                    webView1.loadUrl(map);
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
