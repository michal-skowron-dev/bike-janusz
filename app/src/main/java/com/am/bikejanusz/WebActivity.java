package com.am.bikejanusz;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;

public class WebActivity extends AppCompatActivity {

    WebView webView1;
    String route, url;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        webView1 = findViewById(R.id.webView1);

        intent = getIntent();
        route = intent.getStringExtra("ROUTE");

        url = "http://januszbike.ct8.pl:8303/get-route/" + route;
        webView1.loadUrl(url);
    }
}
