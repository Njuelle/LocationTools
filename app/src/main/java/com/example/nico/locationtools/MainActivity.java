package com.example.nico.locationtools;

import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends LocationActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonLocat = (Button) findViewById(R.id.buttonLocat);
        final TextView textView = (TextView) findViewById(R.id.textView);

        buttonLocat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Location loc = locationTools.getLocation();
                textView.setText("lat: " + loc.getLatitude() + " / lng: " + loc.getLongitude());
            }
        });

    }
}
