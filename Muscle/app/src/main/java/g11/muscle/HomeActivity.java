package g11.muscle;

import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;

import android.view.MenuItem;

import android.widget.TextView;

import android.support.v4.app.FragmentManager;

import android.net.Uri;

public class HomeActivity extends AppCompatActivity implements PickExerciseFragment.OnFragmentInteractionListener,
        ExerciseHistoryFragment.OnFragmentInteractionListener{

    private TextView mTextMessage;
    private static String email;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    mTextMessage.setText(R.string.title_home);
                    return true;
                case R.id.navigation_exerciseList:
                    mTextMessage.setText(R.string.title_exerciseList);
                    switchToFragmentPickExercise();
                    return true;
                case R.id.navigation_exerciseHistory:
                    mTextMessage.setText(R.string.title_exerciseList);
                    switchToFragmentExerciseHistory();
                    return true;
                case R.id.navigation_myPlan:
                    mTextMessage.setText(R.string.title_myPlan);
                    return true;
                case R.id.navigation_myProfile:
                    mTextMessage.setText(R.string.title_myProfile);
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

        email = getIntent().getStringExtra("email");

        mTextMessage = (TextView) findViewById(R.id.message);
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
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
}
