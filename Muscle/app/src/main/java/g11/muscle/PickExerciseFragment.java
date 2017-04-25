package g11.muscle;

import android.content.Context;
import android.content.Intent;
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

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PickExerciseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link PickExerciseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PickExerciseFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "PickExerciseFragment";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String email;


    private String[] groups;
    private RequestQueue queue;

    //GUI
    private GridView groupsView;
    private ListView recent_historyView;

    private OnFragmentInteractionListener mListener;

    public PickExerciseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PickExerciseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PickExerciseFragment newInstance(String param1, String param2) {
        PickExerciseFragment fragment = new PickExerciseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        email = getActivity().getIntent().getStringExtra("email");

        //Create volley queue
        queue = Volley.newRequestQueue(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pick_exercise, container, false);
        //GUI elements
        groupsView  = (GridView) view.findViewById(R.id.groups);
        recent_historyView = (ListView) view.findViewById(R.id.recent_history);
        //UI Static elements (dynamic should be defined in start method)
        createMuscleGroupGrid();
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pick_exercise, container, false);
    }

    @Override
    public void onStart() {
        super.onStart();

        //UI Dynamic elements
        createExerciseHistoryList();
    }

    @Override
    public void onStop() {
        queue.cancelAll(this);
        super.onStop();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, groups);
                        groupsView.setAdapter(adapter);
                        groupsView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
                                Intent intent = new Intent(getActivity(), GroupExercisesActivity.class);
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
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, history);
                        recent_historyView.setAdapter(adapter);
                        // Set the listeners on the list items
                        recent_historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
                                String exercise_name = (String) parent.getAdapter().getItem(position);
                                Intent intent = new Intent(getActivity(), ExerciseActivity.class);
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
