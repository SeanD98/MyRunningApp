package com.example.myrunningapp.Activities;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Switch;
import android.widget.ToggleButton;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.myrunningapp.R;
import com.example.myrunningapp.Utils.DataController;

public class SettingsActivity extends AppCompatActivity {

    Switch calorieCounter;
    Switch autoLogin;
    ToggleButton measurementUnits;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        calorieCounter = (Switch) findViewById(R.id.calorie_counter_switch_settings);
        autoLogin = (Switch) findViewById(R.id.auto_login_switch_settings);
        measurementUnits = (ToggleButton) findViewById(R.id.measurement_unit_toggle);

        if (DataController.getInstance(getApplicationContext()).measurementUnits == 0){
            //if state is OFF upon opening
            measurementUnits.setChecked(false);
        } else if (DataController.getInstance(getApplicationContext()).measurementUnits == 1){
            //if state is ON upon opening
            measurementUnits.setChecked(true);
        }

        if (DataController.getInstance(getApplicationContext()).calorieCounterState == 1){
            calorieCounter.setChecked(true);
        } else if (DataController.getInstance(getApplicationContext()).calorieCounterState == 0){
            calorieCounter.setChecked(false);
        }

        if (autoLogin.isEnabled()){
            DataController.getInstance(getApplicationContext()).autoLoginState = 1;
        }
        if (!autoLogin.isEnabled()){
            DataController.getInstance(getApplicationContext()).autoLoginState = 0;
        }

        calorieCounter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if switch is turned on
                if (DataController.getInstance(getApplicationContext()).calorieCounterState == 0) {
                    calorieCounter.setChecked(true);
                    DataController.getInstance(getApplicationContext()).calorieCounterState = 1;
                }
                //if switch is turned off
                else if (DataController.getInstance(getApplicationContext()).calorieCounterState == 1){
                    calorieCounter.setChecked(false);
                    DataController.getInstance(getApplicationContext()).calorieCounterState = 0;
                }
            }
        });

        measurementUnits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Default is OFF (Meters)
                if (DataController.getInstance(getApplicationContext()).measurementUnits == 0){
                    //IF switch is OFF, set state to ON
                    DataController.getInstance(getApplicationContext()).measurementUnits = 1;
                } else if (DataController.getInstance(getApplicationContext()).measurementUnits == 1){
                    //If switch os ON, set state to OFF
                    DataController.getInstance(getApplicationContext()).measurementUnits = 0;
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("                    Settings"); //Temp
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return true;
    }
}
