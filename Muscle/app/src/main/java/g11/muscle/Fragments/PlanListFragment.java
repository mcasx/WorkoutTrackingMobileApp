package g11.muscle.Fragments;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DetailedExerciseHistoryActivity;
import g11.muscle.LoginActivity;
import g11.muscle.PlanActivity;
import g11.muscle.ProfileActivity;
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
public class PlanListFragment extends Fragment {

    private static final String TAG = "PlanListFragment";
    private static final String ERROR_MSG = "Please try to reconnect";



    // Used by Main Activity
    private OnFragmentInteractionListener mListener;
    private ProgressBar planListProgressBar;
    private VolleyProvider req_queue;
    private ListView plan_listView;


    public PlanListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        req_queue = VolleyProvider.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fragment View
        View fView = inflater.inflate(R.layout.fragment_plan_list, container, false);

        planListProgressBar = (ProgressBar) fView.findViewById(R.id.planListProgressBar);
        planListProgressBar.setVisibility(View.VISIBLE);
        plan_listView = (ListView) fView.findViewById(R.id.plan_list);
        plan_listView.setVisibility(View.INVISIBLE);
        createPlanList();
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


    private void createPlanList(){
        String url = DBConnect.serverURL + "/get_plans";

        //Create the exercise history request
        StringRequest PlanListReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        final ArrayList<PlanItem> history = new ArrayList<>();
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //From the response create the history array
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jo = jsonArray.getJSONObject(i);
                                    history.add(new PlanItem(jo));
                                }
                            } catch (JSONException je) {
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }
                        // Define the groupView adapter


                        ListViewAdapter adapter = new ListViewAdapter(getActivity(), history);
                        plan_listView.setAdapter(adapter);
                        plan_listView.setVisibility(View.VISIBLE);
                        planListProgressBar.setVisibility(View.GONE);
                        // Set the listeners on the list items

                        plan_listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                                //Go to exercise page
                                String email = getContext().getSharedPreferences("UserData",0).getString("email", null);
                                Intent intent = new Intent(getActivity(), PlanActivity.class);
                                intent.putExtra("email", email);
                                intent.putExtra("plan_id", ""  + history.get(position).getPlan_id());
                                startActivity(intent);
                            }
                        });
                        planListProgressBar.setVisibility(View.GONE);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        /*
                        AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("No Internet Connection");
                        //"Please connect your device to the Internet and try again")
                        alertDialog.setMessage(ERROR_MSG);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();*/
                        planListProgressBar.setVisibility(View.GONE);
                    }
                }
        ){
            // use params are specified here
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("plan_name", "");
                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(PlanListReq);
    }


    public interface OnFragmentInteractionListener {
        // Needed to compile
        void onFragmentInteraction(Uri uri);
    }

    private class PlanItem {
        private String plan_name;
        private int plan_id;
        private String plan_creator;
        private JSONObject jsonObj;

        public PlanItem(JSONObject jo) {
            try {
                plan_name = jo.getString("Name");
                plan_id = jo.getInt("ID");
                plan_creator = jo.getString("Creator");
                jsonObj = jo;
            } catch (JSONException je) {
                Log.e(TAG, "Exception creating feedItem", je);
            }
        }

        public String getPlan_name() {
            return plan_name;
        }

        public int getPlan_id() {
            return plan_id;
        }

        public String getPlan_creator() {
            return plan_creator;
        }

        public JSONObject getJsonObj(){
            return jsonObj;
        }

    }

    public class ListViewAdapter extends BaseAdapter {

        public ArrayList<PlanItem> list;
        Activity activity;
        TextView txtPlan;
        TextView txtCreator;

        public ListViewAdapter(Activity activity, ArrayList<PlanItem> list){
            super();
            this.activity=activity;
            this.list=list;
        }

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return list.get(position);
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

                convertView=inflater.inflate(android.R.layout.simple_list_item_2, null);


                txtPlan=(TextView) convertView.findViewById(android.R.id.text1);
                txtCreator=(TextView) convertView.findViewById(android.R.id.text2);
            }

            PlanItem item=list.get(position);

            txtPlan.setText(item.getPlan_name());
            txtCreator.setText(item.getPlan_creator());
            return convertView;
        }
    }
}
