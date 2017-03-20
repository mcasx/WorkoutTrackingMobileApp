package g11.muscle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class groupExercisesActivity extends AppCompatActivity {

    private static final String TAG = "groupExercisesActivity";
    private String email;
    private TextView titleView;
    private ListView exercisesView;
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_exercises);
        //GUI elements
        titleView  = (TextView)findViewById(R.id.title);
        exercisesView = (ListView) findViewById(R.id.exercises);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Get muscle group and email
        Intent intent = getIntent();
        String group = intent.getStringExtra("group");
        email = intent.getStringExtra("email");

        // Define title
        titleView.setText(group);

        // Create request queue
        queue = Volley.newRequestQueue(this);

        //Create the list items through a request
        String url = "http://138.68.158.127/get_exercises_by_muscle_group/" + group;
        JsonArrayRequest jsonarrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        //From the response create the list items
                        String[] exercises = new String[response.length()];
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                exercises[i] = new JSONObject(response.getString(i)).getString("Exercise_name");
                            }
                        } catch (JSONException je){
                            Log.e(TAG, je.toString());
                        }

                        // Define the groupView adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(groupExercisesActivity.this, android.R.layout.simple_list_item_1, exercises);
                        exercisesView.setAdapter(adapter);

                        // Set the listeners on the list items
                        exercisesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
                                String exercise_name = (String) parent.getAdapter().getItem(position);
                                Intent intent = new Intent(groupExercisesActivity.this, ExerciseActivity.class);
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

        //Queue the request
        queue.add(jsonarrayRequest);
    }

    @Override
    protected void onStop() {
        queue.cancelAll(this);
        super.onStop();
    }
}
