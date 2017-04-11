package com.example.david.jumpandrun;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import android.app.Activity;


public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView count;

    boolean activityRunning;
    boolean first;
    float sensorInitialValue;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        count = (TextView) findViewById(R.id.count);
        first = true;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        activityRunning = false;
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (activityRunning) {
            if(first){
                sensorInitialValue = event.values[0];
                first = false;
            }
            count.setText(String.valueOf(event.values[0] - sensorInitialValue));

        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}