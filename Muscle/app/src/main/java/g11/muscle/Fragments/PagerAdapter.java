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

public class PagerAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;

    public PagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                DetailedExerciseHistoryFragment tab1 = new DetailedExerciseHistoryFragment();
                return tab1;
            case 1:
                DetailedExerciseHistoryGraphs tab2 = new DetailedExerciseHistoryGraphs();
                return tab2;
            case 2:
                DetailedExerciseHistoryComments tab3 = new DetailedExerciseHistoryComments();
                return tab3;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}