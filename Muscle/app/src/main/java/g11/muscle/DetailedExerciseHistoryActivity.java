package g11.muscle;

import android.content.DialogInterface;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
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
import g11.muscle.DB.VolleyProvider;
import g11.muscle.Fragments.PagerAdapter;

public class DetailedExerciseHistoryActivity extends AppCompatActivity {



    public static JSONObject exerciseHistoryItem = null;
    private final String TAG = "DetailedExerciseHistory";
    private VolleyProvider req_queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_exercise_history);
        req_queue = VolleyProvider.getInstance(this);

        Log.e(TAG, "SHADSHKASDNSA\n\n\nasdjajosnalnda" + getSharedPreferences("UserData", 0).getString("email", null));


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Exercise"));
        tabLayout.addTab(tabLayout.newTab().setText("Stats"));
        tabLayout.addTab(tabLayout.newTab().setText("Social"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        try {
            setTitle(exerciseHistoryItem.getString("Exercise_name"));
            getSupportActionBar().setSubtitle(exerciseHistoryItem.getString("User_email"));
        }catch (JSONException je) {
            Log.e(TAG, je.toString());
        }


        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final PagerAdapter adapter = new PagerAdapter
                (getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.detailed_exercise_menu, menu);
        if(hasThumbsUp()){

            (menu.findItem(R.id.thumbsUp)).setIcon(R.drawable.ic_thumb_up_full_white_24dp);
        }

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.thumbsUp:
                // User chose the "Settings" item, show the app settings UI...
                thumbsUp();
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
                                new AlertDialog.Builder(DetailedExerciseHistoryActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
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
                                new AlertDialog.Builder(DetailedExerciseHistoryActivity.this)
                                        .setIcon(android.R.drawable.ic_dialog_alert)
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

    private void thumbsUp(){

    }

    private boolean hasThumbsUp(){
        return true;
    }


}
