package g11.muscle.Classes;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import g11.muscle.CreatePlanActivity;
import g11.muscle.R;
import g11.muscle.SelectExercises;

// adapter of recycler view used in training exercises list view
public class DayPlanAdapter extends RecyclerView.Adapter<DayPlanAdapter.DayPlanAdapter_Holder> implements ItemTouchHelperAdapter{

    public List<String> titles;
    public List<ArrayList<PlanExerciseItem>> list;
    private CreatePlanActivity parent;
    private Context context;
    private int buttonPos;
    private DayPlanAdapter_Holder holder;

    public DayPlanAdapter(CreatePlanActivity parent, List<String> titles, List<ArrayList<PlanExerciseItem>> list, Context context) {
        this.titles = titles;
        this.list = list;
        this.parent = parent;
        this.context = context;
        setHasStableIds(true);
    }

    @Override
    public DayPlanAdapter_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        //Inflate the layout, initialize the View Holder
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.plan_day, parent, false);
        return new DayPlanAdapter_Holder(v);
    }

    @Override
    public void onBindViewHolder(DayPlanAdapter_Holder holder, final int position) {

        //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
        holder.day_title.setText(titles.get(position));

        PlanExerciseAdapter adapter = new PlanExerciseAdapter(list.get(position));
        holder.exercise_list_day.setAdapter(adapter);
        holder.exercise_list_day.setLayoutManager(new LinearLayoutManager(parent.getApplicationContext()));
        holder.add_exercise_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                buttonPos = position;
                Intent i = new Intent(context, SelectExercises.class);
                parent.startActivityForResult(i, 1);
            }
        });
        this.holder = holder;
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

    @Override
    public void onItemDismiss(int position) {
        list.remove(position);
        titles.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(list, i, i + 1);
                Collections.swap(titles, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(list, i, i - 1);
                Collections.swap(titles, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    public void addExercises(ArrayList<String> exercises){
        for(String e: exercises){
            list.get(buttonPos).add(new PlanExerciseItem(e, 12, 3, "00:00:30", 0));
        }
        PlanExerciseAdapter adapter = new PlanExerciseAdapter(list.get(buttonPos));
        holder.exercise_list_day.setAdapter(adapter);
        holder.exercise_list_day.setLayoutManager(new LinearLayoutManager(parent.getApplicationContext()));
    }


    public class DayPlanAdapter_Holder extends RecyclerView.ViewHolder {

        private TextView day_title;
        private android.support.v7.widget.RecyclerView exercise_list_day;
        private FloatingActionButton add_exercise_button;

        private DayPlanAdapter_Holder(View itemView) {
            super(itemView);
            day_title = (TextView) itemView.findViewById(R.id.day_title);
            exercise_list_day = (android.support.v7.widget.RecyclerView) itemView.findViewById(R.id.exercise_list_day);
            add_exercise_button = (FloatingActionButton) itemView.findViewById(R.id.add_exercise_button);
        }
    }


}
