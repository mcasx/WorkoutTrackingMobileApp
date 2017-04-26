package g11.muscle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import g11.muscle.MuscleDbContract.*;

/**
 * Created by silveryu on 10-04-2017.
 */


public class MuscleDbHelper extends SQLiteOpenHelper {

    /* DEVICE_USERS DDL */
    private static final String SQL_CREATE_DEVICE_USERS =
            "CREATE TABLE " + DeviceUser.TABLE_NAME + " (" +
                    DeviceUser.COLUMN_NAME_EMAIL + " TEXT PRIMARY KEY," +
                    DeviceUser.COLUMN_NAME_NAME + " TEXT," +
                    DeviceUser.COLUMN_NAME_DOB + " DATE," +
                    DeviceUser.COLUMN_NAME_GENDER + " BOOLEAN," +
                    DeviceUser.COLUMN_NAME_HEIGHT + " INTEGER," +
                    DeviceUser.COLUMN_NAME_WEIGHT + " REAL," +
                    DeviceUser.COLUMN_NAME_PROFILE_PIC + " TEXT," +
                    DeviceUser.COLUMN_NAME_PLAN + " INTEGER," +
                    "FOREIGN KEY(" + DeviceUser.COLUMN_NAME_PLAN + ") "+
                    "REFERENCES " + Plan.TABLE_NAME + "(" + Plan.COLUMN_NAME_ID + "))";

    private static final String SQL_DELETE_DEVICE_USERS =
            "DROP TABLE IF EXISTS " + DeviceUser.TABLE_NAME;


    /* Plan DDL */
    private static final String SQL_CREATE_PLAN =
            "CREATE TABLE " + Plan.TABLE_NAME + " (" +
                    Plan.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    Plan.COLUMN_NAME_OBJ + " TEXT)";

    private static final String SQL_DELETE_PLAN =
            "DROP TABLE IF EXISTS " + Plan.TABLE_NAME;


    /* TRAINING DDL */
    private static final String SQL_CREATE_TRAINING =
            "CREATE TABLE " + Training.TABLE_NAME + " (" +
                    Training.COLUMN_NAME_ID + " TEXT PRIMARY KEY," +
                    Training.COLUMN_NAME_NAME + " TEXT," +
                    Training.COLUMN_NAME_PLAN + " INTEGER," +
                    "FOREIGN KEY(" + Training.COLUMN_NAME_PLAN + ") "+
                    "REFERENCES " + Plan.TABLE_NAME + "(" + Plan.COLUMN_NAME_ID + "))";

    private static final String SQL_DELETE_TRAINING =
            "DROP TABLE IF EXISTS " + Training.TABLE_NAME;


    /* EXERCISE DDL */
    private static final String SQL_CREATE_EXERCISE =
            "CREATE TABLE " + Exercise.TABLE_NAME + " (" +
                    Exercise.COLUMN_NAME_NAME + " TEXT PRIMARY KEY," +
                    Exercise.COLUMN_NAME_IMAGE + " TEXT," +
                    Exercise.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    Exercise.COLUMN_NAME_TYPE + "TEXT)";

    private static final String SQL_DELETE_EXERCISE =
            "DROP TABLE IF EXISTS " + Exercise.TABLE_NAME;

    /* TRAINING_EXERCISE DDL */
    private static final String SQL_CREATE_TRAINING_EXERCISE =
            "CREATE TABLE " + TrainingExercise.TABLE_NAME + " (" +
                    TrainingExercise.COLUMN_NAME_ID + " INTEGER," +
                    TrainingExercise.COLUMN_NAME_EXERCISE + " TEXT," +
                    TrainingExercise.COLUMN_NAME_SETS + " INTEGER," +
                    TrainingExercise.COLUMN_NAME_REPETITION + " INTEGER," +
                    TrainingExercise.COLUMN_NAME_RESTING_TIME + " TIME," +
                    TrainingExercise.COLUMN_NAME_WEIGHT + " INTEGER," +
                    "FOREIGN KEY(" + TrainingExercise.COLUMN_NAME_ID + ") "+
                    "REFERENCES " + Training.TABLE_NAME + "(" + Training.COLUMN_NAME_ID + ")," +
                    "FOREIGN KEY(" + TrainingExercise.COLUMN_NAME_EXERCISE + ") "+
                    "REFERENCES " + Exercise.TABLE_NAME + "(" + Exercise.COLUMN_NAME_NAME + ")," +
                    "PRIMARY KEY(" + TrainingExercise.COLUMN_NAME_ID + "," + TrainingExercise.COLUMN_NAME_EXERCISE + "))";

    private static final String SQL_DELETE_TRAINING_EXERCISE =
            "DROP TABLE IF EXISTS " + TrainingExercise.TABLE_NAME;


    /* MUSCLE_GROUP DDL */
    private static final String SQL_CREATE_MUSCLE_GROUP =
            "CREATE TABLE " + MuscleGroup.TABLE_NAME + " (" +
                    MuscleGroup.COLUMN_NAME_NAME + " TEXT PRIMARY KEY," +
                    MuscleGroup.COLUMN_NAME_IMAGE + " TEXT)";


    private static final String SQL_DELETE_MUSCLE_GROUP =
            "DROP TABLE IF EXISTS " + MuscleGroup.TABLE_NAME;

