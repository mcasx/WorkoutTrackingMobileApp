package g11.muscle.Fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.List;
import java.util.Map;

import g11.muscle.Classes.ExerciseHistoryAdapter;
import g11.muscle.DB.DBConnect;
import g11.muscle.DB.MuscleDbContract;
import g11.muscle.DetailedExerciseHistoryActivity;
import g11.muscle.HomeActivity;
import g11.muscle.R;
import g11.muscle.DB.VolleyProvider;


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

    private String email;

    // Used by Main Activity
    private OnFragmentInteractionListener mListener;

    private VolleyProvider req_queue;
    private RecyclerView recent_historyView;
    private ProgressBar progressBar;
    private List<JSONObject> originalList;
    private ExerciseHistoryAdapter adapter;
    private ArrayList<JSONObject> history;

    public ExerciseHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        req_queue = VolleyProvider.getInstance(getActivity());
        email = getActivity().getIntent().getStringExtra("email");

    }

    // SearchView Stuff
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.search_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem searchViewItem = menu.findItem(R.id.search);

        //SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        //MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        final SearchView searchViewAndroidActionBar = (SearchView) MenuItemCompat.getActionView(searchViewItem);
        searchViewAndroidActionBar.setMaxWidth( Integer.MAX_VALUE );

        searchViewAndroidActionBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchViewAndroidActionBar.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText))
                    adapter.getFilter().filter("");
                else
                    adapter.getFilter().filter(newText);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.search:
                //not sure
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fragment View
        View fView = inflater.inflate(R.layout.fragment_exercise_history, container, false);

        progressBar = (ProgressBar) fView.findViewById(R.id.exerciseHistoryProgressBar);
        progressBar.setVisibility(View.VISIBLE);

        recent_historyView = (RecyclerView) fView.findViewById(R.id.recent_history);
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
        String url = DBConnect.serverURL + "/get_exercise_history_of_user";

        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Initialization of history array
                        history = new ArrayList<>();

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

                        adapter = new ExerciseHistoryAdapter(history);
                        recent_historyView.setAdapter(adapter);
                        // Set the listeners on the list items

                        LinearLayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                        recent_historyView.setLayoutManager(mLayoutManager);
                        recent_historyView.addItemDecoration(new DividerItemDecoration(getActivity(),mLayoutManager.getOrientation()));
                        adapter.mOnClickListener = new HistoryOnClickListener();

                        progressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        progressBar.setVisibility(View.GONE);
                        System.out.println(error.toString());
                    }
                }
        ){
            // use params are specified herere
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("email", email);
                params.put("limit", "100");
                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(StrHistReq);
    }


    public interface OnFragmentInteractionListener {
        // Needed to compile
        void onFragmentInteraction(Uri uri);
    }

    private class HistoryOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(final View view) {
            int itemPosition = recent_historyView.getChildLayoutPosition(view);
            JSONObject item = history.get(itemPosition);
            String exercise_name = null;
            try {
                exercise_name = (item.getString("Exercise_name"));
            } catch (JSONException je) {
                Log.e(TAG, je.toString());
            }

            String email = getContext().getSharedPreferences("UserData",0).getString("email", null);
            DetailedExerciseHistoryActivity.exerciseHistoryItem = item;

            Intent intent = new Intent(getActivity(), DetailedExerciseHistoryActivity.class);

            intent.putExtra("email", email);
            intent.putExtra("exercise_name",exercise_name);
            startActivity(intent);
        }

    }

}
