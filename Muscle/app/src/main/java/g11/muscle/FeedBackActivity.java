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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.sql.Time;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Set;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

import android.media.MediaPlayer;

import android.graphics.Typeface;

import android.animation.ObjectAnimator;
import android.view.animation.DecelerateInterpolator;

import android.os.CountDownTimer;
import java.text.SimpleDateFormat;
import java.text.ParseException;

import g11.muscle.Classes.MuscleProgressItem;
import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;

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

    //Current Information
    private double weight;
    private int rep_count;
    private int set_count;
    private ArrayList<Double> intensities;
    private int restTime;

    // first rep of the exercise
    private boolean firstRep;

    private boolean firstStopped;

    // Timers stuff
    private boolean startedTimer = false;
    private Handler handler = new Handler();
    private SimpleDateFormat outFmt = new SimpleDateFormat("mm:ss",Locale.ENGLISH);

    private int last_checkpoint;

    private int exercise_history_id;

    //Plan information (Intent)
    private String plan_rest;
    private int plan_reps, plan_sets, plan_weight;
    private boolean plan;

    // Chart set colors
    private static final int[] Chart_Colors = {Color.rgb(57,106,177), Color.rgb(218,124,48), Color.rgb(62,150,81),
            Color.rgb(204,37,41), Color.rgb(83,81,84), Color.rgb(107,76,154), Color.rgb(146,36,40), Color.rgb(148,139,61)};

    //GUI
    private LineChart mChart;
    private TextView weightTV;
    private TextView setsTV;
    private TextView repsTV;
    private TextView repsTotalTV;
    private TextView nameTV;
    private ProgressBar progress;
    private ProgressBar progressSet;

    //Handler
    private static Handler mHandler;

    private StringBuilder strBuilder;

    private RequestQueue req_queue;

    private AlertDialog alertDialog;

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

        if(intent.getStringExtra("exercise_rest") != null) { // Plan Information
            plan_rest = intent.getStringExtra("exercise_rest");
            plan_reps = intent.getIntExtra("exercise_reps",0);
            plan_sets = intent.getIntExtra("exercise_sets",0);
            plan_weight = intent.getIntExtra("exercise_weight",0);

            plan = true;
        }
        else {
            plan = false;
        }

        //Sounds
        final MediaPlayer repSound = MediaPlayer.create(this, R.raw.boop);
        final MediaPlayer setSound = MediaPlayer.create(this, R.raw.waterdrop);

        alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Rest Time");
        alertDialog.setCanceledOnTouchOutside(true);
        alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //do whatever you want the back key to do
                alertDialog.dismiss();
            }
        });

        restTime = 0;

        firstRep = true;

        firstStopped = false; // don't want to count the first Stop if any rep was done

        intensities = new ArrayList<>();
        last_checkpoint = 10;

        // for bluetooth data
        strBuilder = new StringBuilder();

        // Create request queue
        req_queue = Volley.newRequestQueue(this);

        // gui
        mChart = (LineChart) findViewById(R.id.chart);
        weightTV = (TextView) findViewById(R.id.weightTextView);
        repsTV = (TextView) findViewById(R.id.Rep);
        repsTotalTV = (TextView) findViewById(R.id.repsTotal);
        nameTV =  (TextView) findViewById(R.id.Exercise_name);
        setsTV = (TextView) findViewById(R.id.feed_set);

        progress = (ProgressBar) findViewById(R.id.progressExercise);
        progressSet = (ProgressBar) findViewById(R.id.progressSet);

        rep_count = 0;
        set_count = 1;

        nameTV.setText(exercise);
        repsTV.setText(String.valueOf(0));

        setsTV.setText(String.valueOf(set_count));

        if(plan){
            repsTotalTV.setVisibility(View.VISIBLE);
            repsTotalTV.setText(String.valueOf(plan_reps));

            progress.setMax(plan_reps * 100);
            progress.setProgress(rep_count*100);


            progressSet.setMax(plan_sets * 100);
            ObjectAnimator Animation = ObjectAnimator.ofInt(progressSet, "progress", progressSet.getProgress(), set_count * 100);
            Animation.setDuration(500);
            Animation.setInterpolator(new DecelerateInterpolator());
            Animation.start();
            progressSet.setProgress(set_count*100);
        }else{
            progressSet.setMax(100);
            progress.setMax(100);

            ObjectAnimator animation = ObjectAnimator.ofInt(progress, "progress", 0, 100);
            animation.setDuration(500);
            animation.setInterpolator(new DecelerateInterpolator());
            animation.start();

            ObjectAnimator Animation = ObjectAnimator.ofInt(progressSet, "progress", 0,100);
            Animation.setDuration(500);
            Animation.setInterpolator(new DecelerateInterpolator());
            Animation.start();
        }

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

                        if(firstRep && set_count == 1) {
                            if(!plan)
                                saveExercise();
                            else
                                saveExerciseWithPlan();
                            firstRep = false;
                        }

                        if (jsonObj.has("stopped")) {
                            if(!firstStopped)
                                return;

                            //getExpectedSetResults();

                            firstStopped = false;

                            restTime = 3; // stopped is received after 3 sec

                            if(setSound.isPlaying()) {
                                setSound.pause();
                                setSound.seekTo(0);
                            }
                            setSound.start();

                            /*
                            if(plan) {
                                timeAlert(plan_rest);
                            }
                            else*/
                            TimeCountStart();
                        }
                        else{
                            firstStopped = true;

                            if(restTime != 0) { // save old Set and reset
                                TimeCountStop();
                                saveSet(new ArrayList<>(intensities),exercise_history_id,set_count,rep_count,weight,restTime);

                                set_count += 1; // new set

                                // Clear values
                                restTime = 0;
                                rep_count = 0;
                                if(!intensities.isEmpty())
                                    intensities.clear();

                                repsTV.setText(String.valueOf(rep_count));
                                progress.setProgress(rep_count*100); // reset Reps progress bar

                                setsTV.setText(String.valueOf(set_count));

                                if(plan) { // set animation
                                    ObjectAnimator animation = ObjectAnimator.ofInt(progressSet, "progress", progressSet.getProgress(), set_count * 100);
                                    animation.setDuration(500);
                                    animation.setInterpolator(new DecelerateInterpolator());
                                    animation.start();
                                }else{
                                    ObjectAnimator animation = ObjectAnimator.ofInt(progressSet, "progress", 0,100);
                                    animation.setDuration(500);
                                    animation.setInterpolator(new DecelerateInterpolator());
                                    animation.start();
                                }
                            }

                            int checkPoint = Integer.parseInt(jsonObj.getString("checkpoint"));

                            if(checkPoint <= last_checkpoint){
                                last_checkpoint = checkPoint;

                                weight = Double.parseDouble(jsonObj.getString("weight"));
                                weightTV.setText(String.valueOf((int)weight));

                                rep_count += 1;
                                repsTV.setText(String.valueOf(rep_count));

                                if(plan) {
                                    ObjectAnimator animation = ObjectAnimator.ofInt(progress, "progress", progress.getProgress(), rep_count * 100);
                                    animation.setDuration(500);
                                    animation.setInterpolator(new DecelerateInterpolator());
                                    animation.start();
                                    progress.setProgress(rep_count*100);
                                }else{ // 'Free' Mode
                                    ObjectAnimator animation = ObjectAnimator.ofInt(progress, "progress", 0, 100);
                                    animation.setDuration(500);
                                    animation.setInterpolator(new DecelerateInterpolator());
                                    animation.start();
                                }

                                if(repSound.isPlaying()){
                                    repSound.pause();
                                    repSound.seekTo(0);
                                }
                                repSound.start();

                                intensities.add(Double.parseDouble(jsonObj.getString("speed")));
                                addEntry(intensities.get(intensities.size()-1));
                            }
                            else{
                                last_checkpoint = checkPoint;
                                intensities.add(Double.parseDouble(jsonObj.getString("speed")));
                                removeLastEntry();
                                addEntry(intensities.get(intensities.size()-1));
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
        // Get a handle on the bluetooth radio
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
    }
    
    @Override
    protected void onStop(){
        req_queue.cancelAll(this);
        if(mConnectedThread != null) mConnectedThread.cancel();
        super.onStop();
    }

    @Override
    protected void onStart(){
        super.onStart();
        enableAdapter();
    }

    @Override
    public void onBackPressed() {
        if(restTime != 0) {
            saveSet(new ArrayList<>(intensities),exercise_history_id,set_count,rep_count,weight,restTime);
            TimeCountStop();
        }

        super.onBackPressed();
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
    }

    
    private Runnable runnableTimer = new Runnable() {
        @Override
        public void run() {
            if(!plan)
                alertDialog.setMessage(outFmt.format(new Date((restTime)*1000)));
            else
                alertDialog.setMessage(outFmt.format(new Date((restTime)*1000)) + "    (" + plan_rest.substring(2) + ")");

            if(startedTimer){
                restTime++;
                handler.postDelayed(runnableTimer, 1000);
            }
        }
    };

    public void TimeCountStop() {
        if(startedTimer){
            handler.removeCallbacksAndMessages(runnableTimer);
            startedTimer = false;
            alertDialog.cancel();
        }
    }

    public void TimeCountStart() {
        startedTimer = true;
        handler.postDelayed(runnableTimer, 1000);

        if(!plan)
            alertDialog.setMessage(outFmt.format(new Date((restTime)*1000)));
        else
            alertDialog.setMessage(outFmt.format(new Date((restTime)*1000)) + "    (" + plan_rest.substring(2) + ")");
        alertDialog.show();
        restTime++;
    }

    /*private void timeAlert(String time){
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.UK);
        Date convertedDate = new Date();

        try {
            convertedDate = dateFormat.parse(time);
        } catch (ParseException e) {
            Log.e("FeedBackAc","Convert Time error:\n" + e.toString());
        }

        alertDialog.setMessage(time);
        alertDialog.show();

        new CountDownTimer(convertedDate.getTime() - 3000, 1000) { //stopped is received after 3 sec
            @Override
            public void onTick(long millisUntilFinished) {
                restTime++;
                alertDialog.setMessage(outFmt.format(new Date(millisUntilFinished)));
            }

            @Override
            public void onFinish() {
                alertDialog.cancel();
                TimeCountStart();
            }
        }.start();
    }*/

    private void chartSetup() {
        // Font for chart text
        Typeface mTfLight = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        // Chart Listener
        mChart.setOnChartValueSelectedListener(this);

        // enable description text
        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("Intensity/Reps");
        mChart.getDescription().setTextColor(Color.WHITE);

        // enable touch gestures
        mChart.setTouchEnabled(true);

        // enable scaling and dragging
        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.setDrawGridBackground(false);

        // if disabled, scaling can be done on x- and y-axis separately
        mChart.setPinchZoom(true);

        // set an alternative background color
        mChart.setBackgroundColor(Color.TRANSPARENT);

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
        //leftAxis.setAxisMaximum(4f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.invalidate();
    }

    private void addEntry(double x) {
        alertDialog.dismiss();

        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(set_count-1);
            // set.addEntry(...); // can be called as well

            if (set == null) {
                set = createSet();

                // turn on values when sets == 1
                /*if(set_count == 1)
                    set.setDrawValues(true);
                else{
                    ILineDataSet tmpSet = data.getDataSetByIndex(0);
                    tmpSet.setDrawValues(false);
                }*/

                data.addDataSet(set);
            }

            data.addEntry(new Entry(set.getEntryCount(), (float) x),set_count-1);
            data.notifyDataChanged();

            // let the chart know it's data has changed
            mChart.notifyDataSetChanged();

            // limit the number of visible entries
            mChart.setVisibleXRangeMaximum(30);
            //mChart.setVisibleYRange(30, AxisDependency.LEFT);

            // move to the latest entry
            mChart.moveViewToX(set.getEntryCount());

            // this automatically refreshes the chart (calls invalidate())
            //mChart.moveViewTo(data.getXValCount()-7, 55f,AxisDependency.LEFT);
        }
    }

    private void removeLastEntry() {

        LineData data = mChart.getData();

        if (data != null) {
            ILineDataSet set = data.getDataSetByIndex(set_count-1);

            if (set != null) {
                Entry e = set.getEntryForXValue(set.getEntryCount() - 1, Float.NaN);

                data.removeEntry(e, set_count-1);
                // or remove by index
                // mData.removeEntryByXValue(xIndex, dataSetIndex);
                data.notifyDataChanged();
                mChart.notifyDataSetChanged();
                mChart.invalidate();
            }
        }
    }

    private LineDataSet createSet() {
        LineDataSet set = new LineDataSet(null, "Set "+set_count);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        //Chart_Colors[set_count % Chart_Colors.length]
        set.setColor(Chart_Colors[set_count % Chart_Colors.length]); // LINE COLOR

        set.setCircleColor(Color.rgb(29,31,34));
        set.setCircleColorHole(Color.rgb(29,31,34));
        set.setDrawCircles(false);   // KEEP CIRCLES ???

        set.setLineWidth(2f);
        set.setCircleRadius(3f);
        set.setFillAlpha(65);
        set.setFillColor(Color.rgb(235,57,20));
        set.setHighLightColor(Color.rgb(244, 117, 117));

        set.setValueTextColor(Color.WHITE);
        set.setValueTextSize(8f);
        set.setDrawValues(false);
        return set;
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.e("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.e("Nothing selected", "Nothing selected.");
    }

    private void getExpectedSetResults(){
        String url = DBConnect.serverURL + "/get_expected_exercise_result";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("ExpectedResponse",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        System.out.println(error.toString());
                        //progressBar.setVisibility(View.GONE);
                    }
                }
        ) {
            // use params are specified here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                Log.e("GET_EXPECTED",email + " " + exercise + " " + String.valueOf(weight) + " " + String.valueOf(set_count));
                params.put("user_email", email);
                params.put("exercise_name", exercise);
                params.put("weight", String.valueOf(weight));
                params.put("set_number", String.valueOf(set_count));
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(this).addRequest(StrHistReq);
    }

    private void saveExercise(){
        // current date time
        final java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

        // add exercise to user history
        StringRequest Add_Req = new StringRequest(Request.Method.POST, DBConnect.serverURL + "/add_exercise_history",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.e(TAG,response);
                        exercise_history_id = Integer.parseInt(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(FeedBackActivity.this).create();
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
                Log.e("VALUES",email + " " + ""+date + " " + exercise);
                params.put("user", email);
                params.put("date_time", ""+date);
                params.put("exercise_name",exercise);
                params.put("set_amount", "null");
                params.put("average_intensity","null");
                params.put("plan_id","null");
                return params;
            }
        };
        VolleyProvider.getInstance(this).addRequest(Add_Req);
    }

    private void saveExerciseWithPlan(){
        // current date time
        final java.sql.Timestamp date = new java.sql.Timestamp(new java.util.Date().getTime());

        // add exercise to user history
        StringRequest Add_Req = new StringRequest(Request.Method.POST, DBConnect.serverURL + "/add_exercise_history_with_plan",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.e(TAG,response);
                        exercise_history_id = Integer.parseInt(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(FeedBackActivity.this).create();
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
                params.put("user", email);
                params.put("date_time", ""+date);
                params.put("exercise_name",exercise);
                return params;
            }
        };
        VolleyProvider.getInstance(this).addRequest(Add_Req);
    }

    private void saveSet(final ArrayList<Double> intensities,final int exercise_history_id,final int set_count,final int rep_count,
                         final double weight,final int restTime){
        // add exercise to user history
        StringRequest Add_Req = new StringRequest(Request.Method.POST, DBConnect.serverURL + "/add_set",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        Log.e("Add Set",response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        android.app.AlertDialog alertDialog = new android.app.AlertDialog.Builder(FeedBackActivity.this).create();
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

                Log.e("SAVESET",""+exercise_history_id + " - " + String.valueOf(set_count) + " - " + String.valueOf(rep_count) + " - "
                            + " - " + String.valueOf((int)weight) + " - " + String.valueOf(intensities.get(intensities.size()-1)) +
                            " - ");
                params.put("exercise_history_id", ""+exercise_history_id);
                params.put("set_number", String.valueOf(set_count));
                params.put("repetitions", String.valueOf(rep_count));
                params.put("weight", String.valueOf((int)weight));
                Log.e("INTENSITIES",intensities.toString());
                params.put("intensity", String.valueOf(intensities.get(intensities.size()-1)));
                Time timeRest = new Time((restTime)*1000);
                // in Android 7.0+ new Time start at 1h
                params.put("resting_time", "00" + String.valueOf(timeRest).substring(2));

                double intensityAvg = calculateAverage(intensities);
                double stdDeviation = 0;
                for(Double x : intensities){
                    stdDeviation += Math.pow((x-intensityAvg),2);
                }
                stdDeviation /= intensities.size();
                params.put("intensity_deviation", ""+stdDeviation);
                return params;
            }
        };
        VolleyProvider.getInstance(this).addRequest(Add_Req);
    }

    private double calculateAverage(ArrayList <Double> marks) {
        double sum = 0;
        if(!marks.isEmpty()) {
            for (Double mark : marks) {
                sum += mark;
            }
            return sum / marks.size();
        }
        return sum;
    }

    /****************************
     *    BLUETOOTH METHODS     *
     ****************************/
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
                FeedBackActivity.this.runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        Toast.makeText(FeedBackActivity.this, "Bluetooth Connected", Toast.LENGTH_SHORT).show();
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
}
