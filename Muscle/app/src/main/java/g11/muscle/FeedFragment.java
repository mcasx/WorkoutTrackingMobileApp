package g11.muscle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FeedFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FeedFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FeedFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "FeedFragment";

    private String email;
    private int amount;

    private OnFragmentInteractionListener mListener;

    private VolleyProvider req_queue;

    //GUI
    private GridView feedView;

    public FeedFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FeedFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FeedFragment newInstance(String param1, String param2) {
        FeedFragment fragment = new FeedFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        req_queue = VolleyProvider.getInstance(getActivity());
        email = getActivity().getIntent().getStringExtra("email");
        amount = 20;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fView = inflater.inflate(R.layout.fragment_feed, container, false);
        //GUI elements
        feedView = (GridView) fView.findViewById(R.id.feed);
        // Inflate the layout for this fragment
        return fView;
    }

    @Override
    public void onStart(){
        super.onStart();
        createUserFeed();
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

    private void createUserFeed(){
        String url = "https://138.68.158.127/get_user_feed";

        //Create the exercise history request
        StringRequest StrFeedReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Initialization of history array
                        feedItem[] history = new feedItem[0];
                        System.out.println(response);
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //From the response create the history array
                            history = new feedItem[jsonArray.length()];
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    history[i] = new feedItem(jo);
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        // Define the groupView adapter

                        ArrayAdapter<feedItem> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, history);
                        feedView.setAdapter(adapter);

                        // Set the listeners on the list items
                        feedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
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
                params.put("user_email", email);
                params.put("amount", String.valueOf(amount));
                return params;
            }
        };

        //Queue the request
        req_queue.addRequest(StrFeedReq);
    }

    private class feedItem{
        private String exercise_name;
        private String user;
        private int set_amount;
        private String datetime;

        public feedItem(JSONObject jo){
            try {
                exercise_name = jo.getString("Exercise_name");
                user = jo.getString("User_email");
                set_amount = jo.getInt("Set_amount");
                datetime = jo.getString("Date_Time");
            }catch(JSONException je){
                Log.e(TAG, "Exception creating feedItem", je);
            }
        }

        @Override
        public String toString(){
            return this.user + " did " + this.set_amount + " sets of " + this.exercise_name + "!\n" + this.datetime;
        }
    }
}
