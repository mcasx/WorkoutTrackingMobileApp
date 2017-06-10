package g11.muscle.Classes;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import g11.muscle.R;

// adapter of recycler view used in training exercises list view
public class Plan_Exercise_View extends RecyclerView.Adapter<Plan_Exercise_View.Plan_Exercise_View_Holder>{

    private List<PlanExerciseItem> list;
    public View.OnClickListener mOnClickListener;

    public Plan_Exercise_View(List<PlanExerciseItem> list) {
        this.list = list;
    }

    @Override
    public Plan_Exercise_View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.costum_plan_exercise_card, parent, false);
        v.setOnClickListener(mOnClickListener);
        return new Plan_Exercise_View_Holder(v);
    }

    @Override
    public void onBindViewHolder(Plan_Exercise_View_Holder holder, int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.plan_exercise.setText(list.get(position).getExercise_name());
        holder.plan_sets.setText(Integer.toString(list.get(position).getExercise_sets()));
        holder.plan_reps.setText(Integer.toString(list.get(position).getExercise_reps()));
        holder.plan_rest.setText(list.get(position).getExercise_rest());
        holder.plan_weight.setText(Integer.toString(list.get(position).getExercise_weight()));

        if(list.get(position).getMode() == 1) // check mark visible
            holder.check.setVisibility(View.VISIBLE);
        else if(list.get(position).getMode() == 0)
            holder.itemView.setAlpha(0.55f);
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
    public void insert(int position, PlanExerciseItem data) {
        list.add(position, data);
        notifyItemInserted(position);
    }

    // Remove a RecyclerView item containing a specified Data object
    public void remove(PlanExerciseItem data) {
        int position = list.indexOf(data);
        list.remove(position);
        notifyItemRemoved(position);
    }

    public class Plan_Exercise_View_Holder extends  RecyclerView.ViewHolder {

        private CardView cv;
        private TextView plan_exercise;
        private TextView plan_sets;
        private TextView plan_reps;
        private TextView plan_rest;
        private TextView plan_weight;
        private TextView check;

        private Plan_Exercise_View_Holder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.PE_cardView);
            plan_exercise = (TextView) itemView.findViewById(R.id.plan_exercise);
            plan_sets = (TextView) itemView.findViewById(R.id.plan_sets);
            plan_reps = (TextView) itemView.findViewById(R.id.plan_reps);
            plan_rest = (TextView) itemView.findViewById(R.id.plan_rest);
            plan_weight = (TextView) itemView.findViewById(R.id.plan_weight);
            check = (TextView) itemView.findViewById(R.id.check);
        }
    }
}
