package g11.muscle;

import android.content.DialogInterface;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;

import android.widget.TextView;

import android.support.v4.app.FragmentManager;

import android.net.Uri;

public class HomeActivity extends AppCompatActivity implements PickExerciseFragment.OnFragmentInteractionListener,
        ExerciseHistoryFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener
        ,FeedFragment.OnFragmentInteractionListener,MyPlanFragment.OnFragmentInteractionListener{

    private VolleyProvider req_queue;
    private FragmentManager manager;
    private Fragment homeFragment;
    private Fragment myPlanFragment;
    private Fragment yourFeedFragment;
    private Fragment historyFragment;
    private Fragment exerciseListFragment;



    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    setTitle("Home");
                    switchToFragmentHome();
                    return true;
                case R.id.navigation_myPlan:
                    setTitle("My Plan");
                    switchToFragmentMyPlan();
                    return true;
                case R.id.navigation_exerciseHistory:
                    setTitle("History");
                    switchToFragmentExerciseHistory();
                    return true;
                case R.id.navigation_Feed:
                    setTitle("Your Feed");
                    switchToFragmentFeed();
                    return true;
                case R.id.navigation_exerciseList:
                    setTitle("Exercise List");
                    switchToFragmentPickExercise();
                    return true;
            }
            return false;
        }

    };

    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty - Used for interaction between fragments
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("Home");
        setContentView(R.layout.activity_home);
        manager = getSupportFragmentManager();
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // Start in Home Fragment
        switchToFragmentHome();
    }

    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Quit Muscle")
                .setMessage("Are you sure you want to close Muscle?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    // Method used to switch to Exercise History Fragment
    public void switchToFragmentHome() {
        if(manager.findFragmentByTag("Home") == null)
            manager.beginTransaction().replace(R.id.content, new HomeFragment(), "Home").commit();
    }

    // Method used to switch to PickExercise Fragment
    public void switchToFragmentPickExercise() {
        if(manager.findFragmentByTag("PickExercise") == null)
            manager.beginTransaction().replace(R.id.content, new PickExerciseFragment(), "PickExercise").commit();
    }

    // Method used to switch to Exercise History Fragment
    public void switchToFragmentMyPlan() {
        if(manager.findFragmentByTag("MyPlan") == null)
            manager.beginTransaction().replace(R.id.content, new MyPlanFragment(), "MyPlan").commit();
    }

    // Method used to switch to Feed Fragment
    public void switchToFragmentFeed() {
        if(manager.findFragmentByTag("Feed") == null)
            manager.beginTransaction().replace(R.id.content, new FeedFragment(), "Feed").commit();
    }

    // Method used to switch to Exercise History Fragment
    public void switchToFragmentExerciseHistory() {
        if(manager.findFragmentByTag("ExerciseHistory") == null)
            manager.beginTransaction().replace(R.id.content, new ExerciseHistoryFragment(), "ExerciseHistory").commit();
    }
}
