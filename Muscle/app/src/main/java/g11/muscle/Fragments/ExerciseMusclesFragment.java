package g11.muscle.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import g11.muscle.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ExerciseMusclesFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class ExerciseMusclesFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private LayoutInflater inflater;
    private View fView;

    private String email, exercise;

    //GUI
    private TextView nameTV;

    public ExerciseMusclesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.inflater = inflater;
        fView = inflater.inflate(R.layout.fragment_exercise_muscles, container, false);

        // Information from previous activity
        final Intent intent = getActivity().getIntent();

        exercise = intent.getStringExtra("exercise_name");
        email = intent.getStringExtra("email");

        nameTV = (TextView) fView.findViewById(R.id.Ex_Name);
        nameTV.setText(exercise);

        // Inflate the layout for this fragment
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
        void onFragmentInteraction(Uri uri);
    }
}
