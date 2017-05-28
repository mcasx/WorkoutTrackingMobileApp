package g11.muscle.Fragments;

/**
 * Created by xarez on 28-05-2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class ExercisePagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public ExercisePagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                ExerciseFragment tab1 = new ExerciseFragment();
                return tab1;
            case 1:
                ExerciseMusclesFragment tab2 = new ExerciseMusclesFragment();
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}
