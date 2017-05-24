package g11.muscle.DB;

import android.provider.BaseColumns;

public class MuscleDbContract {

    private MuscleDbContract() {}

    public static class DeviceUser {
        public static final String TABLE_NAME = "DEVICE_USER";
        public static final String COLUMN_NAME_EMAIL = "Email";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_DOB = "Date_of_birth";
        public static final String COLUMN_NAME_GENDER = "Gender";
        public static final String COLUMN_NAME_HEIGHT = "Height";
        public static final String COLUMN_NAME_WEIGHT = "Weight";
        public static final String COLUMN_NAME_PROFILE_PIC = "Profile_image";
        public static final String COLUMN_NAME_PLAN = "Plan";
    }

    /* Inner class that defines the table contents */
    public static class Plan {
        public static final String TABLE_NAME = "PLAN";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_OBJ = "Objective";
    }

    /* Inner class that defines the table contents */
    public static class Training {
        public static final String TABLE_NAME = "TRAINING";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_PLAN = "Plan_id";
    }

    /* Inner class that defines the table contents */
    public static class Exercise {
        public static final String TABLE_NAME = "EXERCISE";
        public static final String COLUMN_NAME_NAME = "Name";
        public static final String COLUMN_NAME_IMAGE= "Image";
        public static final String COLUMN_NAME_DESCRIPTION = "Description";
        public static final String COLUMN_NAME_TYPE = "Kind";
    }

    /* Inner class that defines the table contents */
    public static class TrainingExercise {
        public static final String TABLE_NAME = "TRAINING_EXERCISE";
        public static final String COLUMN_NAME_ID = "Training_ID";
        public static final String COLUMN_NAME_EXERCISE = "Exercise_name";
        public static final String COLUMN_NAME_SETS = "Sets";
        public static final String COLUMN_NAME_REPETITION = "Repetitions";
        public static final String COLUMN_NAME_WEIGHT = "Weight";
        public static final String COLUMN_NAME_RESTING_TIME = "Resting_time";
    }


    /* Inner class that defines the table contents */
    public static class MuscleGroup {
        public static final String TABLE_NAME = "MUSCLE_GROUP";
        public static final String COLUMN_NAME_NAME= "Name";
        public static final String COLUMN_NAME_IMAGE = "Image";
    }

    /* Inner class that defines the table contents */
    public static class MusclesWorked {
        public static final String TABLE_NAME = "MUSCLES_WORKED";
        public static final String COLUMN_NAME_NAME = "Muscle_name";
        public static final String COLUMN_NAME_EXERCISE = "Exercise_name";
        public static final String COLUMN_NAME_PRIMARY = "primari";
    }

    /* Inner class that defines the table contents */
    public static class ExerciseHistory {
        public static final String TABLE_NAME = "EXERCISE_HISTORY";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_DATE = "Date_Time";
        public static final String COLUMN_NAME_EXERCISE = "Exercise_name";
        public static final String COLUMN_NAME_USER = "User_email";
        public static final String COLUMN_NAME_AVGINT = "Average_intensity";
        public static final String COLUMN_NAME_SET = "Set_amount";
    }

    /* Inner class that defines the table contents */
    public static class PlanHistory {
        public static final String TABLE_NAME = "PLAN_HISTORY";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_START = "Start_date";
        public static final String COLUMN_NAME_END = "End_date";
        public static final String COLUMN_NAME_OBJ = "Met_Objective";
        public static final String COLUMN_NAME_PLAN = "Plan_id";
        public static final String COLUMN_NAME_USER = "User_email";
    }

    /* Inner class that defines the table contents */
    public static class UserWeightHist {
        public static final String TABLE_NAME = "USER_WEIGHT_HISTORY";
        public static final String COLUMN_NAME_ID = "ID";
        public static final String COLUMN_NAME_USER = "User_email";
        public static final String COLUMN_NAME_WEIGHT = "Weight";
        public static final String COLUMN_NAME_DATE = "Date";
    }

    /* Inner class that defines the table contents */
    public static class Sets {
        public static final String TABLE_NAME = "SETS";
        public static final String COLUMN_NAME_HIST = "Exercise_history_id";
        public static final String COLUMN_NAME_NUM = "Set_number";
        public static final String COLUMN_NAME_REPS = "Repetitions";
        public static final String COLUMN_NAME_WEIGHT = "Weight";
        public static final String COLUMN_NAME_INT = "Intensity";
        public static final String COLUMN_NAME_REST = "Resting_Time";
        public static final String COLUMN_NAME_INTDEV = "Intensity_deviation";
    }
}
