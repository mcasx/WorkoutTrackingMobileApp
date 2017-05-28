package g11.muscle.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DetailedExerciseHistoryActivity;
import g11.muscle.ProfileActivity;
import g11.muscle.R;
import g11.muscle.DB.VolleyProvider;

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
    private VolleyProvider search_queue;
    private ArrayAdapter<String> people_list_adapter;
    private HashMap<String,Bitmap> userProfilePicture = new HashMap<>();

    //GUI
    private ListView feedView;
    private ListView people_listView;
    private SearchView search_barView;
    private ProgressBar progressBar;
    private View fView;

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
        setHasOptionsMenu(true);
        req_queue = VolleyProvider.getInstance(getActivity());
        email = getActivity().getIntent().getStringExtra("email");
        amount = 20;
    }

    // SearchView Stuff
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.add_friend_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.add_friend:
                DrawerLayout mDrawerLayout = (DrawerLayout) getActivity().findViewById(R.id.drawer_layout);
                if(mDrawerLayout.isDrawerOpen(Gravity.END)) {
                    mDrawerLayout.closeDrawer(Gravity.END); // set Gravity as per your need
                }else{
                    mDrawerLayout.openDrawer(Gravity.END); // set Gravity as per your need
                }
                break;
            //R.id.add_friend:
            //LinearLayout drawer = (LinearLayout) getActivity().findViewById(R.id.drawer);
            //drawer.setGravity(Gravity.END);
        }
        return super.onOptionsItemSelected(item);
    }
    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fView = inflater.inflate(R.layout.fragment_feed, container, false);

        //GUI elements

        progressBar = (ProgressBar)(fView.findViewById(R.id.feedProgressBar));
        progressBar.setVisibility(View.VISIBLE);

        feedView = (ListView) fView.findViewById(R.id.feed);
        people_listView = (ListView) fView.findViewById(R.id.people_list);

        people_list_adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);
        people_listView.setAdapter(people_list_adapter);

        people_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                String profile_email = (String) parent.getAdapter().getItem(position);
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtra("user_email", email);
                intent.putExtra("profile_email", profile_email);
                startActivity(intent);
            }
        });

        search_barView = (SearchView) fView.findViewById(R.id.search_bar);
        search_queue = VolleyProvider.getInstance(getActivity());
        search_barView.setOnQueryTextListener(new SearchView.OnQueryTextListener(){
            @Override
            public boolean onQueryTextSubmit(String query) {
                search_barView.onActionViewCollapsed();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(newText.equals("")) recommendedFollows();
                else {
                    search_queue.getQueue().cancelAll(getActivity());
                    searchResponse(newText);
                }
                return true;
            }

            private void searchResponse(final String query){
                String url = DBConnect.serverURL + "/get_users_like";

                //Create the exercise history request
                StringRequest StrUsersLikeReq = new StringRequest(Request.Method.POST,url,
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
                                            JSONObject jo = jsonArray.getJSONObject(i);
                                            history[i] = jo.getString("Email");
                                        }
                                    } catch (JSONException je) {
                                        Log.e(TAG, je.toString());
                                    }
                                }catch (JSONException e2){
                                    e2.printStackTrace();
                                }

                                // Define the groupView adapter
                                people_list_adapter.clear();
                                people_list_adapter.addAll(history);

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
                        params.put("email", query);
                        return params;
                    }
                };

                //Queue the request

                search_queue.addRequest(StrUsersLikeReq);
            }
        });



        // Inflate the layout for this fragment
        return fView;
    }

    private void recommendedFollows(){
        String url = DBConnect.serverURL + "/get_recommended_follows";

        //Create the exercise history request
        StringRequest StrUsersLikeReq = new StringRequest(Request.Method.POST,url,
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
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    history[i] = jo.getString("Following");
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        // Define the groupView adapter
                        people_list_adapter.clear();
                        people_list_adapter.addAll(history);
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
                params.put("email_user", search_barView.getQuery().toString());
                params.put("limit", "20");
                return params;
            }
        };

        //Queue the request
        search_queue.addRequest(StrUsersLikeReq);
    }

    @Override
    public void onStart(){
        super.onStart();
        createUserFeed();
        recommendedFollows();

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
        String url = DBConnect.serverURL + "/get_user_feed_and_pictures";

        //Create the exercise history request
        StringRequest StrFeedReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressBar.setVisibility(View.INVISIBLE);
                        System.out.println(response);
                        // Initialization of history array
                        final ArrayList<feedItem> history = new ArrayList<>();
                        feedItem[] temp = new feedItem[0];
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = (JSONArray) jsonObject.get("feed");
                            JSONObject jsonProfile = jsonObject.getJSONObject("pictures");

                            // put necessary profile pictures in dict
                            try{
                                for(Iterator keys = jsonProfile.keys();keys.hasNext();) {

                                    String user_key = (String) keys.next();
                                    String b64Pic = jsonProfile.getString(user_key);
                                    if(b64Pic == null || b64Pic.equals("null"))
                                        continue;
                                    byte[] imageBytes = Base64.decode(b64Pic, Base64.DEFAULT);
                                    Bitmap user_img = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                    userProfilePicture.put(user_key, user_img);
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }

                            //From the response create the history array
                            temp = new feedItem[jsonArray.length()];
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    history.add(new feedItem(jo));
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        // Define the groupView adapter


                        FeedViewAdapter adapter = new FeedViewAdapter(getActivity(), history.toArray(temp));
                        feedView.setAdapter(adapter);

                        // Set the listeners on the list items

                        feedView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
                                DetailedExerciseHistoryActivity.exerciseHistoryItem = history.get(position).getJsonObj();
                                startActivity(new Intent(getActivity(), DetailedExerciseHistoryActivity.class));
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

    private class feedItem {
        private String exercise_name;
        private String user;
        private int set_amount;
        private String datetime;
        private JSONObject jsonObj;

        public feedItem(JSONObject jo) {
            try {
                exercise_name = jo.getString("Exercise_name");
                user = jo.getString("User_email");
                set_amount = jo.getInt("Set_amount");
                datetime = jo.getString("Date_Time").substring(0, 16);
                jsonObj = jo;
            } catch (JSONException je) {
                Log.e(TAG, "Exception creating feedItem", je);
            }
        }

        public String getExercise_name() {
            return exercise_name;
        }

        public String getUser() {
            return user;
        }

        public int getSet_amount() {
            return set_amount;
        }

        public String getDatetime() {
            return datetime;
        }

        public Bitmap getUserImage() {
            return userProfilePicture.get(getUser());
        }

        public JSONObject getJsonObj(){
            return jsonObj;
        }

    }

    public class FeedViewAdapter extends BaseAdapter {

        public feedItem[] list;
        Activity activity;
        TextView txtExercise;
        TextView txtUser;
        TextView txtDate;
        ImageView imgUser;

        private FeedViewAdapter(Activity activity, feedItem[] list){
            super();
            this.activity=activity;
            this.list=list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.length;
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list[position];
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            // used to prevent updates when scrolling, could be desirable so feel free to remove it
            if (convertView != null) return convertView;

            LayoutInflater inflater=activity.getLayoutInflater();

            if(convertView == null){

                convertView=inflater.inflate(R.layout.feed_card, null);

                txtExercise=(TextView) convertView.findViewById(R.id.exercise);
                txtUser=(TextView) convertView.findViewById(R.id.user);
                txtDate=(TextView) convertView.findViewById(R.id.date);
                txtUser.setTextColor(Color.LTGRAY);
                txtDate.setTextColor(Color.LTGRAY);

                imgUser=(ImageView) convertView.findViewById(R.id.user_pic);

            }



            feedItem item=list[position];
            txtExercise.setText(item.getSet_amount() + " sets of " + item.getExercise_name());
            txtUser.setText(item.getUser());
            txtDate.setText(item.getDatetime());
            Bitmap profile = item.getUserImage();
            if(profile != null)
                imgUser.setImageBitmap(profile);
            return convertView;
        }
    }
}