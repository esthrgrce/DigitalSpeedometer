package com.example.digitalspeedometer;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

public class Settings extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private PreferencesHelper db;
    private Spinner spinner;
    private String speed_units[];
    private TextView lblSpeedUnit;
    private TextView txtMaxSpeed;
    private Switch speedalarm;
    private Button btnSaveSpeed;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        db = PreferencesHelper.getInstance(this);
        spinner = findViewById(R.id.spinner);
        speedalarm = findViewById(R.id.speedalarm);
        txtMaxSpeed = findViewById(R.id.txtMaxSpeed);
        lblSpeedUnit = findViewById(R.id.lblSpeedUnit);
        btnSaveSpeed = findViewById(R.id.btnSaveSpeed);
        speed_units = getResources().getStringArray(R.array.speed_units);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.speed_units, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(db.getSpeedUnit());
        lblSpeedUnit.setText(speed_units[db.getSpeedUnit()]);
        speedalarm.setChecked(db.getIsAlarmEnabled());
        speedalarm.setOnCheckedChangeListener((buttonView, isChecked) -> {
            db.setIsAlarmEnabled(isChecked);
        });
        txtMaxSpeed.setText(String.valueOf(db.getMaxSpeed()));
        btnSaveSpeed.setOnClickListener(v -> {
            int newSpeed = Integer.parseInt(String.valueOf(txtMaxSpeed.getText()));
            if (newSpeed < 0 || newSpeed > 100){
                Toast.makeText(this, "Max speed should range from 0-100", Toast.LENGTH_SHORT).show();
                txtMaxSpeed.setText(String.valueOf(db.getMaxSpeed()));
            }else{
                db.setMaxSpeed(newSpeed);
                Toast.makeText(this, "New maximum speed saved!", Toast.LENGTH_SHORT).show();
            }
        });
        txtMaxSpeed.setInputType(InputType.TYPE_CLASS_NUMBER);
        initPreferences();
    }

    private void initPreferences(){
        Log.i("Prefs", db.mPrefs.toString());
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        db.setSpeedUnit(position);
        lblSpeedUnit.setText(parent.getItemAtPosition(position).toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        return;
    }
}