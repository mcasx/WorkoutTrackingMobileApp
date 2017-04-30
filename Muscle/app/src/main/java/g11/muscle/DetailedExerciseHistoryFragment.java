package g11.muscle;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

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
import android.widget.LinearLayout.LayoutParams;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;


public class DetailedExerciseHistoryFragment extends Fragment{


    private TextView averageIntensityText;
    private TextView numberOfSetsText;
    private VolleyProvider req_queue;
    private Context context;
    private LinearLayout baseLayout;
    private View fView;
    final ArrayList<JSONObject> sets = new ArrayList<>();
    private final String TAG = "DetExerHistoryFragment";
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

        fView = inflater.inflate(R.layout.fragment_detailed_exercise_history, container, false);

        try {
            averageIntensityText = (TextView) fView.findViewById(R.id.averageIntensityText);
            numberOfSetsText = (TextView) fView.findViewById(R.id.numberOfSetsText);
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

        getSetCards();

        return fView;

    }


    private void getSetCards(){
        String url = "https://138.68.158.127/get_sets_of_exercise_history";

        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        // Initialization of sets array

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //From the response create the sets array
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    createCardViewProgrammatically(new JSONObject(jsonArray.getString(i)));
                                    break;
                                }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }


                        // Define the groupView adapter

                        ArrayAdapter<JSONObject> adapter = new ArrayAdapter<JSONObject>(getActivity(), android.R.layout.simple_list_item_2, android.R.id.text1, sets) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text1 = (TextView) view.findViewById(android.R.id.text1);
                                TextView text2 = (TextView) view.findViewById(android.R.id.text2);
                                text2.setTextColor(Color.LTGRAY);

                                try {
                                    text1.setText(sets.get(position).getString("Exercise_name"));
                                    text2.setText(sets.get(position).getString("Date_Time"));
                                } catch (JSONException je) {
                                    Log.e(TAG, je.toString());
                                }


                                return view;
                            }
                        };




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


    public void createCardViewProgrammatically(JSONObject set){

        CardView cardview;

        cardview = new CardView(context);

        LayoutParams layoutparams = new LayoutParams(

                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT

        );

        float d = context.getResources().getDisplayMetrics().density;
        int margin = (int)(8 * d);
        layoutparams.setMargins(margin ,(int) (25 * d) , margin, 0);
        cardview.setLayoutParams(layoutparams);
        TextView textview = new TextView(context);

        //textview.setLayoutParams(layoutparams);

        try {
            textview.setText("Set " + set.getString("Set_number"));
        }catch (JSONException je){ Log.e(TAG, "Set_number");}

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //textview.setTextAppearance(android.R.style.TextAppearance_Material);
        }

        textview.setPadding(25,25,25,25);

        textview.setGravity(Gravity.CENTER);


        cardview.setBackgroundColor(Color.parseColor("#424242"));


        ConstraintLayout cardConstrainLayout = new ConstraintLayout(context);
        cardConstrainLayout.addView(textview);

        ConstraintSet cset = new ConstraintSet();
        cset.clone(cardConstrainLayout);
        cset.connect(textview.getId(), ConstraintSet.LEFT, cardConstrainLayout.getId(), ConstraintSet.LEFT, (int)(8*d));
        cset.connect(textview.getId(), ConstraintSet.TOP, cardConstrainLayout.getId(), ConstraintSet.TOP, (int)(8*d));
        cset.applyTo(cardConstrainLayout);

        cardview.addView(cardConstrainLayout);

        //baseLayout.addView(cardConstrainLayout);

    }

}

