package g11.muscle.DB;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by silveryu on 10-04-2017.
 */


public class MuscleDbHelper extends SQLiteOpenHelper {

    /* DEVICE_USERS DDL */
    private static final String SQL_CREATE_DEVICE_USERS =
            "CREATE TABLE " + MuscleDbContract.DeviceUser.TABLE_NAME + " (" +
                    MuscleDbContract.DeviceUser.COLUMN_NAME_EMAIL + " TEXT PRIMARY KEY," +
                    MuscleDbContract.DeviceUser.COLUMN_NAME_NAME + " TEXT," +
                    MuscleDbContract.DeviceUser.COLUMN_NAME_DOB + " DATE," +
                    MuscleDbContract.DeviceUser.COLUMN_NAME_GENDER + " BOOLEAN," +
                    MuscleDbContract.DeviceUser.COLUMN_NAME_HEIGHT + " INTEGER," +
                    MuscleDbContract.DeviceUser.COLUMN_NAME_WEIGHT + " REAL," +
                    MuscleDbContract.DeviceUser.COLUMN_NAME_PROFILE_PIC + " TEXT," +
                    MuscleDbContract.DeviceUser.COLUMN_NAME_PLAN + " INTEGER," +
                    "FOREIGN KEY(" + MuscleDbContract.DeviceUser.COLUMN_NAME_PLAN + ") "+
                    "REFERENCES " + MuscleDbContract.Plan.TABLE_NAME + "(" + MuscleDbContract.Plan.COLUMN_NAME_ID + "))";

    private static final String SQL_DELETE_DEVICE_USERS =
            "DROP TABLE IF EXISTS " + MuscleDbContract.DeviceUser.TABLE_NAME;


    /* Plan DDL */
    private static final String SQL_CREATE_PLAN =
            "CREATE TABLE " + MuscleDbContract.Plan.TABLE_NAME + " (" +
                    MuscleDbContract.Plan.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    MuscleDbContract.Plan.COLUMN_NAME_OBJ + " TEXT)";

    private static final String SQL_DELETE_PLAN =
            "DROP TABLE IF EXISTS " + MuscleDbContract.Plan.TABLE_NAME;


    /* TRAINING DDL */
    private static final String SQL_CREATE_TRAINING =
            "CREATE TABLE " + MuscleDbContract.Training.TABLE_NAME + " (" +
                    MuscleDbContract.Training.COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                    MuscleDbContract.Training.COLUMN_NAME_NAME + " TEXT," +
                    MuscleDbContract.Training.COLUMN_NAME_PLAN + " INTEGER," +
                    "FOREIGN KEY(" + MuscleDbContract.Training.COLUMN_NAME_PLAN + ") "+
                    "REFERENCES " + MuscleDbContract.Plan.TABLE_NAME + "(" + MuscleDbContract.Plan.COLUMN_NAME_ID + "))";

    private static final String SQL_DELETE_TRAINING =
            "DROP TABLE IF EXISTS " + MuscleDbContract.Training.TABLE_NAME;


    /* EXERCISE DDL */
    private static final String SQL_CREATE_EXERCISE =
            "CREATE TABLE " + MuscleDbContract.Exercise.TABLE_NAME + " (" +
                    MuscleDbContract.Exercise.COLUMN_NAME_NAME + " TEXT PRIMARY KEY," +
                    MuscleDbContract.Exercise.COLUMN_NAME_IMAGE + " TEXT," +
                    MuscleDbContract.Exercise.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    MuscleDbContract.Exercise.COLUMN_NAME_TYPE + "TEXT)";

    private static final String SQL_DELETE_EXERCISE =
            "DROP TABLE IF EXISTS " + MuscleDbContract.Exercise.TABLE_NAME;

    /* TRAINING_EXERCISE DDL */
    private static final String SQL_CREATE_TRAINING_EXERCISE =
            "CREATE TABLE " + MuscleDbContract.TrainingExercise.TABLE_NAME + " (" +
                    MuscleDbContract.TrainingExercise.COLUMN_NAME_ID + " INTEGER," +
                    MuscleDbContract.TrainingExercise.COLUMN_NAME_EXERCISE + " TEXT," +
                    MuscleDbContract.TrainingExercise.COLUMN_NAME_SETS + " INTEGER," +
                    MuscleDbContract.TrainingExercise.COLUMN_NAME_REPETITION + " INTEGER," +
                    MuscleDbContract.TrainingExercise.COLUMN_NAME_RESTING_TIME + " TIME," +
                    MuscleDbContract.TrainingExercise.COLUMN_NAME_WEIGHT + " INTEGER," +
                    "FOREIGN KEY(" + MuscleDbContract.TrainingExercise.COLUMN_NAME_ID + ") "+
                    "REFERENCES " + MuscleDbContract.Training.TABLE_NAME + "(" + MuscleDbContract.Training.COLUMN_NAME_ID + ")," +
                    "FOREIGN KEY(" + MuscleDbContract.TrainingExercise.COLUMN_NAME_EXERCISE + ") "+
                    "REFERENCES " + MuscleDbContract.Exercise.TABLE_NAME + "(" + MuscleDbContract.Exercise.COLUMN_NAME_NAME + ")," +
                    "PRIMARY KEY(" + MuscleDbContract.TrainingExercise.COLUMN_NAME_ID + "," + MuscleDbContract.TrainingExercise.COLUMN_NAME_EXERCISE + "))";

