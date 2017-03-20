package g11.muscle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.UUID;

//Used to conver url strings to valid strings
import java.net.URL;
import java.net.URI;

public class ExerciseActivity extends AppCompatActivity {

    private static final String TAG = "ExerciseActivity";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static BluetoothDevice muscleDevice = null;

    //Bluetooth
    private ConnectedThread mConnectedThread;
    private BluetoothAdapter mBluetoothAdapter;
    //defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    //GUI
    private TextView exerciseView;
    private TextView kindView;
    private ImageView imgView;
    private TextView last_weightView;
    private TextView last_repsView;
    private TextView last_intensityView;
    private TextView current_weightView;
    private TextView current_repsView;
    private TextView current_intensityView;
    private Button button;
    private TextView descriptionView;

    private RequestQueue req_queue;

    //Exercise name
    private String exercise;
    //User email
    private String email;

    //Information to save
    private double weight;
    private int rep_count = 0;
    private int set = 1; //TODO what to do with sets??
    // sum of all intensities values  (devide by rep_count to get average intensity)
    private double total_intensity;
    private boolean ongoing;
    private StringBuilder strBuilder;

    //Handler
    private Handler mHandler;

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        // Information from previous activity
        final Intent intent = getIntent();
        exercise = intent.getStringExtra("exercise_name");
        email = intent.getStringExtra("email");
        ongoing = false;
        strBuilder = new StringBuilder();

        //GUI elements
        exerciseView  = (TextView)findViewById(R.id.exercise);
        kindView  = (TextView)findViewById(R.id.kind);
        imgView = (ImageView)findViewById(R.id.image);
        last_weightView = (TextView)findViewById(R.id.last_weight);
        last_repsView  = (TextView)findViewById(R.id.last_reps);
        last_intensityView = (TextView)findViewById(R.id.last_intensity);
        current_weightView  = (TextView)findViewById(R.id.current_weight);
        current_repsView = (TextView)findViewById(R.id.current_reps);
        current_intensityView = (TextView)findViewById(R.id.current_intensity);
        button = (Button)findViewById(R.id.button);
        descriptionView = (TextView)findViewById(R.id.description);

        // Exercise Name
        exerciseView.setText(exercise);

        // Create request queue
        req_queue = Volley.newRequestQueue(this);

        // get exercise information
        String urlEx = validUrlString("http://138.68.158.127/get_exercise/" + exercise);

        JsonObjectRequest Exer_Req = new JsonObjectRequest(
            JsonObjectRequest.Method.GET, urlEx, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try{
                    Log.e(TAG,response.toString());
                    descriptionView.setText(response.getString("Description"));
                    kindView.setText(response.getString("Kind"));

                    //Image name must be in lowercase
                    String DrawableName = response.getString("Image").toLowerCase();
                    //Can't have extension
                    if(DrawableName.contains("."))
                        DrawableName = DrawableName.split("\\.")[0];
                    int resID = getResources().getIdentifier(DrawableName,"drawable",getPackageName());
                    imgView.setImageResource(resID);
                } catch (JSONException je){
                    Log.e(TAG,"GET EXERCISE DATA EXCEPTION");
                    Log.e(TAG, je.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error Response
                System.out.println(error.toString());
            }
        }
        );

        // Add the request to the RequestQueue
        req_queue.add(Exer_Req);

