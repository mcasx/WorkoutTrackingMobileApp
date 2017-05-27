package g11.muscle.Fragments;

/**
 * Created by david on 30-04-2017.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import g11.muscle.Fragments.DetailedExerciseHistoryComments;
import g11.muscle.Fragments.DetailedExerciseHistoryFragment;
import g11.muscle.Fragments.DetailedExerciseHistoryGraphs;

public class PlanPagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PlanPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                MyPlanFragment tab1 = new MyPlanFragment();
                return tab1;
            case 1:
                PlanListFragment tab2 = new PlanListFragment();
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