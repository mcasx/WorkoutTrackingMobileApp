package g11.muscle;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
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
import java.util.Iterator;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;

public class DetailedRunningActivity extends AppCompatActivity {

    public static JSONObject exerciseHistoryItem = null;
    private TextView averageSpeed;
    private TextView distance;
    private TextView time;
    private TextView duration;
    private VolleyProvider req_queue;
    private String TAG = "DetailedRunningActivity";
    private LinearLayout commentsBaseLayout;
    private HashMap<String,Bitmap> userProfilePicture = new HashMap<>();
    private boolean hasThumbs = false;
    LayoutInflater inflater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_running);
        averageSpeed = (TextView) findViewById(R.id.averageSpeedText);
        distance = (TextView) findViewById(R.id.distanceText);
        time = (TextView) findViewById(R.id.timeText);
        duration = (TextView) findViewById(R.id.durationText);
        req_queue = VolleyProvider.getInstance(this);
        commentsBaseLayout = (LinearLayout)findViewById(R.id.commentsBaseLayout);
        inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View commentView = getLayoutInflater().inflate(R.layout.comments, null);
        //commentsBaseLayout.addView(commentView);

        setCard();
        setCommentCards();

        try {
            setTitle(exerciseHistoryItem.getString("Exercise_name"));
            getSupportActionBar().setSubtitle(exerciseHistoryItem.getString("User_email"));
        }catch (JSONException je) {
            Log.e(TAG, je.toString());
        }
    }


    private void setCard(){
        String url = DBConnect.serverURL + "/get_sets_of_exercise_history";

        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    JSONObject set;

                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {
                        try {
                            set = new JSONObject(new JSONArray(response).get(0).toString());
                            averageSpeed.setText(exerciseHistoryItem.getString("Average_intensity") + " Km/h");
                            //Log.e(TAG, exerciseHistoryItem.getString("Average_intensity"));
                            distance.setText(set.getInt("Repetitions")/1000 + " Km");
                            time.setText(exerciseHistoryItem.getString("Date_Time").substring(5, 26));
                            int secs = set.getInt("Weight");
                            String min = secs/60 > 10 ? "" + secs/60 : "0" + secs/60;
                            String sec = secs%60 > 10 ? "" + secs%60 : "0" + secs%60;
                            duration.setText( min + ":" + sec);

                        } catch (JSONException e) {
                            e.printStackTrace();
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

    public void onClickSend(View view){

        String url = DBConnect.serverURL + "/add_comment_to_exercise";
        EditText et = ((EditText)findViewById(R.id.commentInput));
        et.setEnabled(false);

        if(et.getText().toString().trim().equals("")){
            new AlertDialog.Builder(DetailedRunningActivity.this)
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
                        new AlertDialog.Builder(DetailedRunningActivity.this)
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
                    params.put("comment", ((EditText)findViewById(R.id.commentInput)).getText().toString());
                    params.put("email", getSharedPreferences("UserData", 0).getString("email", null));
                }catch(JSONException je){Log.e(TAG, "No Exercise History ID");}
                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(StrHistReq);
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

                            Log.e(TAG, response);
                            JSONObject jsonObject = new JSONObject(response);

                            JSONArray comments = jsonObject.getJSONArray("comments");
                            //From the response create the sets array
                            JSONObject jsonProfile = jsonObject.getJSONObject("pictures");

                            commentsBaseLayout.removeAllViews();


                            //initialize dict with needed pics
                            for(Iterator keys = jsonProfile.keys(); keys.hasNext();) {

                                String user_key = (String) keys.next();
                                String b64Pic = jsonProfile.getString(user_key);
                                if(b64Pic == null || b64Pic.equals("null"))
                                    continue;
                                Log.i("TG",user_key);
                                Log.i("TG",b64Pic);
                                byte[] imageBytes = Base64.decode(b64Pic, Base64.DEFAULT);
                                Bitmap user_img = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                userProfilePicture.put(user_key, user_img);
                            }


                            for (int i = 0; i < comments.length(); i++) {
                                View commentView = getLayoutInflater().inflate(R.layout.comments, null);

                                ImageView userPicture = (ImageView) commentView.findViewById(R.id.userCommentImage);
                                TextView userName = (TextView)commentView.findViewById(R.id.commentsUserName);
                                TextView commentText = (TextView)commentView.findViewById(R.id.commentText);
                                JSONObject obj = new JSONObject(comments.getString(i));

                                String user_name = obj.getString("Name");

                                Bitmap profile = userProfilePicture.get(obj.getString("User_email"));


                                if(profile != null) {
                                    Log.i("TG", profile.toString());
                                    userPicture.setImageBitmap(profile);
                                }

                                userName.setText(user_name);
                                commentText.setText(obj.getString("Comment"));
                                commentText.setKeyListener(null);

                                commentsBaseLayout.addView(commentView);

                                //createCardViewProgrammatically(new JSONObject(jsonArray.getString(i)));

                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        EditText et = ((EditText)findViewById(R.id.commentInput));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailed_exercise_menu, menu);

        if(hasThumbs){
            (menu.findItem(R.id.thumbsUp)).setIcon(R.drawable.ic_thumb_up_full_white_24dp);
        }

        else
            (menu.findItem(R.id.thumbsUp)).setIcon(R.drawable.ic_thumb_up_white_24dp);

        try {
            if(!getSharedPreferences("UserData", 0).getString("email", null).equals(exerciseHistoryItem.getString("User_email"))){
                menu.findItem(R.id.share).setVisible(false);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.thumbsUp:
                // User chose the "Settings" item, show the app settings UI...
                item.setEnabled(false);
                thumbsUp(item);
                return true;

            case R.id.share:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                share();
                return true;

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void share(){

        String url = DBConnect.serverURL + "/is_exercise_history_shared";
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onResponse(String response) {

                        // Initialization of sets array

                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            //From the response create the sets array


                            JSONObject obj = new JSONObject(jsonArray.getString(0));
                            String isShared = obj.getString("Shared");


                            if(isShared.equals("1")){
                                new AlertDialog.Builder(DetailedRunningActivity.this)
                                        .setIcon(android.R.drawable.ic_menu_share)
                                        .setTitle("Share")
                                        .setMessage("You're currently sharing this page. Do you wish to keep sharing or do you want to stop?")
                                        .setPositiveButton("Keep", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                setShared(1);
                                            }
                                        })
                                        .setNegativeButton("Stop", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                setShared(0);
                                            }
                                        })
                                        .show();

                            }
                            else {
                                new AlertDialog.Builder(DetailedRunningActivity.this)
                                        .setIcon(android.R.drawable.ic_menu_share)
                                        .setTitle("Share")
                                        .setMessage("Do you wish to share this page?")
                                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                setShared(1);
                                            }
                                        })
                                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                setShared(0);
                                            }
                                        })
                                        .show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
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
                    params.put("exercise_id", DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("ID"));
                    Log.e("Exercise ID: ", DetailedExerciseHistoryActivity.exerciseHistoryItem.getString("ID"));
                }catch(JSONException je){Log.e(TAG, "No Exercise History ID");}

                return params;
            }
        };
        req_queue.addRequest(StrHistReq);
    }

    private void setShared(final int isShared){
        String url = DBConnect.serverURL + "/set_exercise_history_shared";
        StringRequest StrHistReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {

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
                    params.put("shared", "" + isShared);
                }catch(JSONException je){Log.e(TAG, "No Exercise History ID");}

                return params;
            }
        };

        req_queue.addRequest(StrHistReq);
    }

    private void thumbsUp(final MenuItem item){
        String url = hasThumbs ? DBConnect.serverURL + "/delete_user_bump" : DBConnect.serverURL + "/add_user_bump";

        StringRequest StrHistReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {
                hasThumbs = !hasThumbs;
                invalidateOptionsMenu();
                item.setEnabled(true);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        AlertDialog alertDialog = new AlertDialog.Builder(DetailedRunningActivity.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        //"Please connect your device to the Internet and try again")

                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
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
                    params.put("email", "" + getSharedPreferences("UserData", 0).getString("email", null));
                }catch(JSONException je){Log.e(TAG, "No Exercise History ID");}

                return params;
            }
        };

        req_queue.addRequest(StrHistReq);
    }


    private void hasThumbsUp(){
        String url = DBConnect.serverURL + "/does_user_bump";

        StringRequest StrHistReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onResponse(String response) {
                if(response.equals("true")){
                    hasThumbs = true;
                }
                else{
                    hasThumbs = false;
                }
                invalidateOptionsMenu();
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
                    params.put("email", "" + getSharedPreferences("UserData", 0).getString("email", null));
                }catch(JSONException je){Log.e(TAG, "No Exercise History ID");}

                return params;
            }
        };
        req_queue.addRequest(StrHistReq);
    }

}
