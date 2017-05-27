package g11.muscle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.ColorRes;

import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import g11.muscle.DB.VolleyProvider;
import g11.muscle.MPChartJava.RadarMarkerView;
import g11.muscle.MPChartJava.LineMarkerView;

import g11.muscle.DB.DBConnect;

public class ProfileActivity extends AppCompatActivity {

    private VolleyProvider req_queue;

    private String user_email;
    private String profile_email;
    private String name;
    private String base64profile_pic;

    private ArrayAdapter<String> followingAdapter;
    private ArrayAdapter<String> followersAdapter;

    private boolean friendFlag;

    //Charts
    private RadarChart rChart;
    private LineChart lChart;

    private ImageView profile_image;
    private TextView height_text;
    private TextView weight_text;

    String baseUrl = DBConnect.serverURL;

    private boolean userProfileFlag = false;

    // Chart font
    private Typeface mTfLight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_email = getIntent().getStringExtra("user_email");
        profile_email = getIntent().getStringExtra("profile_email");

        //Guilherme
        profile_image = (ImageView) findViewById(R.id.profile_img);
        height_text = (TextView) findViewById(R.id.height);
        weight_text = (TextView) findViewById(R.id.weight);

        userProfileFlag = user_email.equals(profile_email);
        ActionBar actionBar  = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        if(userProfileFlag) {
            actionBar.setTitle("Your Profile");
            setUserProfileSetup();
        }

        Log.i("TG",user_email + ", " + profile_email);

        req_queue = VolleyProvider.getInstance(this);

        followersAdapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_list_item_1);
        followingAdapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_list_item_1);

        createUserInfo();
        createFollowerInfo();

        rChart = (RadarChart) findViewById(R.id.rChart);
        lChart = (LineChart) findViewById(R.id.lChart);
        rChart.setVisibility(View.GONE);

        // Font for charts text
        mTfLight = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Light.ttf");
        RadarSetup();
        LineSetup();

    }

    private void createUserInfo() {
        String url = DBConnect.serverURL + "/get_user_profile";

        //Create the exercise history request
        StringRequest StrProfileReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            //From the response create the history array

                            name = jsonObject.getString("Name");
                            setTitle(name+"'s Profile");

                            String gender = jsonObject.getInt("Gender") == 0 ? "Male" : "Female";

                            String height = Integer.toString(jsonObject.getInt("Height")) + " cm";

                            String weight = Double.toString(jsonObject.getDouble("Weight")) + " Kg";

                            String age = Integer.toString(jsonObject.getInt("Age")) + " years old";

                            String b64Pic = (String) jsonObject.getString("Profile_image");

                            if (b64Pic != null && !b64Pic.equals("null")) {

                                byte[] imageBytes = Base64.decode(b64Pic, Base64.DEFAULT);
                                Bitmap user_img = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                ImageView profile_pic = ((ImageView) findViewById(R.id.profile_img));
                                //profile_pic.setScaleType(ImageView.ScaleType.FIT_XY);
                                profile_pic.setImageBitmap(user_img);
                            }

                            ((TextView) findViewById(R.id.username)).setText(name);
                            ((TextView) findViewById(R.id.email)).setText(profile_email);
                            ((TextView) findViewById(R.id.gender)).setText(gender);
                            ((TextView) findViewById(R.id.height)).setText(height);
                            ((TextView) findViewById(R.id.weight)).setText(weight);
                            ((TextView) findViewById(R.id.age)).setText(age);


                        } catch (JSONException e2) {
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
        ) {
            // use params are specified here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("email", profile_email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(StrProfileReq);
    }

    private void createFollowerInfo(){
        String url = DBConnect.serverURL + "/get_follow_info";

        //Create the exercise history request
        StringRequest StrProfileReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        friendFlag = false;
                        followingAdapter.clear();
                        followersAdapter.clear();

                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            //will be usefull later
                            JSONArray jsonFollowingArray = jsonObject.getJSONArray("following");
                            JSONArray jsonFollowersArray = jsonObject.getJSONArray("followers");


                            if (jsonFollowingArray != null)
                                for (int i=0;i<jsonFollowingArray.length();i++)
                                    followingAdapter.add(jsonFollowingArray.getString(i));


                            if (jsonFollowersArray != null)
                                for (int i=0;i<jsonFollowersArray.length();i++) {
                                    String followerStr = jsonFollowersArray.getString(i);
                                    followersAdapter.add(followerStr);
                                    if(followerStr.equals(user_email))
                                        friendFlag = true;
                                }

                            TextView following = (TextView) findViewById(R.id.following);
                            TextView followers = (TextView) findViewById(R.id.followers);

                            following.setText("Following (" + Integer.toString(jsonFollowingArray.length()) + ")");
                            followers.setText("Followers (" + Integer.toString(jsonFollowersArray.length()) + ")");
                            Log.i("TG", String.valueOf(friendFlag));

                            Button fab = (Button) findViewById(R.id.fab);
                            if(friendFlag){
                                //fab.setImageResource(R.drawable.ic_remove_black_24dp);
                                //fab.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorPrimary));
                                fab.setVisibility(View.VISIBLE);
                                fab.setText("Unfollow");
                                fab.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        removeFromFollowers();

                                    }
                                });
                            }
                            else if(user_email.equals(profile_email)){
                                fab.setVisibility(View.INVISIBLE);
                            }
                            else{
                                //fab.setImageResource(android.R.drawable.ic_input_add);
                                //fab.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.bright_foreground_holo_dark));
                                fab.setVisibility(View.VISIBLE);
                                fab.setText("Follow");
                                fab.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        addToFollowers();
                                    }
                                });
                            }
                        }catch (JSONException e2){
                            Log.i("TG", e2.toString());
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
                params.put("user_email", user_email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(StrProfileReq);

    }

    private void addToFollowers() {
        String url = DBConnect.serverURL + "/add_to_following";

        //Create the exercise history request
        StringRequest StrFollowReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Following "+profile_email)){
                            // other notification system may be used in the future
                            Toast.makeText(getApplicationContext(), response,
                                    Toast.LENGTH_SHORT).show();
                            createFollowerInfo();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Couldn't add user",
                                    Toast.LENGTH_SHORT).show();
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
        ) {
            // use params are specified here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("follower_email", user_email);
                params.put("following_email", profile_email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(StrFollowReq);
    }

    private void removeFromFollowers(){
        String url = DBConnect.serverURL + "/rm_from_following";

        //Create the exercise history request
        StringRequest StrUnfollowReq = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(response.equals("Unfollowed "+ profile_email)){
                            // other notification system may be used in the future
                            Toast.makeText(getApplicationContext(), response,
                                    Toast.LENGTH_SHORT).show();
                            createFollowerInfo();
                        }
                        else{
                            Toast.makeText(getApplicationContext(), "Couldn't remove user",
                                    Toast.LENGTH_SHORT).show();
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
        ) {
            // use params are specified here
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("unfollower", user_email);
                params.put("unfollowed", profile_email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(StrUnfollowReq);

    }

    public void onClickFollowing(View view){
        // Adapter and whatnot
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle(name+" is following");

        builder.setAdapter(followingAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected_profile_email = followingAdapter.getItem(which);
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                intent.putExtra("user_email", user_email);
                intent.putExtra("profile_email", selected_profile_email);
                startActivity(intent);
            }
        });
        builder.show();
    }

    public void onClickFollowers(View view){
        // Adapter and whatnot
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);

        builder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.setTitle(name+" followers");

        builder.setAdapter(followersAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selected_profile_email = followersAdapter.getItem(which);
                Intent intent = new Intent(ProfileActivity.this, ProfileActivity.class);
                intent.putExtra("user_email", user_email);
                intent.putExtra("profile_email", selected_profile_email);
                startActivity(intent);
            }
        });
        builder.show();
    }

    // Navigation functions
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home, menu);
        MenuItem menuItem = menu.findItem(R.id.nav_home);

        if (menuItem != null) {
            tintMenuIcon(ProfileActivity.this, menuItem, android.R.color.white);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.nav_home:
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                intent.putExtra("email", user_email);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();

            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public static void tintMenuIcon(Context context, MenuItem item, @ColorRes int color) {
        Drawable normalDrawable = item.getIcon();
        Drawable wrapDrawable = DrawableCompat.wrap(normalDrawable);
        DrawableCompat.setTint(wrapDrawable, context.getResources().getColor(color));

        item.setIcon(wrapDrawable);
    }

    public void onClickPickImage(View view) {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult( pickPhotoIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            //Bitmap user_img = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            //ImageView profile_pic = ((ImageView) findViewById(R.id.pick_profile_img));
            //profile_pic.setScaleType(ImageView.ScaleType.FIT_XY);
            //profile_pic.setImageBitmap(user_img);

            Log.i("TG","getting uri");

            Uri uri = data.getData();

            try {

                Bitmap user_img = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Bitmap resized = Bitmap.createScaledBitmap(user_img, 100, 100, true);
                profile_image.setImageBitmap(resized);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte [] byte_arr = stream.toByteArray();
                base64profile_pic = Base64.encodeToString(byte_arr, Base64.DEFAULT);
                Map<String, String>  params = new HashMap<>();
                params.put("profile_pic", base64profile_pic);
                updateUser("/update_user_profile_pic", params);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected void setUserProfileSetup(){

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickPickImage(v);
            }
        });

        height_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText edittext = new EditText(ProfileActivity.this);
                edittext.setInputType( InputType.TYPE_CLASS_NUMBER);
                edittext.setFilters(new InputFilter[] {new InputFilter.LengthFilter(3)});
                edittext.setGravity(Gravity.CENTER);
                AlertDialog.Builder height_dialog  = new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Set height")
                        .setMessage("Height (cm)")
                        .setView(edittext)
                        .setPositiveButton("Set", null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });

                final AlertDialog heightDialog =  height_dialog.create();
                heightDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = heightDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                //What ever you want to do with the value
                                String newHeight = edittext.getText() + "";
                                if(validHeight(newHeight)) {
                                    height_text.setText(edittext.getText() + " cm");
                                    heightDialog.dismiss();
                                    Map<String, String>  params = new HashMap<>();
                                    params.put("height", newHeight);
                                    updateUser("/update_user_height",params);
                                }
                                else
                                    edittext.setError("Insert valid height");

                            }
                        });
                    }
                });
                heightDialog.show();


            }
        });
        weight_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final EditText edittext = new EditText(ProfileActivity.this);
                edittext.setInputType( InputType.TYPE_NUMBER_FLAG_DECIMAL);
                edittext.setFilters(new InputFilter[] {new InputFilter.LengthFilter(6)});
                edittext.setGravity(Gravity.CENTER);
                edittext.setKeyListener(new CustomDigitsKeyListener(true,true));
                edittext.addTextChangedListener(new TextWatcher() {
                    int len = 0;

                    @Override
                    public void afterTextChanged(Editable s) {

                        String weight_in = edittext.getText().toString();
                        if (s.length() >= 3 && len < s.length() && !weight_in.contains("."))
                            s.insert(3, ".");

                    }

                    @Override
                    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                        String str = edittext.getText().toString();
                        len = str.length();
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                    }
                });

                AlertDialog.Builder weight_dialog = new AlertDialog.Builder(ProfileActivity.this)
                        .setTitle("Set weight")
                        .setMessage("Weight (Kg)")
                        .setView(edittext)
                        .setPositiveButton("Set",null)
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                            }
                        });

                final AlertDialog weightDialog =  weight_dialog.create();
                weightDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {

                        Button b = weightDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        b.setOnClickListener(new View.OnClickListener() {

                            @Override
                            public void onClick(View view) {
                                //What ever you want to do with the value
                                String newWeight = edittext.getText() + "";
                                if(validWeight(newWeight)) {
                                    String displayedWeight = Double.toString(Double.parseDouble(newWeight));
                                    weight_text.setText(displayedWeight + " Kg");
                                    weightDialog.dismiss();
                                    Map<String, String>  params = new HashMap<>();
                                    params.put("weight", newWeight);
                                    updateUser("/update_user_weight", params);
                                }
                                else
                                    edittext.setError("Insert valid weight");

                            }
                        });
                    }
                });
                weightDialog.show();
            }
        });
    }

    private boolean validWeight(String weight){
        if(!TextUtils.isEmpty(weight)) {
            try {
                Double.parseDouble(weight);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
        return false;
    }
    private boolean validHeight(String height){
        if(!TextUtils.isEmpty(height) && Integer.parseInt(height) <= 400){
                return true;
        }
        return false;

    }

    private class CustomDigitsKeyListener extends DigitsKeyListener
    {
        public CustomDigitsKeyListener() {
            super(false, false);
        }

        public CustomDigitsKeyListener(boolean sign, boolean decimal) {
            super(sign, decimal);
        }

        public int getInputType() {
            return InputType.TYPE_CLASS_PHONE;
        }
    }

    private void updateUser(String reqUrl, final Map<String,String> params){

        StringRequest saveRequest = new StringRequest(Request.Method.POST, baseUrl + reqUrl,
                new Response.Listener<String>() {
                    public void onResponse(String response){
                        String updated_param = "";
                        for(String param : params.keySet())
                            if(!param.equals(user_email)) {
                                updated_param = param;
                            }

                        Log.i("HELP","User " + user_email + " "+updated_param+" updated");
                        Log.i("HELP","User ola@ua.pt height updated");

                        if(response.equals("User " + user_email + " "+updated_param+" updated")) {

                        }

                        else{

                            android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this).create();
                            alertDialog.setTitle("Error");
                            // Please connect your device to the Internet and try again
                            alertDialog.setMessage(response.toString());
                            alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        android.support.v7.app.AlertDialog alertDialog = new android.support.v7.app.AlertDialog.Builder(ProfileActivity.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        // Please connect your device to the Internet and try again
                        alertDialog.setMessage(error.toString());
                        alertDialog.setButton(android.support.v7.app.AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                }
        ){
            // user params are specified here
            @Override
            protected Map<String, String> getParams()
            {
                params.put("email",user_email);
                return params;
            }
        };

        VolleyProvider.getInstance(this).addRequest(saveRequest);

    }

    private void RadarSetup(){
        rChart.setBackgroundColor(Color.rgb(48,48,48));//@color/holo_primary

        rChart.getDescription().setEnabled(false);

        rChart.setWebLineWidth(1f);
        rChart.setWebColor(Color.LTGRAY);
        rChart.setWebLineWidthInner(1f);
        rChart.setWebColorInner(Color.LTGRAY);
        rChart.setWebAlpha(100);

        rChart.setRotationEnabled(false);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new RadarMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(rChart); // For bounds control
        rChart.setMarker(mv); // Set the marker to the chart

        String url = DBConnect.serverURL + "/get_exercise_muscle_stats_of_user";

        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            // Chart Array Values List
                            ArrayList<RadarEntry> entries = new ArrayList<>();
                            final String[] MuscleGroups = new String[jsonArray.length()];
                            Float[] MuscleCnt = new Float[jsonArray.length()];
                            Float sum = 0f;
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    MuscleGroups[i] = new JSONObject(jsonArray.getString(i)).getString("Muscle_name");
                                    MuscleCnt[i] = Float.parseFloat(new JSONObject(jsonArray.getString(i)).getString("count"));
                                    sum += MuscleCnt[i];
                                }
                            } catch (JSONException je) {
                                Log.e("TG", je.toString());
                            }

                            Float max = 0f;
                            // normalize values
                            for(int i = 0; i < MuscleCnt.length;i++){
                                Float tmp = MuscleCnt[i]/sum;
                                MuscleCnt[i] = tmp*100;
                                if(MuscleCnt[i] > max)
                                    max = MuscleCnt[i];
                            }
                            for (Float x : MuscleCnt) {
                                Float tmp = (x/(max+10))*100;
                                /*if (max < 60){
                                    tmp = ((x/(max+10))*100);
                                }
                                else if ( max < 70){
                                    tmp = ((x/(max+12f))*100);
                                }
                                else if (max < 80){
                                    tmp = ((x/(max+14f))*100);
                                }*/
                                entries.add(new RadarEntry(tmp));
                            }
                            // Graph is explicit i think
                            RadarDataSet set1 = new RadarDataSet(entries,"% Worked Group Muscles");
                            set1.setColor(Color.rgb(121, 162, 175));
                            set1.setFillColor(Color.rgb(121, 162, 175));
                            set1.setDrawFilled(true);
                            set1.setFillAlpha(180);
                            set1.setLineWidth(2f);
                            set1.setDrawHighlightCircleEnabled(true);
                            set1.setDrawHighlightIndicators(false);
                            set1.setLabel("");

                            ArrayList<IRadarDataSet> sets = new ArrayList<>();
                            sets.add(set1);

                            RadarData data = new RadarData(sets);
                            data.setValueTypeface(mTfLight);
                            data.setValueTextSize(8f);
                            data.setDrawValues(false);
                            data.setValueTextColor(Color.WHITE);

                            rChart.setData(data);
                            rChart.invalidate();
                            rChart.setExtraLeftOffset(15);
                            rChart.setExtraRightOffset(15);
                            rChart.setExtraTopOffset(15);

                            rChart.animateXY(
                                    1400, 1400,
                                    Easing.EasingOption.EaseInOutQuad,
                                    Easing.EasingOption.EaseInOutQuad);

                            XAxis xAxis = rChart.getXAxis();
                            xAxis.setTypeface(mTfLight);
                            xAxis.setTextSize(9f);
                            xAxis.setYOffset(0f);
                            xAxis.setXOffset(0f);
                            xAxis.setValueFormatter(new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    return MuscleGroups[(int) value % MuscleGroups.length];
                                }
                            });
                            xAxis.setTextColor(Color.WHITE);

                            YAxis yAxis = rChart.getYAxis();
                            yAxis.setTypeface(mTfLight);
                            yAxis.setLabelCount(5, false);
                            yAxis.setTextSize(9f);
                            yAxis.setAxisMinimum(0f);
                            yAxis.setAxisMaximum(80f);
                            yAxis.setDrawLabels(false);

                            Legend l = rChart.getLegend();
                            l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
                            l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
                            l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
                            l.setDrawInside(false);
                            l.setTypeface(mTfLight);
                            l.setXEntrySpace(7f);
                            l.setYEntrySpace(5f);
                            l.setTextColor(Color.WHITE);
                            l.setEnabled(false);
                            rChart.setVisibility(View.VISIBLE);
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
                params.put("User_email", user_email);
                return params;
            }
        };

        //Queue the request
        VolleyProvider.getInstance(this).addRequest(StrHistReq);
    }

    private void LineSetup(){
        // no description text
        lChart.getDescription().setEnabled(false);

        // enable touch gestures
        lChart.setTouchEnabled(true);

        lChart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        lChart.setDragEnabled(true);
        lChart.setScaleEnabled(true);
        lChart.setDrawGridBackground(false);
        lChart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        lChart.setBackgroundColor(Color.rgb(48,48,48));
        lChart.setViewPortOffsets(0f, 0f, 0f, 0f);

        // create a custom MarkerView (extend MarkerView) and specify the layout
        // to use for it
        MarkerView mv = new LineMarkerView(this, R.layout.custom_marker_view);
        mv.setChartView(lChart); // For bounds control
        lChart.setMarker(mv); // Set the marker to the chart

        String url = DBConnect.serverURL + "/get_weight_history";
        //Create the exercise history request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Chart Array Values List
                            ArrayList<Entry> values = new ArrayList<>();
                            JSONArray jsonArray = new JSONArray(response);
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmpWeight = new JSONObject(jsonArray.getString(i)).getString("Weight");
                                    String tmpDate = new JSONObject(jsonArray.getString(i)).getString("Date");
                                    // parse String to Date - I don't know why but this adds an hour to the date
                                    Date tmpDat = new SimpleDateFormat("EE, dd MMM yyyy HH:mm:ss z", Locale.UK).parse(tmpDate);
                                    values.add(new Entry(tmpDat.getTime(),Float.parseFloat(tmpWeight)));
                                }
                            } catch (JSONException|ParseException jpe) {
                                Log.e("WeightChartSetup", jpe.toString());
                            }
                            // create a dataset and give it a type
                            LineDataSet set1 = new LineDataSet(values, "Weight");
                            set1.setAxisDependency(YAxis.AxisDependency.LEFT);
                            set1.setColor(ColorTemplate.getHoloBlue());
                            set1.setValueTextColor(ColorTemplate.getHoloBlue());
                            set1.setLineWidth(1.5f);
                            set1.setDrawCircles(true);
                            set1.setDrawValues(false);
                            set1.setFillAlpha(65);
                            set1.setFillColor(ColorTemplate.getHoloBlue());
                            set1.setHighLightColor(Color.rgb(244, 117, 117));
                            set1.setDrawCircleHole(false);

                            // create a data object with the datasets
                            LineData data = new LineData(set1);
                            data.setValueTextColor(Color.WHITE);
                            data.setValueTextSize(9f);

                            // set data
                            lChart.setData(data);

                            lChart.invalidate();

                            // get the legend (only possible after setting data)
                            Legend l = lChart.getLegend();
                            l.setEnabled(false);

                            XAxis xAxis = lChart.getXAxis();
                            xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
                            xAxis.setTypeface(mTfLight);
                            xAxis.setTextSize(10f);
                            xAxis.setTextColor(Color.WHITE);
                            xAxis.setDrawAxisLine(false);
                            xAxis.setDrawGridLines(true);
                            xAxis.setTextColor(Color.rgb(255, 192, 56));
                            xAxis.setCenterAxisLabels(true);
                            xAxis.setGranularity(1f); // one hour
                            xAxis.setValueFormatter(new IAxisValueFormatter() {

                                private SimpleDateFormat mFormat = new SimpleDateFormat("EE, dd MMM",Locale.UK);

                                @Override
                                public String getFormattedValue(float value,  AxisBase axis) {

                                    long millis = TimeUnit.HOURS.toMillis((long) value);
                                    return mFormat.format(new Date(millis));
                                }
                            });

                            YAxis leftAxis = lChart.getAxisLeft();
                            leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
                            leftAxis.setTypeface(mTfLight);
                            leftAxis.setTextColor(ColorTemplate.getHoloBlue());
                            leftAxis.setDrawGridLines(true);
                            leftAxis.setGranularityEnabled(true);

                            leftAxis.setAxisMinimum(30f); // min max values
                            leftAxis.setAxisMaximum(120f);

                            leftAxis.setYOffset(-9f);
                            leftAxis.setTextColor(Color.rgb(255, 192, 56));

                            YAxis rightAxis = lChart.getAxisRight();
                            rightAxis.setEnabled(false);

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
                params.put("user_email", user_email);
                return params;
            }
        };

        //Queue the request
        VolleyProvider.getInstance(this).addRequest(StrHistReq);
    }
}
