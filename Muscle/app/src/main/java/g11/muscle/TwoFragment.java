package g11.muscle;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import g11.muscle.MPChartJava.HistRadarMarkerView;

public class TwoFragment extends Fragment implements OnChartValueSelectedListener {

    private static final String TAG = "MyPlanFragment";

    // GUI
    private BarChart bChart;

    // chart font
    Typeface mTfLight;

    private String email;
    private String exercise_name;

    // Radar Chart data
    private ArrayList<BarEntry> entries1 = new ArrayList<>();
    private ArrayList<BarEntry> myAvgEntries = new ArrayList<>();
    private ArrayList<BarEntry> globalAvgEntries = new ArrayList<>();

    private BarDataSet myAvgSet;
    private BarDataSet globalAvgSet;
    private BarDataSet set;

    public TwoFragment() {
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

        View fView = inflater.inflate(R.layout.fragment_two, container, false);

        bChart = (BarChart) fView.findViewById(R.id.bar_chart);
        bChart.setOnChartValueSelectedListener(this);
        bChart.getDescription().setEnabled(false);

//        bChart.setDrawBorders(true);

        // scaling can now only be done on x- and y-axis separately
        bChart.setPinchZoom(false);

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

        // Inflate the layout for this fragment
        return fView;
    }

    private void getUserAverage(){
        String url = "https://138.68.158.127/get_user_avg_stats_ex";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG,response);
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
        String url = "https://138.68.158.127/get_avg_stats_ex";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG,response);
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
                params.put("exercise",exercise_name);
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

        float mult = 80;
        float min = 20;
        int cnt = 5;

        int start = 0;
        int group = 5;

        // NOTE: The order of the entries when being added to the entries array determines their position around the center of
        // the chart.
        entries1.add(new BarEntry(0,15));
        entries1.add(new BarEntry(1,(float)1.5));
        entries1.add(new BarEntry(2,40));
        entries1.add(new BarEntry(3,30));
        entries1.add(new BarEntry(4,3));

        BarDataSet set1 = new BarDataSet(entries1, "This Exercise");
        set1.setColor(Color.rgb(103, 110, 129));

        BarData data = new BarData(set1,myAvgSet,globalAvgSet);
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


}