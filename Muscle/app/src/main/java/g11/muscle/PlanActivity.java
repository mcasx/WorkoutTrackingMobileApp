package g11.muscle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import g11.muscle.Classes.TrainingsItem;
import g11.muscle.Classes.PlanExerciseItem;
import g11.muscle.Classes.Plan_Exercise_View;
import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;

public class PlanActivity extends AppCompatActivity {

    private static final String TAG = "PlanActivity";

    private String email;
    private String plan_id;

    // Initialization of plan_group array
    private List<TrainingsItem> plan_trainings;
    private List<PlanExerciseItem> training_data;

    //GUI
    private RecyclerView recyclerView;
    private Spinner dropdown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);

        plan_trainings = new ArrayList<>();
        training_data = new ArrayList<>();

        // get intent info
        email = getIntent().getStringExtra("email");
        plan_id = getIntent().getStringExtra("plan_id");


        //GUI elements
        dropdown = (Spinner)findViewById(R.id.spinner);
        getTrainings();

        recyclerView = (RecyclerView) findViewById(R.id.PE_recyclerview);
    }

    public void onClickSetPlan(View view){
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Set Plan AQUI")
                .setMessage("\n    You still have an unfinished plan.\n\n    Are you sure you want to set a new plan?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        setNewPlan();
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // sets user new plan
    private void setNewPlan() {
        String url = DBConnect.serverURL + "/set_user_plan";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(PlanActivity.this, "New plan set successfully", Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        Toast.makeText(PlanActivity.this, "Couldn't set new plan", Toast.LENGTH_LONG).show();
                    }
                }
        ){
            // use params are specified here
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("plan_id",plan_id);
                params.put("email",email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(PlanActivity.this).addRequest(StrHistReq);
    }

    // Get User Plan Trainings ( ID + Name )
    private void getTrainings(){
        String url = DBConnect.serverURL + "/get_plan_trainings";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmp_name = new JSONObject(jsonArray.getString(i)).getString("Name");
                                    int tmp_id = Integer.parseInt(new JSONObject(jsonArray.getString(i)).getString("ID"));
                                    plan_trainings.add(new TrainingsItem(tmp_id,tmp_name));
                                }
                            } catch (JSONException je){
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        String[] items = new String[plan_trainings.size()];
                        for(int i = 0; i < plan_trainings.size();i++){
                            items[i] = "Day " + String.valueOf(i+1) + " - " + plan_trainings.get(i).getName();
                        }
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(PlanActivity.this, android.R.layout.simple_spinner_dropdown_item, items);
                        dropdown.setAdapter(adapter1);
                        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                getTrainingExercises(plan_trainings.get(pos).getIdStr());
                            }
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
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
                    params.put("plan_id", plan_id);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(PlanActivity.this).addRequest(StrHistReq);
    }

    // Gets plan training exercises
    private void getTrainingExercises(final String training) {
        String url = DBConnect.serverURL  + "/get_training_exercises";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            training_data.clear(); // clear data - when other training selected
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmp_name = new JSONObject(jsonArray.getString(i)).getString("Exercise_name");
                                    int tmp_reps = Integer.parseInt(new JSONObject(jsonArray.getString(i)).getString("Repetitions"));
                                    int tmp_sets = Integer.parseInt(new JSONObject(jsonArray.getString(i)).getString("Sets"));
                                    String tmp_rest = new JSONObject(jsonArray.getString(i)).getString("Resting_Time");
                                    int tmp_weight = Integer.parseInt(new JSONObject(jsonArray.getString(i)).getString("Weight"));
                                    // TODO NEEDS IMAGE
                                    training_data.add(new PlanExerciseItem(tmp_name,tmp_reps,tmp_sets,tmp_rest,tmp_weight));
                                }
                            } catch (JSONException je){
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        Plan_Exercise_View adapter = new Plan_Exercise_View(training_data);
                        adapter.mOnClickListener = new MyOnClickListener();
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(PlanActivity.this));
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
                params.put("training",training);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(PlanActivity.this).addRequest(StrHistReq);
    }


    private class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(final View view) {
            int itemPosition = recyclerView.getChildLayoutPosition(view);
            PlanExerciseItem item = training_data.get(itemPosition);
            String email = getSharedPreferences("UserData",0).getString("email", null);
            Intent intent = new Intent(PlanActivity.this, ExerciseActivity.class);
            intent.putExtra("email", email);
            intent.putExtra("exercise_name", item.getExercise_name());
            startActivity(intent);
        }
    }
}
