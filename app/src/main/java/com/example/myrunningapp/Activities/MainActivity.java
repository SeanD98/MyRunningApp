package com.example.myrunningapp.Activities;

import android.os.Bundle;
import android.widget.TextView;

import com.example.myrunningapp.R;
import com.google.android.gms.maps.MapView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //MapView mapView = (MapView) findViewById(R.id.mapView);
    }
}