        //Create handler
        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                String readMessage;
                try {
                    // Send the obtained bytes to the UI activity
                    readMessage = new String((byte[]) msg.obj, "UTF-8");
                    readMessage = readMessage.split("\0")[0];
                    strBuilder.append(readMessage);

                    int endOfLineIndex = strBuilder.indexOf("}");

                    if( endOfLineIndex >= 0){
                        JSONObject jsonObj = new JSONObject(strBuilder.substring(0,endOfLineIndex+1));
                        strBuilder.delete(0,endOfLineIndex+1);
                        if(jsonObj.has("status")) {
                            Log.i(TAG, jsonObj.getString("status"));
                        }
                        else {
                            //weight = Integer.parseInt(jsonObj.getString("weight"));
                            //rep_count = Integer.parseInt(jsonObj.getString("rep"));
                            weight = 40;
                            rep_count += 1;
                            total_intensity += Double.parseDouble(jsonObj.getString("meanAcc"));
                            current_weightView.setText(String.valueOf(weight));
                            current_repsView.setText(String.valueOf(rep_count));
                            current_intensityView.setText(String.valueOf(total_intensity/rep_count));
                        }
                    }
                } catch (JSONException je){
                    Log.e(TAG,"HANDLER EXCEPTION");
                    Log.e(TAG, je.toString());

                } catch (UnsupportedEncodingException uee){
                    Log.e(TAG, uee.toString());
                }
            }
        };

        // Get a handle on the bluetooth radio
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        enableAdapter();
    }

    @Override
    protected void onStart(){
        super.onStart();

        // Create request queue
        req_queue = Volley.newRequestQueue(this);
        getLastExerciseInfo();
    }

    //Get last exercise info
    private void getLastExerciseInfo(){
        // get last exercise information
        String urlLast = validUrlString("http://138.68.158.127/get_last_exercise_of_user/" + email + "/" + exercise);

        JsonObjectRequest Last_Ex_Req = new JsonObjectRequest(
                JsonObjectRequest.Method.GET, urlLast, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {

                try{
                    last_weightView.setText(response.getString("Weight"));
                    last_repsView.setText(response.getString("Repetitions"));
                    last_intensityView.setText(response.getString("Intensity"));
                    Log.e(TAG,response.toString());
                } catch (Exception e){
                    Log.e(TAG, e.toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // User doesn't have anything in it's exercise history
                Log.i(TAG,"User never did this exercise");
                last_weightView.setText("0");
                last_repsView.setText("0");
                last_intensityView.setText("0");
            }
        }
        );

        // Add the request to the RequestQueue
        req_queue.add(Last_Ex_Req);
    }

    //Convert url to valid url string
    private String validUrlString(String urlEx){
        try {
            URL url = new URL(urlEx);
            URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
            return uri.toASCIIString();
        }catch (Exception e){
            Log.e(TAG, "URL CONVERTER EXCEPTION");
            Log.e(TAG,e.toString());
        }
        return "";
    }

    @Override
    protected void onStop(){
        req_queue.cancelAll(this);
        mConnectedThread.cancel();
        super.onStop();
    }

    public void onClickStartButton(View view){
        if(ongoing){
            //Send stop signal to muscleDevice
            mConnectedThread.write("stop_sensors");
            mConnectedThread.cancel();
            //Send exercise to history of user
            pushExercise();

            //Set UI elements
            last_weightView.setText(String.valueOf(weight));
            last_repsView.setText(String.valueOf(rep_count));
            last_intensityView.setText(String.valueOf(total_intensity));
            current_weightView.setText("0");
            current_repsView.setText("0");
            current_intensityView.setText("0");

            ongoing = false;
            button.setText("Start");
        }
        else{
            //Send start signal to muscleDevice
            mConnectedThread.write("start_sensors");

            ongoing = true;
            button.setText("Stop");
        }
    }

    private void pushExercise() {

        // current date time
        java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

        // add exercise to user history
        String urlAdd = validUrlString("http://138.68.158.127/add_exercise_history/" + date + "/" + email + "/" + exercise + "/" + set + "/" + rep_count + "/" + weight + "/" + total_intensity/rep_count);
        JsonObjectRequest Add_Req = new JsonObjectRequest(
            JsonObjectRequest.Method.GET, urlAdd, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Log.e(TAG,"Posted exercise");
                    // TODO idk what to do where
                }

                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Error Response
                        System.out.println(error.toString());
                    }
                }

        );

        // Add the request to the RequestQueue
        req_queue.add(Add_Req);
    }

    public void createDevicePickDialog() {
        if( ExerciseActivity.muscleDevice != null){
            ConnectThread mConnectThread = new ConnectThread();
            mConnectThread.start();
            return;
        }

        final Set<BluetoothDevice> bdevices = mBluetoothAdapter.getBondedDevices();

        //Get bonded device names
        String[] bdname = new String[bdevices.size()];
        int i = 0;
        for(BluetoothDevice d : bdevices){
            bdname[i++] = d.getName() + d.getAddress();
        }

        //Create the listener for each item in the dialog list
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startBTConnection(bdevices.toArray(new BluetoothDevice[0])[which]);
            }
        };

        //Create the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Pick the Muscle device").setItems(bdname, listener).setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    //Enables adapter if needed
    //Sends error message if device doesn't support bluetooth or asks for it to be enabled if it is not already
    private void enableAdapter(){
        // Device does not support Bluetooth
        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }

        //Enable adapter if it is not already
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            return;
        }

        createDevicePickDialog();
    }

    private void startBTConnection(BluetoothDevice muscleDevice){
        // Start listening to the device
        ExerciseActivity.muscleDevice = muscleDevice;
        ConnectThread mConnectThread = new ConnectThread();
        mConnectThread.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // Get a handle on the bluetooth radio
                createDevicePickDialog();
            }
        }
    }

    //establish connection
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread() {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = ExerciseActivity.muscleDevice;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = ExerciseActivity.muscleDevice.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                Log.e(TAG, "Socket's create() method failed", e);
            }
            mmSocket = tmp;
        }

        public void run() {
            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    Log.e(TAG, "Could not close the client socket", closeException);
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            mConnectedThread = new ConnectedThread(mmSocket);
            mConnectedThread.start();
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Could not close the client socket", e);
            }
        }
    }

    //logic of connection
    private class ConnectedThread extends Thread {

        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {

            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {

            byte[] buffer = new byte[1024];     // buffer store for the stream
            int bytes;                          // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    mHandler.obtainMessage(MESSAGE_READ, bytes, 0, buffer).sendToTarget();
                    buffer = new byte[1024];
                } catch (Exception e) {
                    Log.i(TAG,e.toString());
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {

            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }
}
