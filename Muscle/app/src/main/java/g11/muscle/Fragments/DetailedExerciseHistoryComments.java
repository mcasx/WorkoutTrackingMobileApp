package g11.muscle.Fragments;

import android.graphics.drawable.Icon;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DetailedExerciseHistoryActivity;
import g11.muscle.R;
import g11.muscle.DB.VolleyProvider;


/**
 * A simple {@link Fragment} subclass.
 */
public class DetailedExerciseHistoryComments extends Fragment {


    final String TAG = "DetExerciseHistComments";
    private VolleyProvider req_queue;
    private LayoutInflater inflater;
    private LinearLayout commentsBaseLayout;

    public DetailedExerciseHistoryComments() {
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
        // Inflate the layout for this fragment
        View fview = inflater.inflate(R.layout.fragment_detailed_exercise_history_comments, container, false);
        this.inflater = inflater;
        commentsBaseLayout = (LinearLayout)fview.findViewById(R.id.commentsBaseLayout);
        setCommentCards();
        ImageButton sendButton = (ImageButton)fview.findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSend();
            }
        });

        return fview;
    }


    private void setCommentCards(){
        String url = DBConnect.serverURL + "/get_comments_exercise";

        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {

                        // Initialization of sets array

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //From the response create the sets array


                            if(jsonArray.length() == 0)
                                new AlertDialog.Builder(getContext())
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle("No comments")
                                        .setMessage("This exercise currently has no comments")
                                        .setPositiveButton("Ok", null)
                                        .show();

                            commentsBaseLayout.removeAllViews();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                View commentView = inflater.inflate(R.layout.comments, null);
                                TextView userName = (TextView)commentView.findViewById(R.id.commentsUserName);
                                TextView commentText = (TextView)commentView.findViewById(R.id.commentText);
                                JSONObject obj = new JSONObject(jsonArray.getString(i));

                                userName.setText(obj.getString("Name"));
                                commentText.setText(obj.getString("Comment"));
                                commentText.setKeyListener(null);

                                commentsBaseLayout.addView(commentView);

                                //createCardViewProgrammatically(new JSONObject(jsonArray.getString(i)));

                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        EditText et = ((EditText)getActivity().findViewById(R.id.commentInput));
                        et.setText("");
                        et.setEnabled(true);

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
                    params.put("exercise_id", DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("ID"));
                    Log.e("Exercise ID: ", DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("ID"));
                }catch(JSONException je){Log.e(TAG, "No Exercise History ID");}

                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(StrHistReq);
    }

    public void onClickSend(){


        String url = DBConnect.serverURL + "/add_comment_to_exercise";
        EditText et = ((EditText)getActivity().findViewById(R.id.commentInput));
        et.setEnabled(false);

        if(et.getText().toString().trim().equals("")){
            new AlertDialog.Builder(getContext())
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Empty Comment")
                    .setMessage("You can't send an empty comment!")
                    .setPositiveButton("Ok", null)
                    .show();
            et.setEnabled(true);
            return;
        }

        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {

                        setCommentCards();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        new AlertDialog.Builder(getContext())
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .setTitle("Can't send comment")
                                .setMessage("An error occurred and your comment could not be sent")
                                .setPositiveButton("Ok", null)
                                .show();
                        Log.e(TAG, error.toString());
                    }
                }
        ){
            // use params are specified here
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                try {
                    params.put("exercise_id", DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("ID"));
                    params.put("comment", ((EditText)getActivity().findViewById(R.id.commentInput)).getText().toString());
                    params.put("email", getActivity().getSharedPreferences("UserData", 0).getString("email", null));
                }catch(JSONException je){Log.e(TAG, "No Exercise History ID");}
                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(StrHistReq);
    }
}
