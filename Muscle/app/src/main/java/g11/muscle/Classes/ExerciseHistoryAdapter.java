package g11.muscle.Classes;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import g11.muscle.R;

/**
 * Created by silveryu on 28-05-2017.
 */

public class ExerciseHistoryAdapter extends RecyclerView.Adapter<ExerciseHistoryAdapter.ExerciseHistoryViewHolder>{

    //shown list
    private List<JSONObject> historyList;
    // original list
    private List<JSONObject> originalList;
    public View.OnClickListener mOnClickListener;

    public static class ExerciseHistoryViewHolder extends RecyclerView.ViewHolder {
        TextView exerciseName;
        TextView dateTime;

        ExerciseHistoryViewHolder(final View itemView) {
            super(itemView);

            int[] attrs = new int[]{R.attr.selectableItemBackground};
            TypedArray typedArray = itemView.getContext().obtainStyledAttributes(attrs);
            int backgroundResource = typedArray.getResourceId(0, 0);
            itemView.setBackgroundResource(backgroundResource);

            exerciseName = (TextView) itemView.findViewById(android.R.id.text1);
            dateTime = (TextView) itemView.findViewById(android.R.id.text2);
            dateTime.setTextColor(Color.LTGRAY);

        }
    }

    public ExerciseHistoryAdapter(List<JSONObject> historyList) {
        this.historyList = historyList;
    }

    @Override
    public ExerciseHistoryViewHolder onCreateViewHolder(ViewGroup parent, final int i) {
        View v = LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);

        v.setOnClickListener(mOnClickListener);
        ExerciseHistoryViewHolder ehvh = new ExerciseHistoryViewHolder(v);
        return ehvh;
    }

    @Override
    public void onBindViewHolder(final ExerciseHistoryViewHolder historyViewHolder, final int position) {
        try{
        String exerciseName = historyList.get(position).getString("Exercise_name");
        String dateTime = historyList.get(position).getString("Date_Time");

        //if(!TextUtils.isEmpty(exerciseName))
        historyViewHolder.exerciseName.setText(exerciseName);
        //if(!TextUtils.isEmpty(dateTime))
        historyViewHolder.dateTime.setText(dateTime);

        }
        catch(JSONException e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }
    //Filter results

    public Filter getFilter() {
        return new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                final FilterResults oReturn = new FilterResults();
                final List<JSONObject> results = new ArrayList<>();

                if (originalList == null)
                    originalList = historyList;

                if (constraint != null) {
                    if (originalList.size() > 0) {
                        try {
                            String[] searchSubstringArray = constraint.toString().toLowerCase().trim().split("[\\s,;]+");

                            for (final JSONObject historyObject : originalList) {

                                boolean matchesFullSearch = true;

                                String exerciseName = historyObject.getString("Exercise_name");
                                String dateTime = historyObject.getString("Date_Time");

                                for (String searchSubstring : searchSubstringArray) {

                                    boolean matchesSubstring =
                                            exerciseName.toLowerCase().contains(searchSubstring) ||
                                                    dateTime.toLowerCase().contains(searchSubstring);

                                    matchesFullSearch = matchesFullSearch && matchesSubstring;
                                }

                                if (matchesFullSearch)
                                    results.add(historyObject);
                            }
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                    oReturn.values = results;
                }
                return oReturn;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                historyList = (ArrayList<JSONObject>) results.values;
                notifyDataSetChanged();
            }
        };
    }


}
