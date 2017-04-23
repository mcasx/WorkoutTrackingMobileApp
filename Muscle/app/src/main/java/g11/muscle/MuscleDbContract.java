package g11.muscle;

import android.provider.BaseColumns;

public class MuscleDbContract {

    private MuscleDbContract() {}

    /* Inner class that defines the table contents */
    public static class Exercise {
        public static final String TABLE_NAME = "exercise";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DESCRIPTION = "description";
        public static final String COLUMN_NAME_IMAGE= "image_path";
    }

    /* Inner class that defines the table contents */
    public static class ExerciseHistory {
        public static final String TABLE_NAME = "exercise_history";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PLAN = "plan_id";
        public static final String COLUMN_NAME_EXERCISE = "exercise_id";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_REPS = "number_of_repetitions";
        public static final String COLUMN_NAME_AVGINT = "average_intensity";
        public static final String COLUMN_NAME_AVGREP = "average_rep_time";
    }

    /* Inner class that defines the table contents */
    public static class ExerciseMuscleGroup {
        public static final String TABLE_NAME = "exercise_muscle_group";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_EXERCISE = "exercise_id";
        public static final String COLUMN_NAME_MUSCLE_GROUP = "muscle_group_id";
        public static final String COLUMN_NAME_PRIMARY = "primary_muscle";
    }

    /* Inner class that defines the table contents */
    public static class ExerciseMuscleType {
        public static final String TABLE_NAME = "exercise_muscle_type";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_EXERCISE = "exercise_id";
        public static final String COLUMN_NAME_MUSCLE_TYPE = "muscle_type_id";
    }

    /* Inner class that defines the table contents */
    public static class PlanExercises {
        public static final String TABLE_NAME = "exercises_in_plan";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_PLAN = "plan_id";
        public static final String COLUMN_NAME_EXERCISE = "exercise_id";
        public static final String COLUMN_NAME_SETS = "number_of_sets";
        public static final String COLUMN_NAME_REPS = "number_of_reps";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_SUBPLAN = "subplan_number";
    }

    /* Inner class that defines the table contents */
    public static class MuscleGroup {
        public static final String TABLE_NAME = "muscle_group";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME= "name";
    }

    /* Inner class that defines the table contents */
    public static class MuscleType {
        public static final String TABLE_NAME = "muscle_type";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME= "name";
    }

    /* Inner class that defines the table contents */
    public static class Plan {
        public static final String TABLE_NAME = "plan";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_USER = "user_id";
        public static final String COLUMN_NAME_NAME = "plan_name";
        public static final String COLUMN_NAME_START = "start_date";
        public static final String COLUMN_NAME_END = "end_date";
        public static final String COLUMN_NAME_TYPE = "plan_type_id";
        public static final String COLUMN_NAME_STATUS = "status";
    }

    /* Inner class that defines the table contents */
    public static class PlanType {
        public static final String TABLE_NAME = "plan_type";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_NAME= "plan_type";
    }

    public static class DeviceUser {
        public static final String TABLE_NAME = "device_user";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_EMAIL = "email";
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_DOB = "date_of_birth";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_HEIGHT = "height";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_PROFILE_PIC = "profile_image";
    }

    /* Inner class that defines the table contents */
    public static class UserWeightHist {
        public static final String TABLE_NAME = "user_weight_history";
        public static final String COLUMN_NAME_ID = "id";
        public static final String COLUMN_NAME_USER = "user_id";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_DATE = "date";
    }

    /*

    public static class Sets {
        public static final String TABLE_NAME = "SETS";
        public static final String COLUMN_NAME_HIST = "exercise_hist";
        public static final String COLUMN_NAME_NUM = "set_number";
        public static final String COLUMN_NAME_REPS = "repetitions";
        public static final String COLUMN_NAME_INT = "intensity";
        public static final String COLUMN_NAME_WEIGHT = "weight";
        public static final String COLUMN_NAME_REST = "resting_time";
        public static final String COLUMN_NAME_AVGREP = "average_rep_time";
        public static final String COLUMN_NAME_REPDEV = "rep_time_deviation";
    }*/
}
