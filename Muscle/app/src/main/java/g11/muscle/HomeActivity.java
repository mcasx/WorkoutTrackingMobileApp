package g11.muscle;

import android.content.DialogInterface;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.util.Log;
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
        Fragment fragment = manager.findFragmentByTag("Home");
        FragmentTransaction ft = manager.beginTransaction();

        if(manager.findFragmentByTag("Home") == null) {
            ft.replace(R.id.content, new HomeFragment(), "Home");
            ft.addToBackStack("Home");
            ft.commit();
            manager.executePendingTransactions();
            Log.e("Home","Selected Home Tag");
        }
        else
            manager.beginTransaction().replace(R.id.content, fragment, "Home").commit();

        getSupportFragmentManager().executePendingTransactions();
    }

    // Method used to switch to PickExercise Fragment
    public void switchToFragmentPickExercise() {
        Fragment fragment = manager.findFragmentByTag("PickExercise");
        FragmentTransaction ft = manager.beginTransaction();

        if(fragment == null) {
            ft.replace(R.id.content, new PickExerciseFragment(), "PickExercise");
            ft.addToBackStack("PickExercise");
            ft.commit();
            manager.executePendingTransactions();
            Log.e("PickExercise","Selected Pick Exercise Tab");
        }
        else
            manager.beginTransaction().replace(R.id.content, fragment, "PickExercise").commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    // Method used to switch to Exercise History Fragment
    public void switchToFragmentMyPlan() {

        Fragment fragment = manager.findFragmentByTag("MyPlan");
        FragmentTransaction ft = manager.beginTransaction();

        if(fragment == null) {
            ft.replace(R.id.content, new MyPlanFragment(), "MyPlan");
            ft.addToBackStack("MyPlan");
            ft.commit();
            manager.executePendingTransactions();
            Log.e("MyPlan","Selected MyPlan Tab");
        }
        else
            manager.beginTransaction().replace(R.id.content, fragment, "MyPlan").commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    // Method used to switch to Feed Fragment
    public void switchToFragmentFeed() {

        Fragment fragment = manager.findFragmentByTag("Feed");
        FragmentTransaction ft = manager.beginTransaction();

        if(fragment == null) {
            ft.replace(R.id.content, new FeedFragment(), "Feed");
            ft.addToBackStack("Feed");
            ft.commit();
            manager.executePendingTransactions();
            Log.e("Feed","Selected Feed Exercise Tab");
        }
        else
            manager.beginTransaction().replace(R.id.content, fragment, "Feed").commit();
        getSupportFragmentManager().executePendingTransactions();
    }

    // Method used to switch to Exercise History Fragment
    public void switchToFragmentExerciseHistory() {
        if(manager.findFragmentByTag("ExerciseHistory") == null)
            manager.beginTransaction().replace(R.id.content, new ExerciseHistoryFragment(), "ExerciseHistory").commit();

        Fragment fragment = manager.findFragmentByTag("ExerciseHistory");
        FragmentTransaction ft = manager.beginTransaction();

        if(fragment == null) {
            ft.replace(R.id.content, new ExerciseHistoryFragment(), "ExerciseHistory");
            ft.addToBackStack("ExerciseHistory");
            ft.commit();
            manager.executePendingTransactions();
            Log.e("ExerciseHistory", "Selected ExerciseHistory Tab");
        }
        else
            manager.beginTransaction().replace(R.id.content, fragment, "ExerciseHistory").commit();
        getSupportFragmentManager().executePendingTransactions();
    }
}
