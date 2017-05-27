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
    private int exercise_image;
    private int exercise_weight;

    public PlanExerciseItem(String name, int reps, int sets, String rest,int weight, int image){
        exercise_name = name;
        exercise_reps = reps;
        exercise_sets = sets;
        exercise_rest = rest;
        exercise_weight = weight;
        exercise_image = image;
    }

    public String getExercise_name(){
        return exercise_name;
    }

    int getExercise_reps(){
        return exercise_reps;
    }

    int getExercise_sets(){
        return exercise_sets;
    }

    String getExercise_rest(){
        return exercise_rest;
    }

    int getExercise_weight() {return exercise_weight; }

    int getExercise_image(){
        return exercise_image;
    }

    @Override
    public String toString(){
        return "\n###################\nPLAN EXERCISE ITEM\nName: " + exercise_name + "\nSets: " + exercise_sets + "\nReps: " + exercise_reps + "\nRest: " + exercise_rest;
    }
}
