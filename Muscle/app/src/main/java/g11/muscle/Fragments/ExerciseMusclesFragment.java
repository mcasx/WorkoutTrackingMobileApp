package g11.muscle.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;
import g11.muscle.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseMusclesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ExerciseMusclesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private LayoutInflater inflater;
    private View fView;

    private String email, exercise;
    private ImageView muscleImage;
    //GUI
    private TextView nameTV;

    private VolleyProvider req_queue;

    public ExerciseMusclesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        fView = inflater.inflate(R.layout.fragment_exercise_muscles, container, false);
        req_queue = VolleyProvider.getInstance(getActivity());

        // Information from previous activity
        final Intent intent = getActivity().getIntent();

        exercise = intent.getStringExtra("exercise_name");
        email = intent.getStringExtra("email");

        muscleImage = (ImageView)fView.findViewById(R.id.muscleImage);
        //nameTV = (TextView) fView.findViewById(R.id.Ex_Name);
        //nameTV.setText(exercise);

        setImage();

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
        void onFragmentInteraction(Uri uri);
    }

    public static Drawable loadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public void setImage() {
        String url = DBConnect.serverURL + "/get_exercise_image_name";

        StringRequest StrHistReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {

                Picasso.with(getActivity())
                        .load("http://138.68.158.127/muscle%20images/m_" + response)
                        .into(muscleImage);

                //muscleImage.setImageDrawable(loadImageFromWebOperations("http://138.68.158.127/muscle%20images/m_" + response));


                //Log.e("Imagens e tal", "http://138.68.158.127/muscle%20images/m_" + response);
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
                params.put("exercise_name", "" + exercise);

                return params;
            }
        };

        req_queue.addRequest(StrHistReq);
    }

}
