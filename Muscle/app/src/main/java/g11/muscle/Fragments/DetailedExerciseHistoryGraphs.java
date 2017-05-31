package g11.muscle.Fragments;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import g11.muscle.DB.DBConnect;
import g11.muscle.DetailedExerciseHistoryActivity;
import g11.muscle.MPChartJava.HistRadarMarkerView;
import g11.muscle.MPChartJava.LineMarkerView;
import g11.muscle.R;
import g11.muscle.DB.VolleyProvider;

public class DetailedExerciseHistoryGraphs extends Fragment implements OnChartValueSelectedListener {

    private static final String TAG = "MyPlanFragment";

    // GUI
    private BarChart bChart;
    private LineChart lChart;

    // chart font
    Typeface mTfLight;

    private String email;
    private String exercise_name;

    // Radar Chart data
    private ArrayList<BarEntry> thisExEntries = new ArrayList<>();
    private ArrayList<BarEntry> myAvgEntries = new ArrayList<>();
    private ArrayList<BarEntry> globalAvgEntries = new ArrayList<>();

    private BarDataSet myAvgSet;
    private BarDataSet globalAvgSet;
    private BarDataSet thisExSet;

    public DetailedExerciseHistoryGraphs() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.e(TAG,getActivity().toString());
        email = getActivity().getIntent().getStringExtra("email");
        exercise_name = getActivity().getIntent().getStringExtra("exercise_name");

        getUserAverage();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Font for charts text
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf");

        View fView = inflater.inflate(R.layout.fragment_detailed_exercise_history_stats, container, false);

        bChart = (BarChart) fView.findViewById(R.id.bar_chart);
        lChart = (LineChart) fView.findViewById(R.id.detail_line_Chart);

