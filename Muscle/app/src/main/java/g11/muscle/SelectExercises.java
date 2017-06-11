package g11.muscle;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import g11.muscle.Classes.ExpandableListAdapter;
import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;

public class SelectExercises extends AppCompatActivity {

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    List<String> back;
    List<String> biceps;
    List<String> cardio;
    List<String> chest;
    List<String> legs;
    List<String> shoulders;
    List<String> triceps;
    RequestQueue queue;
    final String TAG = "SelectExercises";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_exercises);

        queue = Volley.newRequestQueue(this);

        back = new ArrayList<String>();
        biceps = new ArrayList<String>();
        cardio = new ArrayList<String>();
        chest = new ArrayList<String>();
        legs = new ArrayList<String>();
        shoulders = new ArrayList<String>();
        triceps = new ArrayList<String>();

        expListView = (ExpandableListView) findViewById(R.id.lvExp);

        setTitle("Pick Exercises");
        // preparing list data
        prepareListData();

        listAdapter = new ExpandableListAdapter(this, listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);
    }

    private void prepareListData() {
        listDataHeader = new ArrayList<String>();
        listDataChild = new HashMap<String, List<String>>();

        // Adding child data
        listDataHeader.add("Back");
        listDataHeader.add("Biceps");
        listDataHeader.add("Cardio");
        listDataHeader.add("Chest");
        listDataHeader.add("Legs");
        listDataHeader.add("Shoulders");
        listDataHeader.add("Triceps");

        // Adding child data


        listDataChild.put(listDataHeader.get(0), back); // Header, Child data
        listDataChild.put(listDataHeader.get(1), biceps);
        listDataChild.put(listDataHeader.get(2), cardio);
        listDataChild.put(listDataHeader.get(3), chest);
        listDataChild.put(listDataHeader.get(4), legs);
        listDataChild.put(listDataHeader.get(5), shoulders);
        listDataChild.put(listDataHeader.get(6), triceps);

        getExercises();

    }


    private void getExercises(){
        String url = DBConnect.serverURL + "/get_all_exercises";

        //Create the request
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String r) {
                        JSONArray response = null;
                        try {
                            response = new JSONArray(r);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.e(TAG, "" + response);
                        for(int i = 0; i < response.length(); i++){
                            JSONObject obj = null;
                            try {
                                obj = (JSONObject) response.get(i);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            try {
                                switch (obj.getString("Muscle_name")){
                                    case "Back":
                                        back.add(obj.getString("Name"));
                                        break;
                                    case "Biceps":
                                        biceps.add(obj.getString("Name"));
                                        break;
                                    case "Cardio":
                                        cardio.add(obj.getString("Name"));
                                        break;
                                    case "Chest":
                                        chest.add(obj.getString("Name"));
                                        break;
                                    case "Legs":
                                        legs.add(obj.getString("Name"));
                                        break;
                                    case "Shoulders":
                                        shoulders.add(obj.getString("Name"));
                                        break;
                                    case "Triceps":
                                        triceps.add(obj.getString("Name"));
                                        break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        listDataChild.put(listDataHeader.get(0), back); // Header, Child data
                        listDataChild.put(listDataHeader.get(1), biceps);
                        listDataChild.put(listDataHeader.get(2), cardio);
                        listDataChild.put(listDataHeader.get(3), chest);
                        listDataChild.put(listDataHeader.get(4), legs);
                        listDataChild.put(listDataHeader.get(5), shoulders);
                        listDataChild.put(listDataHeader.get(6), triceps);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        System.out.println(error.toString());
                    }
                }
        );
        // Add the request to the RequestQueue
        VolleyProvider.getInstance(this).addRequest(stringRequest);
    }

    @Override
    public void onBackPressed(){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", listAdapter.exercises);
        setResult(Activity.RESULT_OK,returnIntent);
        super.onBackPressed();
    }

}
