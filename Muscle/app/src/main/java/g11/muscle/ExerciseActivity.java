package g11.muscle;

import android.net.Uri;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

//Used to conver url strings to valid strings
import java.util.HashMap;
import java.util.Map;

import g11.muscle.Classes.BounceInterpolator;
import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;
import g11.muscle.Fragments.ExerciseFragment;
import g11.muscle.Fragments.ExerciseMusclesFragment;
import g11.muscle.Fragments.ExercisePagerAdapter;
import g11.muscle.Fragments.PagerAdapter;

public class ExerciseActivity extends AppCompatActivity implements ExerciseFragment.OnFragmentInteractionListener, ExerciseMusclesFragment.OnFragmentInteractionListener{

    private static final String TAG = "ExerciseActivity";

    //Exercise name
    private String exercise;
    //User email
    private String email;

    private String rest;
    private int reps,sets,weight;

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty - Used for interaction between fragments
    }

    // Defines several constants used when transmitting messages between the
    // service and the UI.
    @Override
    protected void onCreate(final Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exercise);

        final Intent intent = getIntent();
        exercise = intent.getStringExtra("exercise_name");
        email = intent.getStringExtra("email");

        setTitle(exercise);
        getSupportActionBar().setSubtitle("Strenght");

        if(intent.getStringExtra("exercise_rest") != null) {
            rest = intent.getStringExtra("exercise_rest");
            reps = intent.getIntExtra("exercise_reps",0);
            sets = intent.getIntExtra("exercise_sets",0);
            weight = intent.getIntExtra("exercise_weight",0);
        }else
            rest = null;

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Exercise"));
        tabLayout.addTab(tabLayout.newTab().setText("Muscle Groups"));

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        final ExercisePagerAdapter adapter = new ExercisePagerAdapter
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


    public void onClickStartButton(View view){
        Intent intent = new Intent(ExerciseActivity.this, FeedBackActivity.class);
        intent.putExtra("email", email);
        intent.putExtra("exercise_name", exercise);
        if(rest != null)
        {
            intent.putExtra("exercise_rest",rest);
            intent.putExtra("exercise_reps",reps);
            intent.putExtra("exercise_sets",sets);
            intent.putExtra("exercise_weight",weight);
        }
        startActivity(intent);
    }
}