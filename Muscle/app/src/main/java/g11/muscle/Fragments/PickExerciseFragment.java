package g11.muscle.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.ProgressBar;
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

import g11.muscle.CardioActivity;
import g11.muscle.DB.DBConnect;
import g11.muscle.ExerciseActivity;
import g11.muscle.GroupExercisesActivity;
import g11.muscle.R;
import g11.muscle.DB.VolleyProvider;

import static android.content.Context.SENSOR_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PickExerciseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PickExerciseFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickExerciseFragment extends Fragment {
    private static final String TAG = "PickExerciseFragment";

    //
    private String email;

    static boolean canRun = true;

    private ProgressBar progressBar;

    private String[] groups;

    //GUI
    private GridView groupsView;
    private ListView recent_historyView;

    private TextView groupExercises;
    private TextView recentExercises;

    // Used by Main Activity
    private OnFragmentInteractionListener mListener;

    public PickExerciseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get email
        email = getActivity().getIntent().getStringExtra("email");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fragment View
        View fView = inflater.inflate(R.layout.fragment_pick_exercise, container, false);
        //GUI elements
        progressBar = (ProgressBar) fView.findViewById(R.id.pickExerciseProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        groupsView  = (GridView) fView.findViewById(R.id.groups);
        recent_historyView = (ListView) fView.findViewById(R.id.recent_history);
        recentExercises = (TextView)fView.findViewById(R.id.textView4);
        groupExercises = (TextView)fView.findViewById(R.id.textView7);
        //UI Static elements
        createMuscleGroupGrid();
        createExerciseHistoryList();
        // Inflate the layout for this fragment

        PackageManager manager = getActivity().getPackageManager();
        canRun = manager.hasSystemFeature(PackageManager.FEATURE_SENSOR_STEP_COUNTER);

        return fView;
    }

    @Override
    public void onStart() {
        super.onStart();
        //UI Dynamic elements

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
        // Needed to compile
        void onFragmentInteraction(Uri uri);
    }

    private void createMuscleGroupGrid() {
        String url = DBConnect.serverURL + "/get_muscle_groups";

        //Create the request
        StringRequest StrPickExReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.GONE);
                        recentExercises.setText("Groups of Exercises");
                        groupExercises.setText("Recent Exercises");
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //From the response create the history array
                            groups = new String[jsonArray.length()];
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    groups[i] = new JSONObject(jsonArray.getString(i)).getString("Name");
                                }
                            } catch (JSONException je){
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        // Set the listeners on the groups items
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, groups);
                        groupsView.setAdapter(adapter);
                        groupsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
                                Intent intent;
                                intent = new Intent(getActivity(), GroupExercisesActivity.class);
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

                        progressBar.setVisibility(View.GONE);

                    }
                }
        );

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(getActivity()).addRequest(StrPickExReq);
    }

    private void createExerciseHistoryList(){
        String url = DBConnect.serverURL + "/get_exercise_history_of_user";

        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Initialization of history array
                        String[] history = new String[0];

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //From the response create the history array
                            history = new String[jsonArray.length()];
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    history[i] = new JSONObject(jsonArray.getString(i)).getString("Exercise_name");
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }


                        // Define the groupView adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, history){
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
                        recent_historyView.setAdapter(adapter);
                        // Set the listeners on the list items

                        recent_historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {

                                //Go to exercise page
                                String exercise_name = (String) parent.getAdapter().getItem(position);
                                Intent intent;
                                if(exercise_name.equals("Running")){
                                    if(!canRun)
                                        return;
                                    else
                                        intent = new Intent(getActivity(), CardioActivity.class);

                                }
                                else
                                    intent = new Intent(getActivity(), ExerciseActivity.class);

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
        ){
            // use params are specified here
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }
}
