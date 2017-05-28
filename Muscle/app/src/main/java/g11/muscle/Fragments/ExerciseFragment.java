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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import g11.muscle.Classes.BounceInterpolator;
import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;
import g11.muscle.DetailedExerciseHistoryActivity;
import g11.muscle.ExerciseActivity;
import g11.muscle.FeedBackActivity;
import g11.muscle.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ExerciseFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private LayoutInflater inflater;
    private View fView;


    //Exercise name
    private String exercise;
    //User email
    private String email;

    //GUI
    private TextView exerciseView;
    private TextView kindView;
    private ImageView imgView;

    private TextView descriptionView;



    private VolleyProvider req_queue;



    public ExerciseFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;

        fView = inflater.inflate(R.layout.fragment_exercise, container, false);


        //GUI elements
        exerciseView  = (TextView)fView.findViewById(R.id.exercise);
        kindView  = (TextView)fView.findViewById(R.id.kind);
        imgView = (ImageView)fView.findViewById(R.id.image);
        descriptionView = (TextView)fView.findViewById(R.id.description);

        // Information from previous activity

        final Intent intent = getActivity().getIntent();
        exercise = intent.getStringExtra("exercise_name");
        email = intent.getStringExtra("email");
        // Exercise Name
        exerciseView.setText(exercise);
        req_queue = VolleyProvider.getInstance(getActivity());
        // get exercise information
        String urlEx = DBConnect.serverURL + "/get_exercise";

        StringRequest Ex_Req = new StringRequest(StringRequest.Method.POST, urlEx,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            descriptionView.setText(jsonObject.getString("Description"));
                            kindView.setText(jsonObject.getString("Kind"));

                            //Image name must be in lowercase
                            String DrawableName = jsonObject.getString("Image").toLowerCase();
                            //Can't have extension
                            if(DrawableName.contains("."))
                                DrawableName = DrawableName.split("\\.")[0];
                            int resID = getResources().getIdentifier(DrawableName,"drawable",getActivity().getPackageName());
                            imgView.setImageResource(resID);
                        } catch (JSONException je){
                            Log.e("","GET EXERCISE DATA EXCEPTION");
                            Log.e("", je.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Error Response
                System.out.println(error.toString());
            }
        }
        ) {
            // use params are specified here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("exercise_name", exercise);
                return params;
            }
        };
        req_queue.addRequest(Ex_Req);
        return fView;
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

    //Get last exercise info
    private void getLastExerciseInfo(){
        String urlLast = DBConnect.serverURL + "/get_last_exercise_of_user";

        // get last exercise information
        StringRequest Last_Ex_Req = new StringRequest(Request.Method.POST, urlLast,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            //last_weightView.setText(response.getString("Weight"));
                            //last_repsView.setText(response.getString("Repetitions"));
                            //last_intensityView.setText(response.getString("Intensity"));
                            Log.e("",jsonObject.toString());
                        } catch (Exception e){
                            Log.e("", e.toString());
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // User doesn't have anything in it's exercise history
                Log.i("","User never did this exercise");
            }
        }
        ){
            // use params are specified here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("user_email", email);
                params.put("exercise_name", exercise);
                return params;
            }
        };

        //Queue the request
        req_queue.addRequest(Last_Ex_Req);
    }


    @Override
    public void onStart(){
        super.onStart();
        getLastExerciseInfo();
    }
}
