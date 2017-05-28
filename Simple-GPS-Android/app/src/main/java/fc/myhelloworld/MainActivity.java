package fc.myhelloworld;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


public class MainActivity extends Activity {

    LocationListener locationListenerGPS = new LocationListener() {
        public void onLocationChanged(Location location) {
            final TextView view_gps_lat = (TextView) findViewById(R.id.display_gps_latitude);
            final TextView view_gps_lng = (TextView) findViewById(R.id.display_gps_longitude);
            final TextView view_gps_accuracy = (TextView) findViewById(R.id.display_gps_accuracy);
            final TextView view_gps_extra = (TextView) findViewById(R.id.display_gps_extra);
            String lat = String.valueOf(location.getLatitude());
            String lng = String.valueOf(location.getLongitude());
            String acc = String.valueOf(location.getAccuracy());

            view_gps_lat.setText("latitude: "+lat);
            view_gps_lng.setText("longitude: "+lng);
            view_gps_accuracy.setText("accuracy: "+acc);
            if(location.getExtras() != null){
                Bundle extra = location.getExtras();
                String estr = "";
                for(String s : extra.keySet()) {
                    estr += "  "+s+": "+extra.get(s).toString()+"\n";
                }
                view_gps_extra.setText(estr);
            }

            updatedLastUpdated((TextView) findViewById(R.id.display_gps_updated));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            final TextView view_gps = (TextView) findViewById(R.id.display_gps_status);
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
            view_gps.setText(str);

        }

        public void onProviderEnabled(String provider) {
            final TextView view_gps = (TextView) findViewById(R.id.display_gps_status_enabled);
            view_gps.setText(provider+" enabled");
        }

        public void onProviderDisabled(String provider) {
            final TextView view_gps = (TextView) findViewById(R.id.display_gps_status_enabled);
            view_gps.setText(provider+" disabled");
        }
    };


    LocationListener locationListenerGSM = new LocationListener() {
        public void onLocationChanged(Location location) {
            final TextView view_gsm_lat = (TextView) findViewById(R.id.display_gsm_latitude);
            final TextView view_gsm_lng = (TextView) findViewById(R.id.display_gsm_longitude);
            final TextView view_gsm_accuracy = (TextView) findViewById(R.id.display_gsm_accuracy);
            final TextView view_gsm_extra = (TextView) findViewById(R.id.display_gsm_extra);
            String lat = String.valueOf(location.getLatitude());
            String lng = String.valueOf(location.getLongitude());
            String acc = String.valueOf(location.getAccuracy());

            view_gsm_lat.setText("latitude: "+lat);
            view_gsm_lng.setText("longitude: "+lng);
            view_gsm_accuracy.setText("accuracy: "+acc);
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
                view_gsm_extra.setText(estr);
            }

            updatedLastUpdated((TextView) findViewById(R.id.display_gsm_updated));
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            final TextView view_gsm = (TextView) findViewById(R.id.display_gsm_status);
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
            view_gsm.setText(str);

        }

        public void onProviderEnabled(String provider) {
            final TextView view_gsm = (TextView) findViewById(R.id.display_gsm_status_enabled);
            view_gsm.setText(provider+" enabled");
        }

        public void onProviderDisabled(String provider) {
            final TextView view_gsm = (TextView) findViewById(R.id.display_gsm_status_enabled);
            view_gsm.setText(provider+" disabled");
        }
    };

    protected void updatedLastUpdated(TextView t){
        String date = DateFormat.getDateTimeInstance().format(System.currentTimeMillis());
        t.setText("updated: "+date);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        LocationManager locationManagerGPS = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManagerGPS.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            locationListenerGPS.onProviderEnabled("GPS");
        }else{
            locationListenerGPS.onProviderDisabled("GPS");
        }
        locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGPS);




        LocationManager locationManagerGSM = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(locationManagerGSM.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            locationListenerGSM.onProviderEnabled("Network");
        }else{
            locationListenerGSM.onProviderDisabled("Network");
        }
        locationManagerGSM.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerGSM);
    }

    public void sendMessage(View view) {
        if(true)
            return;
        //runOnUiThread(new Runnable() {
        //public void run() {
        List<TextView> vs = new ArrayList<TextView>();
        vs.add((TextView) findViewById(R.id.display_gps_status));
        vs.add((TextView) findViewById(R.id.display_gps_status_enabled));
        vs.add((TextView) findViewById(R.id.display_gsm_status));
        vs.add((TextView) findViewById(R.id.display_gsm_status_enabled));

        for(TextView tv : vs){
            tv.setText("Penis mode activated");
        }

        List<TextView> v = new ArrayList<TextView>();
        v.add((TextView) findViewById(R.id.display_gps_latitude));
        v.add((TextView) findViewById(R.id.display_gps_longitude));
        v.add((TextView) findViewById(R.id.display_gps_accuracy));
        v.add((TextView) findViewById(R.id.display_gps_extra));


        v.add((TextView) findViewById(R.id.display_gsm_latitude));
        v.add((TextView) findViewById(R.id.display_gsm_longitude));
        v.add((TextView) findViewById(R.id.display_gsm_accuracy));
        v.add((TextView) findViewById(R.id.display_gsm_extra));

        for(TextView tv : v){
            tv.setText("Penis");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
