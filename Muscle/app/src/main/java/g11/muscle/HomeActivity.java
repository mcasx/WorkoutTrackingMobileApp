package g11.muscle;

import android.content.DialogInterface;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;

import android.widget.TextView;

import android.support.v4.app.FragmentManager;

import android.net.Uri;

public class HomeActivity extends AppCompatActivity implements PickExerciseFragment.OnFragmentInteractionListener,
        ExerciseHistoryFragment.OnFragmentInteractionListener, HomeFragment.OnFragmentInteractionListener
        ,ProfileFragment.OnFragmentInteractionListener,MyPlanFragment.OnFragmentInteractionListener{

    private VolleyProvider req_queue;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    switchToFragmentHome();
                    return true;
                case R.id.navigation_exerciseList:
                    switchToFragmentPickExercise();
                    return true;
                case R.id.navigation_exerciseHistory:
                    switchToFragmentExerciseHistory();
                    return true;
                case R.id.navigation_myPlan:
                    switchToFragmentMyPlan();
                    return true;
                case R.id.navigation_myProfile:
                    switchToFragmentProfile();
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
        setContentView(R.layout.activity_home);

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

    // Method used to switch to PickExercise Fragment
    public void switchToFragmentPickExercise() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content, new PickExerciseFragment()).commit();
    }

    // Method used to switch to Exercise History Fragment
    public void switchToFragmentExerciseHistory() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content, new ExerciseHistoryFragment()).commit();
    }

    // Method used to switch to Exercise History Fragment
    public void switchToFragmentHome() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content, new HomeFragment()).commit();
    }

    // Method used to switch to Exercise History Fragment
    public void switchToFragmentMyPlan() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content, new MyPlanFragment()).commit();
    }

    // Method used to switch to Exercise History Fragment
    public void switchToFragmentProfile() {
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().replace(R.id.content, new ProfileFragment()).commit();
    }
}