        BarSetup();
        LineSetup();
        // Inflate the layout for this fragment
        return fView;
    }

    private void getUserAverage(){
        String url = DBConnect.serverURL + "/get_user_avg_stats_ex";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmp_int = new JSONObject(jsonArray.getString(i)).getString("avg_int");
                                    String tmp_reps = new JSONObject(jsonArray.getString(i)).getString("avg_reps");
                                    String tmp_rest = new JSONObject(jsonArray.getString(i)).getString("avg_rest");
                                    String tmp_sets = new JSONObject(jsonArray.getString(i)).getString("avg_sets");
                                    String tmp_weight = new JSONObject(jsonArray.getString(i)).getString("avg_weight");

                                    int duration = parseData(tmp_rest);

                                    // Adding values to Bar My average data base
                                    // Order: Repetitions - Intensity - Rest - Weight - Sets
                                    myAvgEntries.add(new BarEntry(i,Float.parseFloat(tmp_reps)));
                                    myAvgEntries.add(new BarEntry(i,Float.parseFloat(tmp_int)));
                                    myAvgEntries.add(new BarEntry(i,(float) duration));
                                    myAvgEntries.add(new BarEntry(i,Float.parseFloat(tmp_weight)));
                                    myAvgEntries.add(new BarEntry(i,Float.parseFloat(tmp_sets)));
                                }
                            } catch (JSONException je){
                                Log.e(TAG, je.toString());
                            }

                            myAvgSet = new BarDataSet(myAvgEntries, "My Average");
                            myAvgSet.setColor(Color.rgb(121, 162, 175));
                            
                            // get Next Data set
                            getGlobalAverage();

                        }catch (JSONException e2){
                            e2.printStackTrace();
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
                params.put("user_email", email);
                params.put("exercise",exercise_name);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }

    private void getGlobalAverage(){
        String url = DBConnect.serverURL + "/get_avg_stats_ex";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmp_int = new JSONObject(jsonArray.getString(i)).getString("avg_int");
                                    String tmp_reps = new JSONObject(jsonArray.getString(i)).getString("avg_reps");
                                    String tmp_rest = new JSONObject(jsonArray.getString(i)).getString("avg_rest");
                                    String tmp_sets = new JSONObject(jsonArray.getString(i)).getString("avg_sets");
                                    String tmp_weight = new JSONObject(jsonArray.getString(i)).getString("avg_weight");

                                    int duration = parseData(tmp_rest);

                                    // Adding values to Bar Global average data base
                                    // Order: Repetitions - Intensity - Rest - Weight - Sets
                                    globalAvgEntries.add(new BarEntry(i,Float.parseFloat(tmp_reps)));
                                    globalAvgEntries.add(new BarEntry(i,Float.parseFloat(tmp_int)));
                                    globalAvgEntries.add(new BarEntry(i,(float) duration));
                                    globalAvgEntries.add(new BarEntry(i,Float.parseFloat(tmp_weight)));
                                    globalAvgEntries.add(new BarEntry(i,Float.parseFloat(tmp_sets)));
                                }
                            } catch (JSONException je){
                                Log.e(TAG, je.toString());
                            }

                            globalAvgSet = new BarDataSet(globalAvgEntries, "Global Average");
                            globalAvgSet.setColor(Color.rgb(73, 162, 169));

                            // get Next Data set
                            getThisAverage();

                        }catch (JSONException e2){
                            e2.printStackTrace();
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
                params.put("exercise",exercise_name);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }

    private void getThisAverage(){
        String url = DBConnect.serverURL + "/get_this_ex_avg_stats";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG + " This Ex",response);
                            JSONArray jsonArray = new JSONArray(response);
                            Log.e(TAG,jsonArray.toString());
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmp_int = new JSONObject(jsonArray.getString(i)).getString("avg_int");
                                    String tmp_reps = new JSONObject(jsonArray.getString(i)).getString("avg_reps");
                                    String tmp_rest = new JSONObject(jsonArray.getString(i)).getString("avg_rest");
                                    String tmp_sets = new JSONObject(jsonArray.getString(i)).getString("avg_sets");
                                    String tmp_weight = new JSONObject(jsonArray.getString(i)).getString("avg_weight");

                                    int duration = parseData(tmp_rest);

                                    // Adding values to Bar My average data base
                                    // Order: Repetitions - Intensity - Rest - Weight - Sets
                                    thisExEntries.add(new BarEntry(i,Float.parseFloat(tmp_reps)));
                                    thisExEntries.add(new BarEntry(i,Float.parseFloat(tmp_int)));
                                    thisExEntries.add(new BarEntry(i,(float) duration));
                                    thisExEntries.add(new BarEntry(i,Float.parseFloat(tmp_weight)));
                                    thisExEntries.add(new BarEntry(i,Float.parseFloat(tmp_sets)));
                                }
                            } catch (JSONException je){
                                Log.e(TAG, je.toString());
                            }

                            thisExSet = new BarDataSet(thisExEntries, "This Exercise");
                            thisExSet.setColor(Color.rgb(103, 110, 129));

                            // set Data();
                            setData();

                        }catch (JSONException e2){
                            e2.printStackTrace();
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
                try {
                    params.put("id", DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("ID"));
                }catch(JSONException je){Log.e(TAG, "No Exercise History ID");}

                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Activity", "Selected: " + e.toString() + ", dataSet: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Activity", "Nothing selected.");
    }

    private int parseData(String rest){
        //rest time to int
        String[] aux = rest.split(":");
        int hours = Integer.parseInt(aux[0]);
        int minutes = Integer.parseInt(aux[1]);
        int seconds = Integer.parseInt(aux[2]);
        return 3600 * hours + 60 * minutes + seconds;
    }

    public void setData() {

        float groupSpace = 0.08f;
        float barSpace = 0.03f; // x3 DataSet
        float barWidth = 0.278f; // x3 DataSet

        int start = 0;
        int group = 5;

        BarData data = new BarData(thisExSet,myAvgSet,globalAvgSet);
        data.setValueTypeface(mTfLight);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);

        bChart.setData(data);

        // specify the width each bar should have
        bChart.getBarData().setBarWidth(barWidth);

        // restrict the x-axis range
        bChart.getXAxis().setAxisMinimum(start);

        // barData.getGroupWith(...) is a helper that calculates the width each group needs based on the provided parameters
        bChart.getXAxis().setAxisMaximum(start + bChart.getBarData().getGroupWidth(groupSpace, barSpace) * group);
        bChart.groupBars(start, groupSpace, barSpace);
        bChart.invalidate();
        
        bChart.invalidate();
    }

    private void BarSetup(){
        bChart.setOnChartValueSelectedListener(this);
        bChart.getDescription().setEnabled(false);

//        bChart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately
        bChart.setPinchZoom(false);
        bChart.setScaleEnabled(false);

        bChart.setDrawBarShadow(false);

        bChart.setDrawGridBackground(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new HistRadarMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(bChart); // For bounds control
        bChart.setMarker(mv); // Set the marker to the chart


        XAxis xAxis = bChart.getXAxis();
        xAxis.setTypeface(mTfLight);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private String[] mActivities = new String[]{"Repetitions", "Intensity", "Rest", "Weight", "Sets"};

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mActivities[(int) Math.abs(value) % mActivities.length];
            }
        });
        xAxis.setTextColor(Color.WHITE);

        YAxis leftAxis = bChart.getAxisLeft();
        leftAxis.setTypeface(mTfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(35f);
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)

        bChart.getAxisRight().setEnabled(false);

        Legend l = bChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(true);
        l.setTypeface(mTfLight);
        l.setYOffset(0f);
        l.setXOffset(30f);
        l.setYEntrySpace(0f);
        l.setTextSize(8f);
        l.setTextColor(Color.WHITE);
    }

    private void LineSetup(){

        // no description text
        lChart.getDescription().setEnabled(false);

        // enable touch gestures
        lChart.setTouchEnabled(true);
        lChart.setScaleEnabled(false);

        lChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lChart.setDragEnabled(true);
        lChart.setScaleEnabled(true);
        lChart.setDrawGridBackground(false);
        lChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        lChart.setBackgroundColor(Color.rgb(48,48,48));
        lChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new LineMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(lChart); // For bounds control
        lChart.setMarker(mv); // Set the marker to the chart

        String url = DBConnect.serverURL + "/get_exercise_weight_history";
        //Create the exercise history request

        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Chart Array Values List
                            ArrayList<Entry> values = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            Log.i("RESPONSE",response);
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmpWeight = new JSONObject(jsonArray.getString(i)).getString("Weight");
                                    String tmpDate = new JSONObject(jsonArray.getString(i)).getString("Date_Time");
                                    // parse String to Date - I don't know why but this adds an hour to the date
                                    Date tmpDat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.UK).parse(tmpDate);
                                    values.add(new Entry(tmpDat.getTime(),Float.parseFloat(tmpWeight)));
                                }
                            } catch (JSONException|ParseException jpe) {
                                Log.i("WeightChartSetup", jpe.toString());
                            }
                            // create a dataset and give it a type
                            LineDataSet set1 = new LineDataSet(values, "Weight");
                            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                            set1.setColor(ColorTemplate.getHoloBlue());
                            set1.setValueTextColor(ColorTemplate.getHoloBlue());
                            set1.setLineWidth(1.5f);
                            set1.setDrawCircles(true);
                            set1.setDrawValues(false);
                            set1.setFillAlpha(65);
                            set1.setFillColor(ColorTemplate.getHoloBlue());
                            set1.setHighLightColor(Color.rgb(244, 117, 117));
                            set1.setDrawCircleHole(false);

                            // create a data object with the datasets
                            LineData data = new LineData(set1);
                            data.setValueTextColor(Color.WHITE);
                            data.setValueTextSize(9f);

                            // set data
                            lChart.setData(data);

                            lChart.invalidate();


                            // get the legend (only possible after setting data)
                            Legend l = lChart.getLegend();
                            l.setEnabled(false);


                            XAxis xAxis = lChart.getXAxis();
                            xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
                            xAxis.setTypeface(mTfLight);
                            xAxis.setTextSize(10f);
                            xAxis.setTextColor(Color.WHITE);
                            xAxis.setDrawAxisLine(false);
                            xAxis.setDrawGridLines(true);
                            xAxis.setTextColor(Color.rgb(255, 192, 56));
                            xAxis.setCenterAxisLabels(true);
                            xAxis.setGranularity(1f); // one hour

                            xAxis.setValueFormatter(new IAxisValueFormatter() {

                                private SimpleDateFormat mFormat = new SimpleDateFormat("EE, dd MMM",Locale.UK);

                                @Override
                                public String getFormattedValue(float value,  AxisBase axis) {

                                    long millis = TimeUnit.HOURS.toMillis((long) value);
                                    return mFormat.format(new Date(millis));
                                }
                            });

                            YAxis leftAxis = lChart.getAxisLeft();
                            leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                            leftAxis.setTypeface(mTfLight);
                            leftAxis.setTextColor(ColorTemplate.getHoloBlue());
                            leftAxis.setDrawGridLines(true);
                            leftAxis.setGranularityEnabled(true);

                            leftAxis.setAxisMinimum(30f); // min max values
                            leftAxis.setAxisMaximum(120f);

                            leftAxis.setYOffset(-9f);
                            leftAxis.setTextColor(Color.rgb(255, 192, 56));

                            YAxis rightAxis = lChart.getAxisRight();
                            rightAxis.setEnabled(false);


                        }catch (JSONException e2){
                            Log.i("OUTER",e2.toString());
                        }catch (Exception ee){
                            Log.i("EXECEPTION",ee.toString());
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
                try {
                    params.put("exercise_id", DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("ID"));
                }catch(JSONException je){Log.e(TAG, "No Exercise History ID");}

                return params;
            }
        };

        //Queue the request
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }
}