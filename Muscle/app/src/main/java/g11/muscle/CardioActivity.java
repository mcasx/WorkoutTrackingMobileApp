package g11.muscle;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
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
import android.os.PowerManager;
import android.support.annotation.FloatRange;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;

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
    private float stepDistance = 0;
    private DistanceTime distanceTime;
    private boolean started = false;
    private Handler handler = new Handler();
    private TextView counter;
    private TextView distance;
    private TextView averageSpeed;
    private TextView instantSpeed;
    private TextView maximumSpeed;
    private float height = 0.0f;
    private VolleyProvider req_queue;
    DecimalFormat df;
    Button startButton;
    Button finishButton;


    private Runnable runnable = new Runnable() {
        @Override
        public void run() {

            String[] time = counter.getText().toString().split(":");
            int min = Integer.parseInt(time[0]);
            int sec = Integer.parseInt(time[1]);
            finishButton.setEnabled(true);
            if(sec == 59){
                min++;
                sec = 0;
            }
            else
                sec++;

            DecimalFormat df = new DecimalFormat("0.00");

            counter.setText((min < 10 ? "0" + min : min) + ":" + (sec < 10 ? "0" + sec : sec));
            averageSpeed.setText("Average Speed: " + df.format(distanceTime.getAverageSpeed(sec+min*60)) + " Km/h");
            instantSpeed.setText("Instant Speed: " + df.format(distanceTime.getInstantSpeed(sec+min*60)) + " Km/h");
            maximumSpeed.setText("Maximum Speed: " + df.format(distanceTime.getMaxSpeed()) + " Km/h");


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

        req_queue = VolleyProvider.getInstance(this);
        getHeight();

        count = (TextView) findViewById(R.id.steps);
        counter = (TextView) findViewById(R.id.counter);
        distance = (TextView) findViewById(R.id.distance);
        averageSpeed = (TextView) findViewById(R.id.averageSpeed);
        instantSpeed = (TextView) findViewById(R.id.instantSpeed);
        maximumSpeed = (TextView) findViewById(R.id.maximumSpeed);
        startButton = (Button) findViewById(R.id.startButton);
        finishButton = (Button) findViewById(R.id.finishButton);

        df = new DecimalFormat("0.00");
        distanceTime = new DistanceTime();
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

    @Override
    protected void onResume() {
        super.onResume();
        activityRunning = true;
    }

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

        stepDistance += height==0.0f ? height * 0.45 : 1.75 * 0.45;

        if (gpsDistance <= stepDistance * 0.75) {
            distance.setText("Distance: " + df.format(stepDistance/1000) + " Km");
            String[] time = counter.getText().toString().split(":");
            int secs =  Integer.parseInt(time[1]) + Integer.parseInt(time[0]) * 60;
            distanceTime.add(stepDistance, secs);
        }
    }

    public void onClickButton(View view) {
        if (((Button) view).getText().equals("Start")) {
            start();
            Sensor countSensor = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
                countSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
            }
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

            locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2500, 10, locationListenerGPS);
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
    private String str = null;
    private Location previousLocation = null;
    private float gpsDistance = 0;

    LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {

            if(previousLocation == null){
                previousLocation = location;
                gpsDistance = stepDistance;
                return;
            }

            gpsDistance += location.distanceTo(previousLocation);

            if(gpsDistance > stepDistance * 0.75) {
                distance.setText("Distance: " + df.format(gpsDistance/1000) + " Km");
                String[] time = counter.getText().toString().split(":");
                int secs =  Integer.parseInt(time[1]) + Integer.parseInt(time[0]) * 60;
                distanceTime.add(stepDistance, secs);
            }
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

    public void getHeight(){
        String url = DBConnect.serverURL + "/get_user_height";

        StringRequest StrHistReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){

            @Override
            public void onResponse(String response) {
                try {
                    height = new JSONObject(response).getInt("Height") / 100;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        System.out.println(error.toString());
                    }
                }
        ){
            // use params are specified here
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("email", "" + getSharedPreferences("UserData", 0).getString("email", null));

                return params;
            }
        };

        req_queue.addRequest(StrHistReq);
    }

    private class DistanceTime{
        private ArrayList<Float> distances; //distance in meters
        private ArrayList<Integer> times;    //time in seconds
        float maxSpeed = 0;

        private DistanceTime(){
            distances = new ArrayList<>();
            times = new ArrayList<>();
        }

        public void add(float distance, int time){
            this.distances.add(distance);
            times.add(time);
        }

        private float getInstantSpeed(int currentTime){


            if(times.get(times.size()-1) + 5 < currentTime)
                return 0.0f;


            if(distances.size()-2 < 0)
                return 0.0f;

            float instantSpeed = Float.NaN;
            for(int i = 2; i<times.size(); i++) {
                if((times.get(times.size() - 1) - times.get(times.size() - i) > 0)){
                    instantSpeed = ((distances.get(distances.size() - 1) - distances.get(distances.size() - i)) / (times.get(times.size() - 1) - times.get(times.size() - i))) * 3600 / 1000;
                    break;
                }
            }

            if(instantSpeed > maxSpeed){
                maxSpeed = instantSpeed;
            }

            return instantSpeed;
        }

        private float getMaxSpeed(){
            return maxSpeed;
        }

        private float getAverageSpeed(int currentTime){
            return (distances.get(distances.size()-1)/ currentTime) * 3600 / 1000;
        }
    }

    @Override
    public void onBackPressed() {
        if(counter.getText().toString().equals("00:00"))
            finish();


        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Exit")
                .setMessage("Do you want to exit without saving?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void onFinishClicked(View view){

        stop();

        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Finish")
                .setMessage("Do you want to save your result?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();

    }

    private void saveExercise(){
        // current date time
        final java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

        // add exercise to user history
        StringRequest Add_Req = new StringRequest(Request.Method.POST, DBConnect.serverURL + "/add_exercise_history",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.e(TAG,"Posted exercise");

                        saveSet(Integer.parseInt(response));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(CardioActivity.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        // Please connect your device to the Internet and try again
                        alertDialog.setMessage(error.toString());
                        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
        ){
            // use params are specified here
            // DoB, height, gender and weight are specified later, for now they have default values
            // effin not nulls
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("user", getSharedPreferences("UserData", 0).getString("email", null));
                params.put("date_time", String.valueOf(date));
                params.put("exercise_name", "Running");
                params.put("set_amount", String.valueOf(1));
                params.put("average_intensity", averageSpeed.getText().toString().split(" ")[1]);
                return params;
            }
        };
        VolleyProvider.getInstance(this).addRequest(Add_Req);
    }

    private void saveSet(int exerciseID){

        // current date time
        final java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

        // add exercise to user history
        StringRequest Add_Req = new StringRequest(Request.Method.POST, DBConnect.serverURL + "/add_exercise_history",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.e(TAG,"Posted exercise");

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(CardioActivity.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        // Please connect your device to the Internet and try again
                        alertDialog.setMessage(error.toString());
                        alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
        ){
            // use params are specified here
            // DoB, height, gender and weight are specified later, for now they have default values
            // effin not nulls
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("user", getSharedPreferences("UserData", 0).getString("email", null));
                params.put("date_time", String.valueOf(date));
                params.put("exercise_name", "Running");
                params.put("set_amount", String.valueOf(1));
                params.put("average_intensity", averageSpeed.getText().toString().split(" ")[1]);
                return params;
            }
        };
        VolleyProvider.getInstance(this).addRequest(Add_Req);

    }
}
