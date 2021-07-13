package com.example.digitalspeedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.math.BigDecimal;
import java.util.Locale;

public class Speedometer extends AppCompatActivity {

    private String speed_units[];
    private TextView lblspeed;
    private TextView lblspeed_unit;
    private TextView lbldistance;
    private TextView lbldistance_unit;
    private TextView lblmaxspeed;
    private TextView lblmaxspeed_unit;
    private TextView lbltime;
    private Location previous;
    private int seconds = 0;
    private float distance = 0;
    private PreferencesHelper db;
    private boolean alarmEnabled = false;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speedometer);
        speed_units = getResources().getStringArray(R.array.speed_units);
        lbldistance = findViewById(R.id.lbldistance);
        lbldistance_unit = findViewById(R.id.lbldistance_unit);
        lblspeed = findViewById(R.id.lblspeed);
        lblspeed_unit = findViewById(R.id.lblspeed_unit);
        lblmaxspeed = findViewById(R.id.lblmaxspeed);
        lblmaxspeed_unit = findViewById(R.id.lblmaxspeed_unit);
        lbltime = findViewById(R.id.lbltime);
        db = PreferencesHelper.getInstance(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        lblmaxspeed.setText(String.valueOf(db.getMaxSpeed()));
        String unit = speed_units[db.getSpeedUnit()];
        switch (unit){
            case "kph":
                lblspeed_unit.setText("kph");
                lbldistance_unit.setText("km");
                lblmaxspeed_unit.setText("kph");
                break;
            case "mph":
                lblspeed_unit.setText("mph");
                lblmaxspeed_unit.setText("mph");
                lbldistance_unit.setText("mi");
                break;
            case "m/s":
                lbldistance_unit.setText("m");
                lblspeed_unit.setText("m/s");
                lblmaxspeed_unit.setText("m/s");
                break;
        }
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours = seconds / 3600;
                int minutes = (seconds % 3600) / 60;
                int secs = seconds % 60;
                String time
                        = String
                        .format(Locale.getDefault(),
                                "%02d:%02d:%02d", hours,
                                minutes, secs);
                lbltime.setText(time);
                seconds++;
                handler.postDelayed(this, 1000);
            }
        });
        alarmEnabled = db.getIsAlarmEnabled();
        getLocation();
    }

    @SuppressLint("MissingPermission")
    private void getLocation(){
        LocationCallback locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                LatLng position = new LatLng(latitude, longitude);
                int speed = 0;
                if (previous != null){
                    if (db.getSpeedUnit() == 0){
                        distance += location.distanceTo(previous);
                        speed = Math.round(location.getSpeed() * 60 / 1000);
                    }else if (db.getSpeedUnit() == 1){
                        distance += location.distanceTo(previous);
                        speed = Math.round(location.getSpeed() * 60 / 1600);
                    }else{
                        distance += location.distanceTo(previous);
                        speed = Math.round(location.getSpeed());
                    }
                }
                previous = location;
                lbldistance.setText(BigDecimal.valueOf(distance).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                lblspeed.setText(String.valueOf(speed));
                if (alarmEnabled){
                    if (speed > db.getMaxSpeed()){
                        Toast.makeText(Speedometer.this, "Overspeeding!!!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        };
        fusedLocationClient.requestLocationUpdates(createLocationRequest(), locationCallback, Looper.getMainLooper());
    }
    protected LocationRequest createLocationRequest() {
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(1000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        return locationRequest;
    }

}