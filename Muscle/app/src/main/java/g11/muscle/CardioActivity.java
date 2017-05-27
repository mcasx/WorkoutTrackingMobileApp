package g11.muscle;

import android.Manifest;
import android.app.Service;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.PowerManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import static android.location.LocationManager.GPS_PROVIDER;


public class CardioActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private TextView count;
    boolean activityRunning;
    boolean first;
    float sensorInitialValue;
    PowerManager.WakeLock wl;
    int countSteps;



    private GpsCalculator gpsCalculator = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cardio);
        count = (TextView) findViewById(R.id.instantSpeed);
        first = true;

        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
        wl.acquire();
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        this.gpsCalculator =  new GpsCalculator();


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
        super.onDestroy();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        if (first) {
            sensorInitialValue = event.values[0];
            first = false;
        }
        countSteps = (int) (event.values[0] - sensorInitialValue);
        count.setText("" + countSteps);

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void onClickButton(View view) {
        if (((Button) view).getText().equals("Start")) {
            Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (countSensor != null) {
                sensorManager.registerListener(this, countSensor, SensorManager.SENSOR_DELAY_UI);
            } else {
                Toast.makeText(this, "Count sensor not available!", Toast.LENGTH_LONG).show();
            }
            this.gpsCalculator.run(this);
            ((Button) view).setText("Stop");
        } else {
            Sensor countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            if (countSensor != null) {
                sensorManager.unregisterListener(this);
            }
            this.gpsCalculator.stop();
            ((Button) view).setText("Start");

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }



    public class GpsCalculator {

        private LocationManager locationManager = null;
        private Location previousLocation = null;
        private double totalDistance = 0D;

        private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 20; // 100 meters
        private static final long MIN_TIME_BW_UPDATES = 1000 * 30; // 30 seconds
        Context context;

        public void run(Context context) {
            // Get the location manager
            locationManager = (LocationManager) context.getSystemService(Service.LOCATION_SERVICE);
            ((TextView)findViewById(R.id.averageSpeed)).setText("Speed and other stuff");
            this.context = context;
            // Add new listeners with the given params
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener); // Network location
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, locationListener); // Gps location
        }

        public void stop()
        {
            locationManager.removeUpdates(locationListener);
        }

        private LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location newLocation)
            {
                if (previousLocation != null)
                {
                    double latitude = newLocation.getLatitude() + previousLocation.getLatitude();
                    latitude *= latitude;
                    double longitude = newLocation.getLongitude() + previousLocation.getLongitude();
                    longitude *= longitude;
                    double altitude = newLocation.getAltitude() + previousLocation.getAltitude();
                    altitude *= altitude;
                    GpsCalculator.this.totalDistance += Math.sqrt(latitude + longitude + altitude);

                }

                // Update stored location
                GpsCalculator.this.previousLocation = newLocation;

                ((TextView)findViewById(R.id.averageSpeed)).setText("" + GpsCalculator.this.totalDistance);
            }

            @Override
            public void onProviderDisabled(String provider) {}

            @Override
            public void onProviderEnabled(String provider) {}

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {}
        };
    }
}
