package g11.muscle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseHistoryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ExerciseHistoryFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class ExerciseHistoryFragment extends Fragment {

    private static final String TAG = "ExerciseHistoryFragment";

    //
    private String email;

    // Used by Main Activity
    private OnFragmentInteractionListener mListener;

    private VolleyProvider req_queue;
    private ListView recent_historyView;

    public ExerciseHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        req_queue = VolleyProvider.getInstance(getActivity());
        email = getActivity().getIntent().getStringExtra("email");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fragment View
        View fView = inflater.inflate(R.layout.fragment_exercise_history, container, false);

        recent_historyView = (ListView) fView.findViewById(R.id.recent_history);
        createExerciseHistoryList();
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


    private void createExerciseHistoryList(){
        String url = "https://138.68.158.127/get_exercise_history_of_user";

        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Initialization of history array
                        final ArrayList<JSONObject> history = new ArrayList<>();

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //From the response create the history array
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    history.add(new JSONObject(jsonArray.getString(i)));
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }


                        // Define the groupView adapter

                        ArrayAdapter<JSONObject> adapter = new ArrayAdapter<JSONObject>(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, history) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                                text2.setTextColor(Color.LTGRAY);

                                try {
                                    text1.setText(history.get(position).getString("Exercise_name"));
                                    text2.setText(history.get(position).getString("Date_Time"));
                                } catch (JSONException je) {
                                    Log.e(TAG, je.toString());
                                }


                                return view;
                            }
                        };

                        recent_historyView.setAdapter(adapter);
                        // Set the listeners on the list items
                        recent_historyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
                                String exercise_name = null;
                                try {
                                    exercise_name = ((JSONObject) parent.getAdapter().getItem(position)).getString("Exercise_name");
                                } catch (JSONException je) {
                                    Log.e(TAG, je.toString());
                                }

                                DetailedExerciseHistoryActivity.exerciseHistoryItem = (JSONObject) parent.getAdapter().getItem(position);
                                Intent intent = new Intent(getActivity(), DetailedExerciseHistoryActivity.class);
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


    public interface OnFragmentInteractionListener {
        // Needed to compile
        void onFragmentInteraction(Uri uri);
    }

}
