package g11.muscle;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


public class CardioActivity extends AppCompatActivity implements SensorEventListener {

    private static final int UPDATE_TEXT = 0;
    private SensorManager sensorManager;
    private TextView count;
    boolean activityRunning;
    boolean first;
    float sensorInitialValue;
    private PowerManager.WakeLock wl;
    int countSteps;
    private String TAG = "CardioActivity";
    private LocationManager locationManagerGPS;

    private boolean started = false;
    private Handler handler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            TextView counter = (TextView) findViewById(R.id.counter);
            String[] time = counter.getText().toString().split(":");
            int min = Integer.parseInt(time[0]);
            int sec = Integer.parseInt(time[1]);
            if(sec == 59){
                min++;
                sec = 0;
            }
            else
                sec++;

            counter.setText((min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec));

            if(started)
                start();
        }
    };

    public void stop() {
        started = false;
        handler.removeCallbacks(runnable);
    }

    public void start() {
        started = true;
        handler.postDelayed(runnable, 1000);


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);

        count = (TextView) findViewById(R.id.steps);


        first = true;
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        ///////////Location////////////////
        locationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManagerGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationListenerGPS.onProviderEnabled("GPS");
        } else {
            locationListenerGPS.onProviderDisabled("GPS");
        }

        ////////////////////////////////////////////

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();

    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        wl.release();
        locationManagerGPS.removeUpdates(locationListenerGPS);
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (first) {
            sensorInitialValue = event.values[0];
            first = false;
        }
        countSteps = (int) (event.values[0] - sensorInitialValue);
        count.setText("Steps: " + countSteps);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClickButton(View view) {
        if (((Button) view).getText().equals("Start")) {
            start();
            Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (countSensor != null) {
                sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
            } else {
                Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                        0);
                Log.e(TAG, "GPS Doesn't have permission");
                return;
            }

            locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);
            ((Button) view).setText("Stop");
        } else {
            stop();
            locationManagerGPS.removeUpdates(locationListenerGPS);
            Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (countSensor != null) {
                sensorManager.unregisterListener(this);
            }

            ((Button) view).setText("Start");

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

////////////////////////////////////////////////////////////////////////////////

    private String latGPS = null;
    private String lngGPS = null;
    private String accGPS = null;
    private String latGSM = null;
    private String lngGSM = null;//Status
    private String accGSM = null;
    private String str = null;

    LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {

            latGPS = String.valueOf(location.getLatitude());
            lngGPS = String.valueOf(location.getLongitude());
            accGPS = String.valueOf(location.getAccuracy());


            if(location.getExtras() != null){
                Bundle extra = location.getExtras();
                String estr = "";
                for(String s : extra.keySet()) {
                    estr += "  "+s+": "+extra.get(s).toString()+"\n";
                }

            }
            ((TextView)findViewById(R.id.averageSpeed)).setText(latGPS);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {

            str = "Status ("+provider+"): ";
            if(status == LocationProvider.TEMPORARILY_UNAVAILABLE){
                str += "temporarily unavailable";
            }else if(status == LocationProvider.OUT_OF_SERVICE) {
                str += "out of service";
            }else if(status == LocationProvider.AVAILABLE) {
                str += "available";
            }else{
                str += "unknown";
            }
            //view_gps.setText(str);

        }

        public void onProviderEnabled(String provider) {
            //final TextView view_gps = (TextView) findViewById(R.id.display_gps_status_enabled);
            //view_gps.setText(provider+" enabled");
        }

        public void onProviderDisabled(String provider) {
            //final TextView view_gps = (TextView) findViewById(R.id.display_gps_status_enabled);
            //view_gps.setText(provider+" disabled");
        }
    };


    LocationListener locationListenerGSM = new LocationListener() {
        public void onLocationChanged(Location location) {

            latGSM = String.valueOf(location.getLatitude());
            lngGSM = String.valueOf(location.getLongitude());
            accGSM = String.valueOf(location.getAccuracy());


            if(location.getExtras() != null){
                Bundle extra = location.getExtras();
                String estr = "";
                for(String s : extra.keySet()) {
                    String d = extra.get(s).toString();
                    //if(extra.get(s) instanceof Location){
                    //    d.replaceFirst("\\{Bundle\\[mParcelledData.dataSize=\\d+\\]\\}", "");
                    //}
                    estr += "  "+s+": '"+d+"'\n";
                }
                //view_gsm_extra.setText(estr);
            }

            //updatedLastUpdated((TextView) findViewById(R.id.display_gsm_updated));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            //final TextView view_gsm = (TextView) findViewById(R.id.display_gsm_status);
            String str = "Status ("+provider+"): ";
            if(status == LocationProvider.TEMPORARILY_UNAVAILABLE){
                str += "temporarily unavailable";
            }else if(status == LocationProvider.OUT_OF_SERVICE) {
                str += "out of service";
            }else if(status == LocationProvider.AVAILABLE) {
                str += "available";
            }else{
                str += "unknown";
            }
            //view_gsm.setText(str);

        }

        public void onProviderEnabled(String provider) {
        }

        public void onProviderDisabled(String provider) {
        }
    };

}
