package g11.muscle;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import g11.muscle.Classes.DayPlanAdapter;
import g11.muscle.Classes.PlanExerciseItem;
import g11.muscle.Classes.Plan_Exercise_View;

public class CreatePlanActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DayPlanAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);
        this.recyclerView = (RecyclerView) findViewById(R.id.day_list_view);

        ArrayList<ArrayList<PlanExerciseItem>> list = new ArrayList<>();
        ArrayList<String> titles = new ArrayList<>();

        adapter = new DayPlanAdapter(this, titles, list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(CreatePlanActivity.this));
    }

    public void onClickAddDay(View view){
        final EditText editText = new EditText(CreatePlanActivity.this);
        new android.app.AlertDialog.Builder(CreatePlanActivity.this)
                .setTitle("Add day")
                .setView(editText)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.insert(editText.getText().toString(), new ArrayList<PlanExerciseItem>());
                    }
                })
                .setNegativeButton("Cancel", null).show();
    }

    public void onClickAddExercise(View view){
        final Spinner spinner = new Spinner(CreatePlanActivity.this);
        ArrayAdapter<String> a = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, this.adapter.titles);
        spinner.setAdapter(a);
        new android.app.AlertDialog.Builder(CreatePlanActivity.this)
                .setTitle("Add exercise")
                .setView(spinner)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        //SILVÈRIO TRABALHA AQUI
                        spinner.getSelectedItemPosition();
                    }
                })
                
                .setNegativeButton("Cancel", null).show();
    }
}