    /* MUSCLES_WORKED DDL */
    private static final String SQL_CREATE_MUSCLES_WORKED =
            "CREATE TABLE " + MusclesWorked.TABLE_NAME + " (" +
                    MusclesWorked.COLUMN_NAME_NAME + " TEXT PRIMARY KEY," +
                    MusclesWorked.COLUMN_NAME_EXERCISE + " TEXT," +
                    MusclesWorked.COLUMN_NAME_PRIMARY + " BOOLEAN," +
                    "FOREIGN KEY(" + MusclesWorked.COLUMN_NAME_NAME + ") "+
                    "REFERENCES " + MuscleGroup.TABLE_NAME + "(" + MuscleGroup.COLUMN_NAME_NAME + ")," +
                    "FOREIGN KEY(" + MusclesWorked.COLUMN_NAME_EXERCISE + ") "+
                    "REFERENCES " + Exercise.TABLE_NAME + "(" + Exercise.COLUMN_NAME_NAME + ")," +
                    "PRIMARY KEY(" + MusclesWorked.COLUMN_NAME_NAME + "," + MusclesWorked.COLUMN_NAME_EXERCISE + "))";

    private static final String SQL_DELETE_MUSCLES_WORKED =
            "DROP TABLE IF EXISTS " + MusclesWorked.TABLE_NAME;


    /* EXERCISE_HISTORY DDL */
    private static final String SQL_CREATE_EXERCISE_HISTORY =
            "CREATE TABLE " + ExerciseHistory.TABLE_NAME + " (" +
                    ExerciseHistory.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    ExerciseHistory.COLUMN_NAME_DATE + " DATETIME," +
                    ExerciseHistory.COLUMN_NAME_EXERCISE + " TEXT," +
                    ExerciseHistory.COLUMN_NAME_USER + " TEXT," +
                    ExerciseHistory.COLUMN_NAME_AVGINT + " REAL," +
                    ExerciseHistory.COLUMN_NAME_SET + " INTEGER," +
                    "FOREIGN KEY(" + ExerciseHistory.COLUMN_NAME_USER + ") "+
                    "REFERENCES " + DeviceUser.TABLE_NAME + "(" + DeviceUser.COLUMN_NAME_EMAIL + ")," +
                    "FOREIGN KEY(" + ExerciseHistory.COLUMN_NAME_EXERCISE + ") "+
                    "REFERENCES " + Exercise.TABLE_NAME + "(" + Exercise.COLUMN_NAME_NAME + "))";


    private static final String SQL_DELETE_EXERCISE_HISTORY =
            "DROP TABLE IF EXISTS " + ExerciseHistory.TABLE_NAME;


    /* PLAN_HISTORY DDL */
    private static final String SQL_CREATE_PLAN_HISTORY =
            "CREATE TABLE " + PlanHistory.TABLE_NAME + " (" +
                    PlanHistory.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    PlanHistory.COLUMN_NAME_START + " DATETIME," +
                    PlanHistory.COLUMN_NAME_END + " DATETIME," +
                    PlanHistory.COLUMN_NAME_OBJ + " BOOLEAN," +
                    PlanHistory.COLUMN_NAME_PLAN + " INTEGER," +
                    PlanHistory.COLUMN_NAME_USER + " TEXT," +
                    "FOREIGN KEY(" + PlanHistory.COLUMN_NAME_PLAN + ") "+
                    "REFERENCES " + Plan.TABLE_NAME + "(" + Plan.COLUMN_NAME_ID+ ")," +
                    "FOREIGN KEY(" + PlanHistory.COLUMN_NAME_USER + ") "+
                    "REFERENCES " + DeviceUser.TABLE_NAME + "(" + DeviceUser.COLUMN_NAME_EMAIL + "))";

    private static final String SQL_DELETE_PLAN_HISTORY =
            "DROP TABLE IF EXISTS " + PlanHistory.TABLE_NAME;


    /* USER_WEIGHT_HISTORY DDL */
    private static final String SQL_CREATE_USER_WEIGHT_HIST =
            "CREATE TABLE " + UserWeightHist.TABLE_NAME + " (" +
                    UserWeightHist.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    UserWeightHist.COLUMN_NAME_USER + " TEXT," +
                    UserWeightHist.COLUMN_NAME_WEIGHT + " INTEGER," +
                    UserWeightHist.COLUMN_NAME_DATE + " DATE," +
                    "FOREIGN KEY(" + UserWeightHist.COLUMN_NAME_USER + ") "+
                    "REFERENCES " + DeviceUser.TABLE_NAME + "(" + DeviceUser.COLUMN_NAME_EMAIL+ "))";

    private static final String SQL_DELETE_USER_WEIGHT_HIST =
            "DROP TABLE IF EXISTS " + UserWeightHist.TABLE_NAME;


    /* SETS DDL */
    private static final String SQL_CREATE_SETS =
            "CREATE TABLE " + Sets.TABLE_NAME + " (" +
            Sets.COLUMN_NAME_HIST + " INTEGER," +
            Sets.COLUMN_NAME_NUM + " INTEGER," +
            Sets.COLUMN_NAME_REPS + " INTEGER," +
            Sets.COLUMN_NAME_WEIGHT + " INTEGER," +
            Sets.COLUMN_NAME_INT + " REAL," +
            Sets.COLUMN_NAME_REST + " TIME," +
            Sets.COLUMN_NAME_INTDEV + " REAL," +
            "FOREIGN KEY(" + Sets.COLUMN_NAME_HIST + ") "+
            "REFERENCES " + ExerciseHistory.TABLE_NAME + "(" + ExerciseHistory.COLUMN_NAME_ID + ")," +
            "PRIMARY KEY(" + Sets.COLUMN_NAME_HIST + "," + Sets.COLUMN_NAME_NUM + "))";

    private static final String SQL_DELETE_SETS =
            "DROP TABLE IF EXISTS " + Sets.TABLE_NAME;


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
