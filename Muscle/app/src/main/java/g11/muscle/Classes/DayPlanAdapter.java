package g11.muscle.Classes;

import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import g11.muscle.CreatePlanActivity;
import g11.muscle.PlanActivity;
import g11.muscle.R;

// adapter of recycler view used in training exercises list view
public class DayPlanAdapter extends RecyclerView.Adapter<DayPlanAdapter.DayPlanAdapter_Holder>{

    public List<String> titles;
    private List<ArrayList<PlanExerciseItem>> list;
    private CreatePlanActivity parent;

    public DayPlanAdapter(CreatePlanActivity parent, List<String> titles, List<ArrayList<PlanExerciseItem>> list) {
        this.titles = titles;
        this.list = list;
        this.parent = parent;
    }

    @Override
    public DayPlanAdapter_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_day, parent, false);
        return new DayPlanAdapter_Holder(v);
    }

    @Override
    public void onBindViewHolder(DayPlanAdapter_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.day_title.setText(titles.get(position));

        Plan_Exercise_View adapter = new Plan_Exercise_View(list.get(position));
        holder.exercise_list_day.setAdapter(adapter);
        holder.exercise_list_day.setLayoutManager(new LinearLayoutManager(parent.getApplicationContext()));
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

    public void insert_item_to_day(int position, PlanExerciseItem item){
        list.get(position).add(item);
    }

    // Insert a new item to the RecyclerView on a predefined position
    public void insert(String title, ArrayList<PlanExerciseItem> data) {
        titles.add(title);
        list.add(data);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(PlanExerciseItem data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public class DayPlanAdapter_Holder extends  RecyclerView.ViewHolder {

        private TextView day_title;
        private android.support.v7.widget.RecyclerView exercise_list_day;

        private DayPlanAdapter_Holder(View itemView) {
            super(itemView);
            day_title = (TextView) itemView.findViewById(R.id.day_title);
            exercise_list_day = (android.support.v7.widget.RecyclerView) itemView.findViewById(R.id.exercise_list_day);
        }
    }
}
