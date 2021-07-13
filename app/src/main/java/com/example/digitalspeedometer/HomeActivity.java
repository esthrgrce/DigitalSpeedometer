package com.example.digitalspeedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;

public class HomeActivity extends AppCompatActivity {

    ImageButton btnMapview;
    ImageButton btnSpeedometer;
    ImageButton btnHistory;
    ImageButton btnSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        btnMapview = findViewById(R.id.btnMapview);
        btnSpeedometer = findViewById(R.id.btnSpeedometer);
        btnSettings = findViewById(R.id.btnSettings);

        btnMapview.setOnClickListener(v -> {
            Intent intent = new Intent(this, MapView.class);
            startActivity(intent);
        });

        btnSpeedometer.setOnClickListener(v -> {
            Intent intent = new Intent(this, Speedometer.class);
            startActivity(intent);
        });


        btnSettings.setOnClickListener(v -> {
            Intent intent = new Intent(this, Settings.class);
            startActivity(intent);
        });
    }
}