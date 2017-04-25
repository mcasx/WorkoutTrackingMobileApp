package g11.muscle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class PickExerciseActivity extends AppCompatActivity {

    private static final String TAG = "PickExerciseActivity";
    private String email;
    private String[] groups;
    private RequestQueue queue;

    //GUI
    private GridView groupsView;
    private ListView recent_historyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_exercise);

        email = getIntent().getStringExtra("email");

        //GUI elements
        groupsView  = (GridView)findViewById(R.id.groups);
        recent_historyView = (ListView) findViewById(R.id.recent_history);

        //Create volley queue
        queue = Volley.newRequestQueue(this);

        //UI Static elements (dynamic should be defined in start method)
        createMuscleGroupGrid();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //UI Dynamic elements
        createExerciseHistoryList();
    }

    @Override
    protected void onStop() {
        queue.cancelAll(this);
        super.onStop();
    }

    private void createMuscleGroupGrid() {
        String url = "http://138.68.158.127/get_muscle_groups";

        //Create the request
        JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //From the response create the history array
                        groups = new String[response.length()];
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                groups[i] = new JSONObject(response.getString(i)).getString("Name");
                            }
                        } catch (JSONException je){
                            Log.e(TAG, je.toString());
                        }

                        // Set the listeners on the groups items
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(PickExerciseActivity.this, android.R.layout.simple_list_item_1, groups);
                        groupsView.setAdapter(adapter);
                        groupsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
                                Intent intent = new Intent(PickExerciseActivity.this, GroupExercisesActivity.class);
                                intent.putExtra("group", groups[position]);
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
                    }
                }
        );

        // Add the request to the RequestQueue
        queue.add(jsonarrayRequest);
    }

    private void createExerciseHistoryList(){
        //Create the exercise history request
        String url = "http://138.68.158.127/get_exercise_history_of_user/" + email;
        JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //From the response create the history array
                        String[] history = new String[response.length()];
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                history[i] = new JSONObject(response.getString(i)).getString("Exercise_name");
                            }
                        } catch (JSONException je){
                            Log.e(TAG, je.toString());
                        }

                        // Define the groupView adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(PickExerciseActivity.this, android.R.layout.simple_list_item_1, history);
                        recent_historyView.setAdapter(adapter);
                        // Set the listeners on the list items
                        recent_historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
                                String exercise_name = (String) parent.getAdapter().getItem(position);
                                Intent intent = new Intent(PickExerciseActivity.this, ExerciseActivity.class);
                                intent.putExtra("exercise_name", exercise_name);
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
                    }
                }
        );

        // Add the request to the RequestQueue
        queue.add(jsonarrayRequest);
    }
}