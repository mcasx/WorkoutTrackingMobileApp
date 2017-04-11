package com.example.david.jumpandrun;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class JumpActivity extends AppCompatActivity implements SensorEventListener {

    TextView minX;
    TextView maxX;
    TextView minY;
    TextView maxY;
    TextView minZ;
    TextView maxZ;
    TextView module;
    TextView thresholdText;
    TextView countText;

    long time;
    boolean thresholdup;
    float minx,maxx,miny,maxy,minz,maxz = 0.0f;

    int count;

    boolean stationary;

    float[]  linear_acceleration = new float[3];

    private SensorManager sensorManager;
    PowerManager.WakeLock wl;

    double threshold;
    double accelaration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();

        Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        if (countSensor != null) {
            sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
        } else {
            Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
        }

        minX = (TextView) findViewById(R.id.minX);
        maxX = (TextView) findViewById(R.id.maxX);
        minY = (TextView) findViewById(R.id.minY);
        maxY = (TextView) findViewById(R.id.maxY);
        minZ = (TextView) findViewById(R.id.minZ);
        maxZ = (TextView) findViewById(R.id.maxZ);
        module = (TextView) findViewById(R.id.module);
        countText = (TextView) findViewById(R.id.count);

        countText.setText(String.format("%d", count));
        thresholdText = (TextView) findViewById(R.id.threshold);
        count = 0;
        stationary = true;
        threshold = 0;
        thresholdup = false;

        time = System.currentTimeMillis();
    }

    @Override
    protected void onDestroy() {
        wl.release();
        super.onDestroy();
    }


    @Override
    protected void onResume() {
        wl.acquire();
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wl.release();
        // if you unregister the last listener, the hardware will stop detecting step events
//        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        // In this example, alpha is calculated as t / (t + dT),
        // where t is the low-pass filter's time-constant and
        // dT is the event delivery rate.



        // Remove the gravity contribution with the high-pass filter.
        linear_acceleration[0] = event.values[0];
        linear_acceleration[1] = event.values[1];
        linear_acceleration[2] = event.values[2];

        if(linear_acceleration[0] < minx)
            minx = linear_acceleration[0];
        else if (linear_acceleration[0] > maxx)
            maxx = linear_acceleration[0];

        if(linear_acceleration[1] < miny)
            miny = linear_acceleration[1];
        else if (linear_acceleration[1] > maxy)
            maxy = linear_acceleration[1];

        if(linear_acceleration[2] < minz)
            minz = linear_acceleration[2];
        else if (linear_acceleration[2] > maxz)
            maxz = linear_acceleration[0];

        maxX.setText(String.format("%.2f", minx));
        minX.setText(String.format("%.2f", maxx));
        minY.setText(String.format("%.2f", miny));
        maxY.setText(String.format("%.2f", maxy));
        minZ.setText(String.format("%.2f", minz));
        maxZ.setText(String.format("%.2f", maxz));

        accelaration = Math.sqrt(linear_acceleration[0] * linear_acceleration[0] + linear_acceleration[1] * linear_acceleration[1] + linear_acceleration[2] * linear_acceleration[2]);

        if(accelaration > threshold + 3.5 || accelaration < threshold - 3.5)
            threshold = accelaration;

        module.setText(String.format("%.2f", accelaration));
        thresholdText.setText(String.format("%.2f", threshold));

        if ( time < System.currentTimeMillis() + 250) {
            if (threshold > 11 && !thresholdup) {

                count++;
                countText.setText(String.format("%d", count/2));
                thresholdup = true;
                time = System.currentTimeMillis();
            } else
                thresholdup = false;
        }

    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
