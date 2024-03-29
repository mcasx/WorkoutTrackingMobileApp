package g11.muscle.Fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DetailedExerciseHistoryActivity;
import g11.muscle.R;
import g11.muscle.DB.VolleyProvider;


public class DetailedExerciseHistoryFragment extends Fragment{
    private final String TAG = "DetExerHistoryFragment";

    private TextView averageIntensityText;
    private TextView numberOfSetsText;
    private VolleyProvider req_queue;
    private Context context;
    private LinearLayout baseLayout;
    private View fView;
    private LayoutInflater inflater;
    final ArrayList<JSONObject> sets = new ArrayList<>();
    private ImageView image;
    public DetailedExerciseHistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        req_queue = VolleyProvider.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        this.inflater = inflater;

        fView = inflater.inflate(R.layout.fragment_detailed_exercise_history, container, false);

        image = (ImageView) fView.findViewById(R.id.exerciseImage);

        Context context = image.getContext();
        int id = 0;
        try {
            Log.e(TAG, DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("Exercise_name").replace('-', '_').replace(' ','_').toLowerCase());
            id = context.getResources().getIdentifier(DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("Exercise_name").replace('-', '_').replace(' ','_').toLowerCase(), "drawable", context.getPackageName());

        } catch (JSONException e) {
            e.printStackTrace();
        }
        image.setImageResource(id);


        try {
            averageIntensityText = (TextView) fView.findViewById(R.id.repetitionsText);
            numberOfSetsText = (TextView) fView.findViewById(R.id.numOfSets);
        }catch (NullPointerException ne){
            Log.e(TAG, ne.toString());
        }

        try {
            averageIntensityText.setText(DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("Average_intensity") + "  ");
            numberOfSetsText.setText(DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("Set_amount") + "  ");
        }catch(JSONException je){
            Log.e(TAG, je.toString());
        }

        context = getActivity().getApplicationContext();

        baseLayout = (LinearLayout) fView.findViewById(R.id.baseLayout);

        TextView timeText = (TextView)fView.findViewById(R.id.timeText);


        try {
            String str = DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("Date_Time");
            timeText.setText(str.substring(0, str.length() - 4));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        getSetCards();

        return fView;

    }


    private void getSetCards(){
        String url = DBConnect.serverURL + "/get_sets_of_exercise_history";

        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //From the response create the sets array
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    createCardViewProgrammatically(new JSONObject(jsonArray.getString(i)));
                                }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }
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
                try {
                    params.put("id", DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("ID"));
                    Log.e("Exercise History ID: ", DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("ID"));
                }catch(JSONException je){Log.e(TAG, "No Exercise History ID");}

                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(StrHistReq);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void createCardViewProgrammatically(JSONObject set){

        View child = inflater.inflate(R.layout.card_view_detailed_exercise_activity_model, null);

        TextView setText = (TextView) child.findViewById(R.id.setText);
        TextView repetitionsText = (TextView) child.findViewById(R.id.repetitionsText);
        TextView weightText = (TextView) child.findViewById(R.id.timeText);
        TextView intensityText = (TextView) child.findViewById(R.id.intensityText);
        TextView restingTimeText = (TextView) child.findViewById(R.id.restingTimeText);

        try {
            setText.setText("Set " + set.getString("Set_number"));
            repetitionsText.setText(set.getString("Repetitions"));
            weightText.setText(set.getString("Weight"));
            intensityText.setText(set.getString("Intensity"));
            restingTimeText.setText(set.getString("Resting_Time"));
        }catch (JSONException je){ Log.e(TAG, "Set_number");}

        baseLayout.addView(child);
    }
}

