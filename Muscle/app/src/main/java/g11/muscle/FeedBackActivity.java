package g11.muscle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import android.graphics.Typeface;

public class FeedBackActivity extends AppCompatActivity implements
        OnChartValueSelectedListener {

    private static final String TAG = "FeedBackActivity";

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    private static BluetoothDevice muscleDevice = null;

    //Bluetooth
    private FeedBackActivity.ConnectedThread mConnectedThread;
    private BluetoothAdapter mBluetoothAdapter;

    //defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    //Information to save
    private double weight;
    private int rep_count = 0;
    private int set = 1; //TODO what to do with sets??
    // sum of all intensities values  (devide by rep_count to get average intensity)
    private double total_intensity;

    //GUI
    private LineChart mChart;
    private TextView weigthTV;

    // multi adds chart thread
    private Thread chTread;

    //Handler
    private static Handler mHandler;

    private StringBuilder strBuilder;

    private RequestQueue req_queue;

    // Intent vars
    private String email;
    private String exercise;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed_back);

        // Information from previous activity
        final Intent intent = getIntent();
        email = intent.getStringExtra("email");
        exercise = intent.getStringExtra("exercise_name");

        // for bluetooth data
        strBuilder = new StringBuilder();

        // Create request queue
        req_queue = Volley.newRequestQueue(this);

        // gui
        mChart = (LineChart) findViewById(R.id.chart);
        weigthTV = (TextView) findViewById(R.id.weightTextView);

        // start chart
        chartSetup();

        //Create handler bluetooth
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
                            addEntry(Double.parseDouble(jsonObj.getString("meanAcc")));
                            weigthTV.setText(String.valueOf(weight));

                            System.out.println("Get Data from bluetooth");
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
    }

    @Override
    protected void onStop(){
        //Send exercise to history of user
        //pushExercise();
        req_queue.cancelAll(this);
        if(mConnectedThread != null) mConnectedThread.cancel();
        if (chTread != null) {
            chTread.interrupt();
        }
        super.onStop();
    }

    @Override
    protected void onStart(){
        super.onStart();
        enableAdapter();
    }

    public void onClickAddEntryButton(View view){
        addEntry();
    }

    public void onClickClearButton(View view) {
        mChart.clearValues();
        Toast.makeText(this, "Chart cleared!", Toast.LENGTH_SHORT).show();

    }

    public void onClickMultiButton(View view) {
        feedMultiple();
    }

    private void chartSetup() {
        // Font for chart text
        Typeface mTfLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        // Chart Listener
        mChart.setOnChartValueSelectedListener(this);


        // enable description text
        mChart.getDescription().setEnabled(true);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.BLACK);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        // add empty data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();

        // modify the legend ...
        l.setForm(Legend.LegendForm.LINE);
        l.setTypeface(mTfLight);
        l.setTextColor(Color.WHITE);

        XAxis xl = mChart.getXAxis();
        xl.setTypeface(mTfLight);
        xl.setTextColor(Color.WHITE);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMaximum(100f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.invalidate();
    }

    private void addEntry() {
        addEntry(-1234567890);
    } //Add random value - TEST ONLY

    private void addEntry(double x) {

        LineData data = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();
                data.addDataSet(set);
            }

            // random value for testing
            if (x == -1234567890)
                data.addEntry(new Entry(set.getEntryCount(), (float) (Math.random() * 40) + 30f), 0);
            else
                data.addEntry(new Entry(set.getEntryCount(), (float) x),0);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(120);
            //mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(data.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            //mChart.moveViewTo(data.getXValCount()-7, 55f,AxisDependency.LEFT);
        }
    }

    private LineDataSet createSet() {

        LineDataSet set = new LineDataSet(null, "meanAcc");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColor(Color.rgb(235,57,20));     // LINE COLOR - RED
        set.setCircleColor(Color.WHITE);
        set.setLineWidth(2f);
        set.setCircleRadius(4f);
        set.setFillAlpha(65);
        set.setFillColor(Color.rgb(235,57,20));
        set.setHighLightColor(Color.rgb(244, 117, 117));
        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(9f);
        set.setDrawValues(true);
        return set;
    }


    private void feedMultiple() {

        if (chTread != null)
            chTread.interrupt();

        final Runnable runnable = new Runnable() {

            @Override
            public void run() {
                addEntry();
            }
        };

        chTread = new Thread(new Runnable() {

            @Override
            public void run() {
                for (int i = 0; i < 1000; i++) {

                    // Don't generate garbage runnables inside the loop.
                    runOnUiThread(runnable);

                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }
        });

        chTread.start();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }


    private void pushExercise() {

        // current date time
        final java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

        // add exercise to user history
        StringRequest Add_Req = new StringRequest(Request.Method.POST, "http://138.68.158.127/add_exercise_history",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.e(TAG,"Posted exercise");
                        // TODO idk what to do here
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        AlertDialog alertDialog = new AlertDialog.Builder(FeedBackActivity.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        // Please connect your device to the Internet and try again
                        alertDialog.setMessage(error.toString());
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
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
                params.put("user", email);
                params.put("date_time", String.valueOf(date));
                params.put("exercise_name", exercise);
                params.put("set_amount", String.valueOf(rep_count));
                params.put("average_intensity", String.valueOf(total_intensity/rep_count));
                return params;
            }
        };

        VolleyProvider.getInstance(this).addRequest(Add_Req);
    }


    /*
     * BLUETOOTH METHODS
     */
    public void createDevicePickDialog() {
        if( FeedBackActivity.muscleDevice != null){
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
        FeedBackActivity.muscleDevice = muscleDevice;
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
            mmDevice = FeedBackActivity.muscleDevice;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = FeedBackActivity.muscleDevice.createRfcommSocketToServiceRecord(MY_UUID);
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

            write("start_sensors");
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
                Log.e(TAG, "Could not write into mmOutStream", e);
            }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                write("stop_sensors");
                mmSocket.close();
            } catch (IOException e) {
                Log.e(TAG, "Error during connectedThread cancel", e);
            }
        }
    }
}
