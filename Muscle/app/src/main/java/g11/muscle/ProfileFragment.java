package g11.muscle;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
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

import g11.muscle.MPChartJava.RadarMarkerView;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProfileFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProfileFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment implements
        OnChartValueSelectedListener {

    private static final String TAG = "ProfileFragment";

    private OnFragmentInteractionListener mListener;

    //GUI
    private LineChart lChart;
    private RadarChart rChart;

    // Chart font
    private Typeface mTfLight;

    private String email;

    // Chart Array Values List
    private ArrayList<Entry> Weightvalues = new ArrayList<>();

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO CHANGE TO INTENT AGAIN - SKIPPED LOGIN
        //email = getActivity().getIntent().getStringExtra("email");
        email = "ola@ua.pt";
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Font for charts text
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf");

        // Fragment View
        View fView = inflater.inflate(R.layout.fragment_profile, container, false);
        // Ini Charts
        lChart = (LineChart) fView.findViewById(R.id.lChart); 
        rChart = (RadarChart) fView.findViewById(R.id.rChart);
        // Charts setup
        // get data for weight progress graph
        LineSetup();
        RadarSetup();
        return fView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // needed to compile
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("Entry selected", e.toString());
    }

    @Override
    public void onNothingSelected() {
        Log.i("Nothing selected", "Nothing selected.");
    }

    private void LineSetup(){
        // no description text
        lChart.getDescription().setEnabled(false);

        // enable touch gestures
        lChart.setTouchEnabled(true);

        lChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lChart.setDragEnabled(true);
        lChart.setScaleEnabled(true);
        lChart.setDrawGridBackground(false);
        lChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        lChart.setBackgroundColor(Color.WHITE);
        lChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        String url = "https://138.68.158.127/get_weight_history";
        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Chart Array Values List
                            ArrayList<Entry> values = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmpWeight = new JSONObject(jsonArray.getString(i)).getString("Weight");
                                    String tmpDate = new JSONObject(jsonArray.getString(i)).getString("Date");
                                    // parse String to Date - I don't know why but this adds an hour to the date
                                    Date tmpDat = new SimpleDateFormat("EE, dd MMM yyy HH:mm:ss z",Locale.UK).parse(tmpDate);
                                    values.add(new Entry(tmpDat.getTime(),Float.parseFloat(tmpWeight)));
                                }
                            } catch (JSONException|ParseException jpe) {
                                Log.e(TAG, jpe.toString());
                            }
                            // create a dataset and give it a type
                            LineDataSet set1 = new LineDataSet(values, "Weight");
                            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                            set1.setColor(ColorTemplate.getHoloBlue());
                            set1.setValueTextColor(ColorTemplate.getHoloBlue());
                            set1.setLineWidth(1.5f);
                            set1.setDrawCircles(false);
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

                                private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM HH:mm",Locale.UK);

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
                            leftAxis.setAxisMinimum(0f);
                            leftAxis.setAxisMaximum(170f);
                            leftAxis.setYOffset(-9f);
                            leftAxis.setTextColor(Color.rgb(255, 192, 56));

                            YAxis rightAxis = lChart.getAxisRight();
                            rightAxis.setEnabled(false);

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
                return params;
            }
        };

        //Queue the request
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }

    private void RadarSetup(){
        rChart.setBackgroundColor(Color.rgb(60, 65, 82));

        rChart.getDescription().setEnabled(false);

        rChart.setWebLineWidth(1f);
        rChart.setWebColor(Color.LTGRAY);
        rChart.setWebLineWidthInner(1f);
        rChart.setWebColorInner(Color.LTGRAY);
        rChart.setWebAlpha(100);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(getActivity(), R.layout.custom_marker_view);
        mv.setChartView(rChart); // For bounds control
        rChart.setMarker(mv); // Set the marker to the chart

        String url = "https://138.68.158.127/get_exercise_muscle_stats_of_user";

        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            // Chart Array Values List
                            ArrayList<RadarEntry> entries = new ArrayList<>();
                            final String[] MuscleGroups = new String[jsonArray.length()];
                            Float[] MuscleCnt = new Float[jsonArray.length()];
                            Float sum = 0f;
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    MuscleGroups[i] = new JSONObject(jsonArray.getString(i)).getString("Muscle_name");
                                    MuscleCnt[i] = Float.parseFloat(new JSONObject(jsonArray.getString(i)).getString("count"));
                                    sum += MuscleCnt[i];
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }

                            // normalize values
                            for(Float x : MuscleCnt){
                                Float tmp = x/sum;
                                entries.add(new RadarEntry(tmp*100));
                            }

                            RadarDataSet set1 = new RadarDataSet(entries,"% Worked Group Muscles");
                            set1.setColor(Color.rgb(121, 162, 175));
                            set1.setFillColor(Color.rgb(121, 162, 175));
                            set1.setDrawFilled(true);
                            set1.setFillAlpha(180);
                            set1.setLineWidth(2f);
                            set1.setDrawHighlightCircleEnabled(true);
                            set1.setDrawHighlightIndicators(false);

                            ArrayList<IRadarDataSet> sets = new ArrayList<>();
                            sets.add(set1);

                            RadarData data = new RadarData(sets);
                            data.setValueTypeface(mTfLight);
                            data.setValueTextSize(8f);
                            data.setDrawValues(false);
                            data.setValueTextColor(Color.WHITE);

                            rChart.setData(data);
                            rChart.invalidate();

                            rChart.animateXY(
                                    1400, 1400,
                                    Easing.EasingOption.EaseInOutQuad,
                                    Easing.EasingOption.EaseInOutQuad);

                            XAxis xAxis = rChart.getXAxis();
                            xAxis.setTypeface(mTfLight);
                            xAxis.setTextSize(9f);
                            xAxis.setYOffset(0f);
                            xAxis.setXOffset(0f);
                            xAxis.setValueFormatter(new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    return MuscleGroups[(int) value % MuscleGroups.length];
                                }
                            });
                            xAxis.setTextColor(Color.WHITE);

                            YAxis yAxis = rChart.getYAxis();
                            yAxis.setTypeface(mTfLight);
                            yAxis.setLabelCount(5, false);
                            yAxis.setTextSize(9f);
                            yAxis.setAxisMinimum(0f);
                            yAxis.setAxisMaximum(80f);
                            yAxis.setDrawLabels(false);

                            Legend l = rChart.getLegend();
                            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                            l.setDrawInside(false);
                            l.setTypeface(mTfLight);
                            l.setXEntrySpace(7f);
                            l.setYEntrySpace(5f);
                            l.setTextColor(Color.WHITE);
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
                params.put("User_email", email);
                return params;
            }
        };

        //Queue the request
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }
}
