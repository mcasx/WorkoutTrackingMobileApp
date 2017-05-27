package g11.muscle.Fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
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
import g11.muscle.DB.VolleyProvider;
import g11.muscle.R;


public class WeightProgressFragment extends SimpleFragment {

    public static Fragment newInstance(String mail) {
        email = mail;
        return new WeightProgressFragment();
    }

    //GUI
    private LineChart lChart;

    // Chart font
    private Typeface mTfLight;

    private static String email;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weight_progress, container, false);

        // Font for charts text
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf");

        lChart = (LineChart) v.findViewById(R.id.lChart);

        LineSetup();

        return v;
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

        String url = DBConnect.serverURL + "/get_weight_history";
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
                                    Date tmpDat = new SimpleDateFormat("EE, dd MMM yyy HH:mm:ss z", Locale.UK).parse(tmpDate);
                                    values.add(new Entry(tmpDat.getTime(),Float.parseFloat(tmpWeight)));
                                }
                            } catch (JSONException |ParseException jpe) {
                                Log.e("WeightProgressFragment", jpe.toString());
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
}
