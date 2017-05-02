package g11.muscle;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.ColorRes;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private VolleyProvider req_queue;
    private String user_email;
    private String profile_email;
    private String name;
    private ArrayAdapter<String> followingAdapter;
    private ArrayAdapter<String> followersAdapter;
    private boolean friendFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user_email = getIntent().getStringExtra("user_email");
        profile_email = getIntent().getStringExtra("profile_email");

        req_queue = VolleyProvider.getInstance(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        followersAdapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_list_item_1);
        followingAdapter = new ArrayAdapter<>(ProfileActivity.this, android.R.layout.simple_list_item_1);

        createUserInfo();
        createFollowerInfo();

    }

    private void createUserInfo() {
        String url = "https://138.68.158.127/get_user_profile";

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
        String url = "https://138.68.158.127/get_follow_info";

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

                            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
                            if(friendFlag){
                                Log.i("TG", "friends :D ");
                                fab.setImageResource(R.drawable.ic_remove_black_24dp);
                                fab.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.colorPrimary));
                                fab.setVisibility(View.VISIBLE);
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
                                fab.setImageResource(android.R.drawable.ic_input_add);
                                fab.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.bright_foreground_holo_dark));
                                fab.setVisibility(View.VISIBLE);
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
                params.put("user_email", profile_email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        req_queue.addRequest(StrProfileReq);

    }

    private void addToFollowers() {
        String url = "https://138.68.158.127/add_to_following";

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
        String url = "https://138.68.158.127/rm_from_following";

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
                this.finish();
                return true;
            case R.id.nav_home:
                Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
                intent.putExtra("email", user_email);
                startActivity(intent);

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

}
