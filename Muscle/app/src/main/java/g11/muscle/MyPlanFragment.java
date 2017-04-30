package g11.muscle;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ArrayAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.TextView;
import android.widget.Spinner;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MyPlanFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MyPlanFragment# newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyPlanFragment extends Fragment {
    private OnFragmentInteractionListener mListener;

    private static final String TAG = "MyPlanFragment";

    private String email;

    // Initialization of plan_group array
    private List<TrainingsItem> plan_trainings;
    private List<PlanExerciseItem> training_data;

    //GUI
    private RecyclerView recyclerView;
    private Spinner dropdown;

    public MyPlanFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        plan_trainings = new ArrayList<>();
        training_data = new ArrayList<>();

        // get user email
        email = getActivity().getIntent().getStringExtra("email");

        //getTrainings();
        // Training data TODO static data - TO REMOVE
        //training_data = fill_with_data();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Fragment View
        View fView = inflater.inflate(R.layout.fragment_my_plan, container, false);

        //GUI elements
        dropdown = (Spinner)fView.findViewById(R.id.spinner);
        getTrainings();

        recyclerView = (RecyclerView) fView.findViewById(R.id.PE_recyclerview);

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

    // Get User Plan Trainings ( ID + Name )
    private void getTrainings(){
        String url = "https://138.68.158.127/get_user_plan_trainings";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmp_name = new JSONObject(jsonArray.getString(i)).getString("Name");
                                    int tmp_id = Integer.parseInt(new JSONObject(jsonArray.getString(i)).getString("ID"));
                                    plan_trainings.add(new TrainingsItem(tmp_id,tmp_name));
                                }
                            } catch (JSONException je){
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        String[] items = new String[plan_trainings.size()];
                        for(int i = 0; i < plan_trainings.size();i++){
                            items[i] = "Day " + String.valueOf(i+1) + " - " + plan_trainings.get(i).getName();
                        }
                        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, items);
                        dropdown.setAdapter(adapter1);
                        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                                getTrainingExercises(plan_trainings.get(pos).getIdStr());
                            }
                            public void onNothingSelected(AdapterView<?> parent) {
                            }
                        });
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        System.out.println(error.toString());
                    }
                }
        ){
            // use params are specified here
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("email", email);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }

    private void getTrainingExercises(final String training) {
        String url = "https://138.68.158.127/get_training_exercises";

        //Create the exercise plan_group request
        StringRequest StrHistReq = new StringRequest(Request.Method.POST,url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            training_data.clear(); // clear data - when other training selected
                            try {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    String tmp_name = new JSONObject(jsonArray.getString(i)).getString("Exercise_name");
                                    int tmp_reps = Integer.parseInt(new JSONObject(jsonArray.getString(i)).getString("Repetitions"));
                                    int tmp_sets = Integer.parseInt(new JSONObject(jsonArray.getString(i)).getString("Sets"));
                                    String tmp_rest = new JSONObject(jsonArray.getString(i)).getString("Resting_Time");
                                    // TODO NEEDS IMAGE
                                    training_data.add(new PlanExerciseItem(tmp_name,tmp_reps,tmp_sets,tmp_rest,R.mipmap.default_avatar));
                                }
                            } catch (JSONException je){
                                Log.e(TAG, je.toString());
                            }
                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                        Plan_Exercise_View_Adapter adapter = new Plan_Exercise_View_Adapter(training_data);
                        recyclerView.setAdapter(adapter);
                        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //Handle error response
                        System.out.println(error.toString());
                    }
                }
        ){
            // use params are specified here
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("training",training);
                return params;
            }
        };

        // Add the request to the RequestQueue
        VolleyProvider.getInstance(getActivity()).addRequest(StrHistReq);
    }

    //
    private class TrainingsItem {
        private int id;
        private String name;

        private TrainingsItem (int id, String name){
            this.id = id;
            this.name = name;
        }

        private String getIdStr(){
            return String.valueOf(id);
        }

        private String getName() {
            return name;
        }

        @Override
        public String toString(){
            return "\n#############\nID: " + id + "\nName: " + name;
        }
    }

    // It defined a Exercise ( Used in training exercises list view )
    private class PlanExerciseItem {
        private String exercise_name;
        private int exercise_reps;
        private int exercise_sets;
        private String exercise_rest;
        private int exercise_image;

        private PlanExerciseItem(String name, int reps, int sets, String rest, int image){
            exercise_name = name;
            exercise_reps = reps;
            exercise_sets = sets;
            exercise_rest = rest;
            exercise_image = image;
        }

        private String getExercise_name(){
            return exercise_name;
        }

        private int getExercise_reps(){
            return exercise_reps;
        }

        private int getExercise_sets(){
            return exercise_sets;
        }

        private String getExercise_rest(){
            return exercise_rest;
        }

        private int getExercise_image(){
            return exercise_image;
        }

        @Override
        public String toString(){
            return "\n###################\nPLAN EXERCISE ITEM\nName: " + exercise_name + "\nSets: " + exercise_sets + "\nReps: " + exercise_reps + "\nRest: " + exercise_rest;
        }
    }

    // used in training exercises list view
    private class View_Holder extends RecyclerView.ViewHolder {

        CardView cv;
        ImageView plan_image;
        TextView plan_exercise;
        TextView plan_sets;
        TextView plan_reps;
        TextView plan_rest;

        View_Holder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.PE_cardView);
            plan_exercise = (TextView) itemView.findViewById(R.id.plan_exercise);
            plan_sets = (TextView) itemView.findViewById(R.id.plan_sets);
            plan_reps = (TextView) itemView.findViewById(R.id.plan_reps);
            plan_rest = (TextView) itemView.findViewById(R.id.plan_rest);
            plan_image = (ImageView) itemView.findViewById(R.id.plan_image);
        }
    }

    // adapter of recycler view used in training exercises list view
    private class Plan_Exercise_View_Adapter extends RecyclerView.Adapter<View_Holder>{

        List<PlanExerciseItem> list;

        private Plan_Exercise_View_Adapter(List<PlanExerciseItem> list){
            this.list = list;
        }

        @Override
        public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Inflate the layout, initialize the View Holder
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.costum_plan_exercise, parent, false);
            View_Holder holder = new View_Holder(v);
            return holder;
        }

        @Override
        public void onBindViewHolder(View_Holder holder, int position) {

            //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
            holder.plan_exercise.setText(list.get(position).exercise_name);
            holder.plan_sets.setText(Integer.toString(list.get(position).exercise_sets));
            holder.plan_reps.setText(Integer.toString(list.get(position).exercise_reps));
            holder.plan_rest.setText(list.get(position).exercise_rest);
            holder.plan_image.setImageResource(list.get(position).exercise_image);
            //animate(holder);
        }

        @Override
        public int getItemCount() {
            //returns the number of elements the RecyclerView will display
            return list.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        // Insert a new item to the RecyclerView on a predefined position
        public void insert(int position,PlanExerciseItem  data) {
            list.add(position, data);
            notifyItemInserted(position);
        }

        // Remove a RecyclerView item containing a specified Data object
        public void remove(PlanExerciseItem data) {
            int position = list.indexOf(data);
            list.remove(position);
            notifyItemRemoved(position);
        }
    }
}
