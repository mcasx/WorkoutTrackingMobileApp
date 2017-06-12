package g11.muscle;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

import g11.muscle.DB.MuscleDbContract;
import g11.muscle.R;

public class SetupActivity extends AppCompatActivity {

    private static final String TAG = "FeedBackActivity";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private static BluetoothDevice muscleDevice = null;
    //Bluetooth
    private SetupActivity.ConnectedThread mConnectedThread;
    private BluetoothAdapter mBluetoothAdapter;
    private StringBuilder strBuilder;

    //defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status
    private static Handler mHandler;

    private boolean weight_mode;

    private android.app.AlertDialog alertDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        strBuilder = new StringBuilder();
        setContentView(R.layout.activity_setup);
        setTitle("Calibration");

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        // when asking for weight sensor it return 2 '}'
        weight_mode = false;

        alertDialog = new android.app.AlertDialog.Builder(SetupActivity.this).create();
        setMessageRead();
    }

    protected void onStop(){
        if(mConnectedThread != null){
            mConnectedThread.write("testModeOff");
            mConnectedThread.cancel();
        }
        super.onStop();
    }

    @Override
    protected void onStart(){
        super.onStart();
        enableAdapter();
    }

    public void onCalibrateClick(View view){
        if(mConnectedThread != null) {
            mConnectedThread.write("testModeOn");
            alertDialog.setMessage("Push the bar");
            alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
            alertDialog.show();
        }
        else
            Log.e("BLUETOOTH", "NOT CONNECTED");
    }

    private void setMessageRead(){
        mHandler = new Handler(){
            public void handleMessage(android.os.Message msg){
                String readMessage;
                try {
                    // Send the obtained bytes to the UI activity
                    readMessage = new String((byte[]) msg.obj, "UTF-8");
                    readMessage = readMessage.split("\0")[0];
                    strBuilder.append(readMessage);

                    int endOfLineIndex = strBuilder.indexOf("}");

                    if(weight_mode)
                        endOfLineIndex++;

                    Log.e("MESSAGE",readMessage);
                    if( endOfLineIndex >= 0){
                        JSONObject jsonObj = new JSONObject(strBuilder.substring(0,endOfLineIndex+1));
                        strBuilder.delete(0,endOfLineIndex+1);

                        if (jsonObj.has("test_mode")){
                            String tmp =  jsonObj.getString("test_mode");

                            if (tmp.equals("on")){
                                alertDialog.dismiss();
                                Toast.makeText(getApplicationContext(), "Testing Weight Sensors...", Toast.LENGTH_SHORT).show();

                                Handler handler = new Handler();
                                final Runnable r = new Runnable() {
                                    public void run() {
                                        Log.e("WRITE","NOW");
                                        mConnectedThread.write("weight");
                                    }
                                };
                                handler.postDelayed(r, 2000);

                                weight_mode = true;
                            }
                        }
                        if (jsonObj.has("weight_sensors")) {
                            JSONObject weights = jsonObj.getJSONObject("weight_sensors");
                            String[] weights_strings = {"5", "10", "15", "20", "25", "30", "35", "40", "45", "50", "55", "60"};
                            Resources res = getResources();

                            int i = 1;
                            for(String s: weights_strings){
                                int id = res.getIdentifier("sensor" + i, "id", getApplicationContext().getPackageName());
                                if(weights.getInt(s) >= 520){
                                    ((ImageView)findViewById(id)).setImageResource(R.mipmap.sensor_green);
                                }
                                i++;
                            }

                            weight_mode = false;
                            alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(getApplicationContext(), "Testing Checkpoint Sensors...", Toast.LENGTH_SHORT).show();
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                            mConnectedThread.write("checkpoint");
                        }
                        if (jsonObj.has("message")) {
                            String checkpoint = jsonObj.getString("message");
                            Resources res = getResources();
                            Character lastChar = checkpoint.charAt(checkpoint.length() - 1);

                            if(Character.isDigit(lastChar)) {
                                int id = res.getIdentifier("sensorCheck" + lastChar, "id", getApplicationContext().getPackageName());
                                ((ImageView) findViewById(id)).setImageResource(R.mipmap.sensor_green);
                            }
                        }
                        System.out.println("Get Data from bluetooth");
                    }
                } catch (JSONException je){
                    Log.e(TAG,"HANDLER EXCEPTION");
                    Log.e(TAG, je.toString());
                } catch (UnsupportedEncodingException uee){
                    Log.e(TAG, uee.toString());
                }
            }
        };
    }

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
        SetupActivity.muscleDevice = muscleDevice;
        SetupActivity.ConnectThread mConnectThread = new SetupActivity.ConnectThread();
        mConnectThread.start();
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
                    Log.e(TAG,e.toString());
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) {
                Log.e(TAG, "Could not write into mmOutStream", e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error during connectedThread cancel", e);
            }
        }
    }




    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread() {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = SetupActivity.muscleDevice;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = SetupActivity.muscleDevice.createRfcommSocketToServiceRecord(MY_UUID);
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
                SetupActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(SetupActivity.this, "Bluetooth Connected", Toast.LENGTH_SHORT).show();
                    }
                });
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
            mConnectedThread = new SetupActivity.ConnectedThread(mmSocket);
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

    public void createDevicePickDialog() {
        if( SetupActivity.muscleDevice != null){
            SetupActivity.ConnectThread mConnectThread = new SetupActivity.ConnectThread();
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
                SetupActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(SetupActivity.this, "Connecting to Bluetooth...", Toast.LENGTH_SHORT).show();
                    }
                });

                Log.e(TAG, bdevices.toArray(new BluetoothDevice[0])[which].toString());
                startBTConnection(bdevices.toArray(new BluetoothDevice[0])[which]);
                //getSharedPreferences("UserData", 0).edit().
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
}