    private static final String SQL_DELETE_TRAINING_EXERCISE =
            "DROP TABLE IF EXISTS " + MuscleDbContract.TrainingExercise.TABLE_NAME;


    /* MUSCLE_GROUP DDL */
    private static final String SQL_CREATE_MUSCLE_GROUP =
            "CREATE TABLE " + MuscleDbContract.MuscleGroup.TABLE_NAME + " (" +
                    MuscleDbContract.MuscleGroup.COLUMN_NAME_NAME + " TEXT PRIMARY KEY," +
                    MuscleDbContract.MuscleGroup.COLUMN_NAME_IMAGE + " TEXT)";


    private static final String SQL_DELETE_MUSCLE_GROUP =
            "DROP TABLE IF EXISTS " + MuscleDbContract.MuscleGroup.TABLE_NAME;

    /* MUSCLES_WORKED DDL */
    private static final String SQL_CREATE_MUSCLES_WORKED =
            "CREATE TABLE " + MuscleDbContract.MusclesWorked.TABLE_NAME + " (" +
                    MuscleDbContract.MusclesWorked.COLUMN_NAME_NAME + " TEXT PRIMARY KEY," +
                    MuscleDbContract.MusclesWorked.COLUMN_NAME_EXERCISE + " TEXT," +
                    MuscleDbContract.MusclesWorked.COLUMN_NAME_PRIMARY + " BOOLEAN," +
                    "FOREIGN KEY(" + MuscleDbContract.MusclesWorked.COLUMN_NAME_NAME + ") "+
                    "REFERENCES " + MuscleDbContract.MuscleGroup.TABLE_NAME + "(" + MuscleDbContract.MuscleGroup.COLUMN_NAME_NAME + ")," +
                    "FOREIGN KEY(" + MuscleDbContract.MusclesWorked.COLUMN_NAME_EXERCISE + ") "+
                    "REFERENCES " + MuscleDbContract.Exercise.TABLE_NAME + "(" + MuscleDbContract.Exercise.COLUMN_NAME_NAME + ")," +
                    "PRIMARY KEY(" + MuscleDbContract.MusclesWorked.COLUMN_NAME_NAME + "," + MuscleDbContract.MusclesWorked.COLUMN_NAME_EXERCISE + "))";

    private static final String SQL_DELETE_MUSCLES_WORKED =
            "DROP TABLE IF EXISTS " + MuscleDbContract.MusclesWorked.TABLE_NAME;


    /* EXERCISE_HISTORY DDL */
    private static final String SQL_CREATE_EXERCISE_HISTORY =
            "CREATE TABLE " + MuscleDbContract.ExerciseHistory.TABLE_NAME + " (" +
                    MuscleDbContract.ExerciseHistory.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    MuscleDbContract.ExerciseHistory.COLUMN_NAME_DATE + " DATETIME," +
                    MuscleDbContract.ExerciseHistory.COLUMN_NAME_EXERCISE + " TEXT," +
                    MuscleDbContract.ExerciseHistory.COLUMN_NAME_USER + " TEXT," +
                    MuscleDbContract.ExerciseHistory.COLUMN_NAME_AVGINT + " REAL," +
                    MuscleDbContract.ExerciseHistory.COLUMN_NAME_SET + " INTEGER," +
                    "FOREIGN KEY(" + MuscleDbContract.ExerciseHistory.COLUMN_NAME_USER + ") "+
                    "REFERENCES " + MuscleDbContract.DeviceUser.TABLE_NAME + "(" + MuscleDbContract.DeviceUser.COLUMN_NAME_EMAIL + ")," +
                    "FOREIGN KEY(" + MuscleDbContract.ExerciseHistory.COLUMN_NAME_EXERCISE + ") "+
                    "REFERENCES " + MuscleDbContract.Exercise.TABLE_NAME + "(" + MuscleDbContract.Exercise.COLUMN_NAME_NAME + "))";


    private static final String SQL_DELETE_EXERCISE_HISTORY =
            "DROP TABLE IF EXISTS " + MuscleDbContract.ExerciseHistory.TABLE_NAME;


