package g11.muscle.Classes;

/**
 * Created by Xdye on 22/05/2017.
 */

// It defines a Exercise ( Used in training exercises list view )
public class PlanExerciseItem {
    private String exercise_name;
    private int exercise_reps;
    private int exercise_sets;
    private String exercise_rest;
    private int exercise_weight;
    private int mode; // used in my plan current exercise (oval shape)

    public PlanExerciseItem(String name, int reps, int sets, String rest,int weight){
        exercise_name = name;
        exercise_reps = reps;
        exercise_sets = sets;
        exercise_rest = rest;
        exercise_weight = weight;
        this.mode = 0;
    }

    public PlanExerciseItem(String name, int reps, int sets, String rest,int weight, int mode){
        exercise_name = name;
        exercise_reps = reps;
        exercise_sets = sets;
        exercise_rest = rest;
        exercise_weight = weight;
        this.mode = mode;
    }

    public String getExercise_name(){
        return exercise_name;
    }

    public int getExercise_reps(){
        return exercise_reps;
    }

    public int getExercise_sets(){
        return exercise_sets;
    }

    public String getExercise_rest(){
        return exercise_rest;
    }

    public int getExercise_weight() {return exercise_weight; }

    public int getMode() { return mode; }

    @Override
    public String toString(){
        return "\n###################\nPLAN EXERCISE ITEM\nName: " + exercise_name + "\nSets: " + exercise_sets + "\nReps: "
                + exercise_reps + "\nRest: " + exercise_rest +"\nWeight: " + exercise_weight;
    }
}
