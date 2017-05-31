package g11.muscle.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import g11.muscle.Classes.MuscleProgressItem;
import g11.muscle.DB.DBConnect;
import g11.muscle.ExerciseActivity;
import g11.muscle.PlanActivity;
import g11.muscle.R;
import g11.muscle.DB.VolleyProvider;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HomeFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HomeFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeFragment extends Fragment implements OnChartValueSelectedListener {
    private static final String TAG = "HomeFragment";
    private static final String ERROR_MSG = "Please try to reconnect";

    private OnFragmentInteractionListener mListener;

    private String email;

    private String[] arraySpinner;

    //GUI
    private Spinner spinner;
    private ListView recList;
    private View fView;
    private ProgressBar progressBar;
    private GridView muscle_groupView;
    private HorizontalBarChart bChart;

    private LinearLayout homeLinearLayout;
    // Chart font
    private Typeface mTfLight;

    private ArrayList<MuscleProgressItem> list;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        email = getActivity().getIntent().getStringExtra("email");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // Fragment View
        fView = inflater.inflate(R.layout.fragment_home, container, false);

        this.arraySpinner = new String[] {
                "Recommended Plans","Recommended Exercises"
        };


        muscle_groupView = (GridView)fView.findViewById(R.id.groups);
        progressBar = (ProgressBar)fView.findViewById(R.id.homeProgressBar);
        homeLinearLayout = (LinearLayout)fView.findViewById(R.id.homeLinearLayout);
        progressBar.setVisibility(View.VISIBLE);
        homeLinearLayout.setVisibility(View.INVISIBLE);

        recList = (ListView) fView.findViewById(R.id.home_rec_list);

        bChart = (HorizontalBarChart) fView.findViewById(R.id.home_bar_chart);
        // Font for charts text
        mTfLight = Typeface.createFromAsset(getActivity().getAssets(), "fonts/OpenSans-Light.ttf");

        spinner = (Spinner) fView.findViewById(R.id.home_spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_dropdown_item, arraySpinner);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                if(pos == 0){
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1,new String[0]);
                    recList.setAdapter(adapter);
                    getRecommendedPlans();
                }
                else {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, new String[0]);
                    recList.setAdapter(adapter);
                    getRecommendedExercises();
                }
            }
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        getMuscleProgress();
        // Inflate the layout for this fragment
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getMuscleProgress() {
        String url = DBConnect.serverURL + "/get_muscle_progress";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String r) {
                        JSONObject response = null;
                        try {
                            response = new JSONObject(r);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        list = new ArrayList<>();

                        Iterator<String> iter;
                        if(response != null) {
                            iter = response.keys();
                            while (iter.hasNext()) {
                                String key = iter.next();
                                try {
                                    Double value = response.getDouble(key);
                                    list.add(new MuscleProgressItem(value, key));
                                } catch (JSONException e) {
                                    // Something went wrong!
                                }
                            }

                            // Define the groupView adapter

                            progressBar.setVisibility(View.GONE);

                            // set bar chart
                            barChartSetup();
                        }
                    progressBar.setVisibility(View.INVISIBLE);
                    homeLinearLayout.setVisibility(View.VISIBLE);
                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        System.out.println(error.toString());
                        progressBar.setVisibility(View.GONE);
                    }
                }
        ) {
            // use params are specified here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_email", email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }

    private void barChartSetup(){

        final String [] MuscleGroups = new String[list.size()];
        ArrayList<BarEntry> yValues = new ArrayList<>();

        float chartMax = -101f;
        float chartMin = 1000f;

        for(int i = 0, k = 5;i<list.size();i++, k += 10){
            MuscleGroups[i] = list.get(i).getName();
            float tmp_value = (float) ((list.get(i).getVar()-1)*100);

            yValues.add(new BarEntry(k,tmp_value));

            if(chartMax < tmp_value)
                chartMax = tmp_value;
            if(chartMin > tmp_value)
                chartMin = tmp_value;

        }

        bChart.setOnChartValueSelectedListener(this);
        bChart.setDrawGridBackground(false);
        bChart.getDescription().setEnabled(false);

        // scaling can now only be done on x- and y-axis separately
        bChart.setPinchZoom(false);
        bChart.setScaleEnabled(false);

        //bChart.setHighlightPerTapEnabled(false);

        bChart.setDrawBarShadow(false);
        bChart.setDrawValueAboveBar(true);
        bChart.setHighlightFullBarEnabled(false);


        bChart.getAxisLeft().setEnabled(false);
        bChart.getAxisRight().setAxisMaximum(Math.round(chartMax) + 2f);
        bChart.getAxisRight().setAxisMinimum(Math.round(chartMin) - 2f);
        bChart.getAxisRight().setDrawGridLines(false);
        bChart.getAxisRight().setDrawZeroLine(true);
        bChart.getAxisRight().setLabelCount(7, false);
        bChart.getAxisRight().setTextColor(Color.WHITE);
        bChart.getAxisRight().setTextSize(9f);

        XAxis xAxis = bChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setDrawGridLines(false);
        xAxis.setDrawAxisLine(false);
        xAxis.setTextSize(9f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(MuscleGroups.length*10);
        xAxis.setCenterAxisLabels(true);
        xAxis.setLabelCount(12);
        xAxis.setGranularity(10f);

        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return MuscleGroups[Math.abs((int)value) % MuscleGroups.length];
            }
        });

        Legend l = bChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setFormSize(8f);
        l.setTextColor(Color.WHITE);
        l.setFormToTextSpace(4f);
        l.setXEntrySpace(6f);

        MyBarDataSet set = new MyBarDataSet(yValues, "Weekly Progress");
        set.setDrawIcons(false);
        set.setValueTextSize(7f);
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setColors(new int[]{Color.rgb(230,56,56),Color.rgb(25,199,199)});
        set.setDrawValues(false);

        BarData data = new BarData(set);
        data.setBarWidth(8.5f);
        data.setHighlightEnabled(false);
        bChart.setData(data);

        bChart.invalidate();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.i("VALUE SELECTED", "");
    }

    @Override
    public void onNothingSelected() {
        // TODO Auto-generated method stub
        Log.i("NOTING SELECTED", "");
    }
    
    // Get Recommended Exercises ( ID + Name )
    private void getRecommendedExercises() {
        String url = DBConnect.serverURL + "/get_recommended_exercises";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    String[] rE;

                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e(TAG,response);
                            JSONArray jsonArray = new JSONArray(response);
                            rE = new String[jsonArray.length()];
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmp = jsonArray.getJSONObject(i).getString("Exercise_name");
                                    // TODO Needs a way to list exercises by count
                                    rE[i] = tmp;
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }

                        // Define the groupView adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, rE);
                        recList.setAdapter(adapter);
                        recList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to Exercise Activity
                                Intent intent = new Intent(getActivity(), ExerciseActivity.class);
                                intent.putExtra("exercise_name", rE[position]);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            }
                        });


                    }

                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        System.out.println();
                        progressBar.setVisibility(View.GONE);
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("No Internet Connection");
                        //"Please connect your device to the Internet and try again")
                            alertDialog.setMessage(ERROR_MSG);
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                            alertDialog.show();
                    }
                }
        ) {
            // use params are specified here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_email", email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }

    // Get Recommended Plans
    private void getRecommendedPlans() {
        String url = DBConnect.serverURL + "/get_recommended_plans";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    String[] rP;
                    String[] cola;

                    @Override
                    public void onResponse(String response) {
                        try {
                            Log.e("Response", response);
                            JSONArray jsonArray = new JSONArray(response);
                            rP = new String[jsonArray.length()];
                            cola = new String[jsonArray.length()];
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject tmp = jsonArray.getJSONObject(i);
                                    rP[i] = tmp.getString("ID");
                                    cola[i] = tmp.getString("Name");
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }

                        // Define the groupView adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, cola);
                        recList.setAdapter(adapter);
                        recList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to
                                Intent intent = new Intent(getActivity(), PlanActivity.class);
                                intent.putExtra("plan_id", rP[position]);
                                intent.putExtra("email", email);
                                startActivity(intent);
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        System.out.println(error.toString());
                        progressBar.setVisibility(View.GONE);

                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("No Internet Connection");
                        //"Please connect your device to the Internet and try again")
                        alertDialog.setMessage("Please try to reconnect");
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
        ) {
            // use params are specified here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_email", email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }

    private class MyBarDataSet extends BarDataSet {


        private MyBarDataSet(List<BarEntry> yVals, String label) {
            super(yVals, label);
        }

        @Override
        public int getColor(int index) {
            if(getEntryForIndex(index).getY() < 0) // less than 95 green
                return Color.rgb(230,56,56);
            else
                return Color.rgb(25,199,199);
        }

    }
}
