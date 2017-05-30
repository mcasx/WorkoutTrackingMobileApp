package g11.muscle.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import g11.muscle.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link PlanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class PlanFragment extends Fragment{

    private OnFragmentInteractionListener mListener;
    private FragmentManager manager;
    private int buttonHeight;

    public PlanFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        manager = getChildFragmentManager();
        View fView = inflater.inflate(R.layout.fragment_plan, container, false);
        TabLayout tabLayout = (TabLayout) fView.findViewById(R.id.plan_tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("My Plan"));
        tabLayout.addTab(tabLayout.newTab().setText("Plans"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        // Inflate the layout for this fragment
        final ViewPager viewPager = (ViewPager) fView.findViewById(R.id.plan_pager);

        BottomNavigationView  bottom = (BottomNavigationView) container.findViewById(R.id.navigation);
        if (bottom != null) {
            buttonHeight = bottom.getHeight();
        }
        else
            buttonHeight = 120;

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        lp.setMargins(0, 0, 0, buttonHeight);
        viewPager.setLayoutParams(lp);


        final PlanPagerAdapter adapter = new PlanPagerAdapter
                (manager, tabLayout.getTabCount());
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
        return fView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void switchToFragmentPlanList() {
        Fragment fragment = manager.findFragmentByTag("PlanList");
        FragmentTransaction ft = manager.beginTransaction();

        if(fragment == null) {
            ft.replace(R.id.content, new MyPlanFragment(), "PlanList");
            ft.addToBackStack("PlanList");
            ft.commit();
            manager.executePendingTransactions();
            Log.e("PlanList","Selected PlanList Tab");
        }
        else
            manager.beginTransaction().replace(R.id.content, fragment, "PlanList").commit();
        manager.executePendingTransactions();
    }

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
        manager.executePendingTransactions();
    }
}
