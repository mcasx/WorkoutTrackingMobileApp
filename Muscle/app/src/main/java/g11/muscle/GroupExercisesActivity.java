package g11.muscle;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;

public class GroupExercisesActivity extends AppCompatActivity {

    private static final String TAG = "groupExercisesActivity";
    private static boolean canRun;
    private String email;
    private ListView exercisesView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_exercises);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //GUI elements
        exercisesView = (ListView) findViewById(R.id.exercises);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        //Get muscle group and email
        Intent intent = getIntent();
        final String group = intent.getStringExtra("group");
        email = intent.getStringExtra("email");
        getSupportActionBar().setTitle(group);

        PackageManager manager = getPackageManager();
        canRun = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);

        // Define title

        //Create the list items through a request
        String url = DBConnect.serverURL + "/get_exercises_by_muscle_group";
        StringRequest ExeListReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Initialization of exercises list
                        String[] exercises = new String[0];

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Log.e(TAG,jsonArray.toString());
                            //From the response create the list items
                            exercises = new String[jsonArray.length()];
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    exercises[i] = new JSONObject(jsonArray.getString(i)).getString("Exercise_name");
                                }
                            } catch (JSONException je){
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        // Define the groupView adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(GroupExercisesActivity.this, android.R.layout.simple_list_item_1, exercises){
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view =super.getView(position, convertView, parent);

                                TextView textView=(TextView) view.findViewById(android.R.id.text1);
                                if(textView.getText().toString().equals("Running") && !canRun)
                                    textView.setTextColor(Color.GRAY);
                                else
                                    textView.setTextColor(Color.WHITE);

                                return view;
                            }
                        };
                        exercisesView.setAdapter(adapter);

                        // Set the listeners on the list items
                        exercisesView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
                                String exercise_name = (String) parent.getAdapter().getItem(position);
                                Intent intent;
                                if(exercise_name.equals("Running")) {
                                    if (!canRun)
                                        return;
                                    else
                                        intent = new Intent(GroupExercisesActivity.this, CardioActivity.class);
                                }
                                else
                                    intent = new Intent(GroupExercisesActivity.this, ExerciseActivity.class);
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
        ) {
            // use params are specified here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("muscle_group", group);
                return params;
            }
        };

        //Queue the request
        VolleyProvider.getInstance(GroupExercisesActivity.this).addRequest(ExeListReq);
    }
}
