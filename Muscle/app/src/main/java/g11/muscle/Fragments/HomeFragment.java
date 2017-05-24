package g11.muscle.Fragments;

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
import android.widget.ListView;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;

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
public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";

    private OnFragmentInteractionListener mListener;

    private String email;

    //GUI
    private ListView recExView;
    private ListView recPlanView;

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
        View fView = inflater.inflate(R.layout.fragment_home, container, false);
        recExView = (ListView) fView.findViewById(R.id.rec_ex);
        recPlanView = (ListView) fView.findViewById(R.id.rec_plan);

        getRecommendedExercises();
        getRecommendedPlans();
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
                        recExView.setAdapter(adapter);
                        recExView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
                        System.out.println(error.toString());
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
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmp = jsonArray.getJSONObject(i).getString("ID");
                                    // TODO Needs a way to list plans by count
                                    rP[i] = tmp;
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }
                        } catch (JSONException e2) {
                            e2.printStackTrace();
                        }

                        //TODO REMOVE THIS
                        cola = new String[rP.length];
                        for(int i = 0; i < rP.length;i++){
                            cola[i] = "Plan "+ rP[i];
                        }

                        // Define the groupView adapter
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, cola);
                        recPlanView.setAdapter(adapter);
                        recPlanView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
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
}
