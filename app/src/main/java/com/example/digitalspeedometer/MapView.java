package com.example.digitalspeedometer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.math.BigDecimal;
import java.util.Locale;

public class MapView extends AppCompatActivity implements OnMapReadyCallback {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 123;
    private GoogleMap mMap;
    private boolean locationPermissionGranted;
    private FusedLocationProviderClient fusedLocationClient;
    private TextView txtSpeed;
    private TextView txtTime;
    private TextView txtDistance;
    private TextView lblUnit;
    private TextView lblDistanceUnit;
    private String speed_units[];
    private Location previous;
    private int seconds = 0;
    private float distance = 0;
    private PreferencesHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_view);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapView);
        db = PreferencesHelper.getInstance(this);
        mapFragment.getMapAsync(this);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        txtSpeed = findViewById(R.id.txtSpeed);
        lblUnit = findViewById(R.id.lblUnit);
        txtTime = findViewById(R.id.txtTime);
        txtDistance = findViewById(R.id.txtDistance);
        lblDistanceUnit = findViewById(R.id.lblDistanceUnit);
        Handler handler = new Handler();
        speed_units = getResources().getStringArray(R.array.speed_units);
        String unit = speed_units[db.getSpeedUnit()];
        switch (unit){
            case "kph":
                lblUnit.setText("kph");
                lblDistanceUnit.setText("km");
                break;
            case "mph":
                lblUnit.setText("mph");
                lblDistanceUnit.setText("mi");
                break;
            case "m/s":
                lblDistanceUnit.setText("m");
                lblUnit.setText("m/s");
                break;
        }
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
                txtTime.setText(time);
                seconds++;
                handler.postDelayed(this, 1000);
            }
        });
        getLocationPermission();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.zoomTo(18));
//        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            return;
        }
        mMap.setMyLocationEnabled(true);

    }


    private void getLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            locationPermissionGranted = true;
            getLocation();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    locationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {

        if (locationPermissionGranted){
            Toast.makeText(this, "Permission granted!", Toast.LENGTH_SHORT).show();
            getLocation();
        }else{
            Toast.makeText(this, "Permission denied! App may malfunction.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            this.finish();
        }
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
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(position));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(position));
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
                txtDistance.setText(BigDecimal.valueOf(distance).setScale(2, BigDecimal.ROUND_HALF_UP).toString());
                txtSpeed.setText(String.valueOf(speed));
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