    /* PLAN_HISTORY DDL */
    private static final String SQL_CREATE_PLAN_HISTORY =
            "CREATE TABLE " + MuscleDbContract.PlanHistory.TABLE_NAME + " (" +
                    MuscleDbContract.PlanHistory.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    MuscleDbContract.PlanHistory.COLUMN_NAME_START + " DATETIME," +
                    MuscleDbContract.PlanHistory.COLUMN_NAME_END + " DATETIME," +
                    MuscleDbContract.PlanHistory.COLUMN_NAME_OBJ + " BOOLEAN," +
                    MuscleDbContract.PlanHistory.COLUMN_NAME_PLAN + " INTEGER," +
                    MuscleDbContract.PlanHistory.COLUMN_NAME_USER + " TEXT," +
                    "FOREIGN KEY(" + MuscleDbContract.PlanHistory.COLUMN_NAME_PLAN + ") "+
                    "REFERENCES " + MuscleDbContract.Plan.TABLE_NAME + "(" + MuscleDbContract.Plan.COLUMN_NAME_ID+ ")," +
                    "FOREIGN KEY(" + MuscleDbContract.PlanHistory.COLUMN_NAME_USER + ") "+
                    "REFERENCES " + MuscleDbContract.DeviceUser.TABLE_NAME + "(" + MuscleDbContract.DeviceUser.COLUMN_NAME_EMAIL + "))";

    private static final String SQL_DELETE_PLAN_HISTORY =
            "DROP TABLE IF EXISTS " + MuscleDbContract.PlanHistory.TABLE_NAME;


    /* USER_WEIGHT_HISTORY DDL */
    private static final String SQL_CREATE_USER_WEIGHT_HIST =
            "CREATE TABLE " + MuscleDbContract.UserWeightHist.TABLE_NAME + " (" +
                    MuscleDbContract.UserWeightHist.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    MuscleDbContract.UserWeightHist.COLUMN_NAME_USER + " TEXT," +
                    MuscleDbContract.UserWeightHist.COLUMN_NAME_WEIGHT + " INTEGER," +
                    MuscleDbContract.UserWeightHist.COLUMN_NAME_DATE + " DATE," +
                    "FOREIGN KEY(" + MuscleDbContract.UserWeightHist.COLUMN_NAME_USER + ") "+
                    "REFERENCES " + MuscleDbContract.DeviceUser.TABLE_NAME + "(" + MuscleDbContract.DeviceUser.COLUMN_NAME_EMAIL+ "))";

    private static final String SQL_DELETE_USER_WEIGHT_HIST =
            "DROP TABLE IF EXISTS " + MuscleDbContract.UserWeightHist.TABLE_NAME;


    /* SETS DDL */
    private static final String SQL_CREATE_SETS =
            "CREATE TABLE " + MuscleDbContract.Sets.TABLE_NAME + " (" +
            MuscleDbContract.Sets.COLUMN_NAME_HIST + " INTEGER," +
            MuscleDbContract.Sets.COLUMN_NAME_NUM + " INTEGER," +
            MuscleDbContract.Sets.COLUMN_NAME_REPS + " INTEGER," +
            MuscleDbContract.Sets.COLUMN_NAME_WEIGHT + " INTEGER," +
            MuscleDbContract.Sets.COLUMN_NAME_INT + " REAL," +
            MuscleDbContract.Sets.COLUMN_NAME_REST + " TIME," +
            MuscleDbContract.Sets.COLUMN_NAME_INTDEV + " REAL," +
            "FOREIGN KEY(" + MuscleDbContract.Sets.COLUMN_NAME_HIST + ") "+
            "REFERENCES " + MuscleDbContract.ExerciseHistory.TABLE_NAME + "(" + MuscleDbContract.ExerciseHistory.COLUMN_NAME_ID + ")," +
            "PRIMARY KEY(" + MuscleDbContract.Sets.COLUMN_NAME_HIST + "," + MuscleDbContract.Sets.COLUMN_NAME_NUM + "))";

    private static final String SQL_DELETE_SETS =
            "DROP TABLE IF EXISTS " + MuscleDbContract.Sets.TABLE_NAME;


    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Muscle.db";

    public MuscleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EXERCISE);
        db.execSQL(SQL_CREATE_PLAN);
        db.execSQL(SQL_CREATE_DEVICE_USERS);
        db.execSQL(SQL_CREATE_TRAINING);
        db.execSQL(SQL_CREATE_TRAINING_EXERCISE);
        db.execSQL(SQL_CREATE_MUSCLE_GROUP);
        db.execSQL(SQL_CREATE_MUSCLES_WORKED);
        db.execSQL(SQL_CREATE_EXERCISE_HISTORY);
        db.execSQL(SQL_CREATE_PLAN_HISTORY);
        db.execSQL(SQL_CREATE_USER_WEIGHT_HIST);
        db.execSQL(SQL_CREATE_SETS);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_SETS);
        db.execSQL(SQL_DELETE_USER_WEIGHT_HIST);
        db.execSQL(SQL_DELETE_PLAN_HISTORY);
        db.execSQL(SQL_DELETE_EXERCISE_HISTORY);
        db.execSQL(SQL_DELETE_MUSCLES_WORKED);
        db.execSQL(SQL_DELETE_MUSCLE_GROUP);
        db.execSQL(SQL_DELETE_TRAINING_EXERCISE);
        db.execSQL(SQL_DELETE_TRAINING);
        db.execSQL(SQL_DELETE_DEVICE_USERS);
        db.execSQL(SQL_DELETE_PLAN);
        db.execSQL(SQL_DELETE_EXERCISE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
