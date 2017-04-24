package g11.muscle;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import g11.muscle.MuscleDbContract.*;

/**
 * Created by silveryu on 10-04-2017.
 */


public class MuscleDbHelper extends SQLiteOpenHelper {

    /* EXERCISE DDL */
    private static final String SQL_CREATE_EXERCISE =
            "CREATE TABLE " + Exercise.TABLE_NAME + " (" +
                    Exercise.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    Exercise.COLUMN_NAME_NAME + " TEXT," +
                    Exercise.COLUMN_NAME_DESCRIPTION + " INTEGER," +
                    Exercise.COLUMN_NAME_IMAGE + " TEXT)";

    private static final String SQL_DELETE_EXERCISE =
            "DROP TABLE IF EXISTS " + Exercise.TABLE_NAME;


    /* EXERCISE_HISTORY DDL */
    private static final String SQL_CREATE_EXERCISE_HISTORY =
            "CREATE TABLE " + ExerciseHistory.TABLE_NAME + " (" +
                    ExerciseHistory.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    ExerciseHistory.COLUMN_NAME_PLAN + " INTEGER," +
                    ExerciseHistory.COLUMN_NAME_EXERCISE + " INTEGER," +
                    ExerciseHistory.COLUMN_NAME_WEIGHT + " INTEGER," +
                    ExerciseHistory.COLUMN_NAME_REPS + " INTEGER," +
                    ExerciseHistory.COLUMN_NAME_AVGINT + " REAL," +
                    ExerciseHistory.COLUMN_NAME_AVGREP + " REAL," +
                    "FOREIGN KEY(" + ExerciseHistory.COLUMN_NAME_PLAN + ") "+
                    "REFERENCES " + Plan.TABLE_NAME + "(" + Plan.COLUMN_NAME_ID + ")," +
                    "FOREIGN KEY(" + ExerciseHistory.COLUMN_NAME_EXERCISE + ") "+
                    "REFERENCES " + Exercise.TABLE_NAME + "(" + Exercise.COLUMN_NAME_ID + "))";


    private static final String SQL_DELETE_EXERCISE_HISTORY =
            "DROP TABLE IF EXISTS " + ExerciseHistory.TABLE_NAME;

    /* EXERCISE_MUSCLE_GROUP DDL */
    private static final String SQL_CREATE_EXERCISE_MUSCLE_GROUP =
            "CREATE TABLE " + ExerciseMuscleGroup.TABLE_NAME + " (" +
                    ExerciseMuscleGroup.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    ExerciseMuscleGroup.COLUMN_NAME_EXERCISE+ " INTEGER," +
                    ExerciseMuscleGroup.COLUMN_NAME_MUSCLE_GROUP+ " INTEGER," +
                    ExerciseMuscleGroup.COLUMN_NAME_PRIMARY + " BOOLEAN," +
                    "FOREIGN KEY(" + ExerciseMuscleGroup.COLUMN_NAME_EXERCISE + ") "+
                    "REFERENCES " + Exercise.TABLE_NAME + "(" + Exercise.COLUMN_NAME_ID + ")," +
                    "FOREIGN KEY(" + ExerciseMuscleGroup.COLUMN_NAME_MUSCLE_GROUP + ") "+
                    "REFERENCES " + MuscleGroup.TABLE_NAME + "(" + MuscleGroup.COLUMN_NAME_ID + "))";


    private static final String SQL_DELETE_EXERCISE_MUSCLE_GROUP =
            "DROP TABLE IF EXISTS " + ExerciseMuscleGroup.TABLE_NAME;

    /* EXERCISE_MUSCLE_TYPE DDL */
    private static final String SQL_CREATE_EXERCISE_MUSCLE_TYPE =
            "CREATE TABLE " + ExerciseMuscleType.TABLE_NAME + " (" +
                    ExerciseMuscleType.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    ExerciseMuscleType.COLUMN_NAME_EXERCISE+ " INTEGER," +
                    ExerciseMuscleType.COLUMN_NAME_MUSCLE_TYPE + " INTEGER," +
                    "FOREIGN KEY(" + ExerciseMuscleType.COLUMN_NAME_EXERCISE + ") "+
                    "REFERENCES " + Exercise.TABLE_NAME + "(" + Exercise.COLUMN_NAME_ID + ")," +
                    "FOREIGN KEY(" + ExerciseMuscleType.COLUMN_NAME_MUSCLE_TYPE + ") "+
                    "REFERENCES " + MuscleType.TABLE_NAME + "(" + MuscleType.COLUMN_NAME_ID + "))";


    private static final String SQL_DELETE_EXERCISE_MUSCLE_TYPE =
            "DROP TABLE IF EXISTS " + ExerciseMuscleType.TABLE_NAME;

    /* EXERCISE_IN_PLAN DDL */
    private static final String SQL_CREATE_EXERCISE_IN_PLAN =
            "CREATE TABLE " + PlanExercises.TABLE_NAME + " (" +
                    PlanExercises.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    PlanExercises.COLUMN_NAME_PLAN + " INTEGER," +
                    PlanExercises.COLUMN_NAME_EXERCISE + " INTEGER," +
                    PlanExercises.COLUMN_NAME_SETS + " INTEGER," +
                    PlanExercises.COLUMN_NAME_REPS + " INTEGER," +
                    PlanExercises.COLUMN_NAME_WEIGHT + " INTEGER," +
                    PlanExercises.COLUMN_NAME_SUBPLAN + " INTEGER," +
                    "FOREIGN KEY(" + PlanExercises.COLUMN_NAME_PLAN + ") "+
                    "REFERENCES " + Plan.TABLE_NAME + "(" + Plan.COLUMN_NAME_ID + ")," +
                    "FOREIGN KEY(" + PlanExercises.COLUMN_NAME_EXERCISE + ") "+
                    "REFERENCES " + Exercise.TABLE_NAME + "(" + Exercise.COLUMN_NAME_ID + "))";


    private static final String SQL_DELETE_EXERCISE_IN_PLAN =
            "DROP TABLE IF EXISTS " + PlanExercises.TABLE_NAME;

    /* MUSCLE_GROUP DDL */
    private static final String SQL_CREATE_MUSCLE_GROUP =
            "CREATE TABLE " + MuscleGroup.TABLE_NAME + " (" +
                    MuscleGroup.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    MuscleGroup.COLUMN_NAME_NAME + " TEXT)";


    private static final String SQL_DELETE_MUSCLE_GROUP =
            "DROP TABLE IF EXISTS " + MuscleGroup.TABLE_NAME;

    /* MUSCLE_TYPE DDL */
    private static final String SQL_CREATE_MUSCLE_TYPE =
            "CREATE TABLE " + MuscleType.TABLE_NAME + " (" +
                    MuscleType.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    MuscleType.COLUMN_NAME_NAME + " TEXT)";


    private static final String SQL_DELETE_MUSCLE_TYPE =
            "DROP TABLE IF EXISTS " + MuscleType.TABLE_NAME;

    /* PLAN DDL */
    private static final String SQL_CREATE_PLAN =
            "CREATE TABLE " + Plan.TABLE_NAME + " (" +
                    Plan.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    Plan.COLUMN_NAME_USER + " INTEGER," +
                    Plan.COLUMN_NAME_NAME + " TEXT," +
                    Plan.COLUMN_NAME_START + " DATETIME," +
                    Plan.COLUMN_NAME_END + " DATETIME," +
                    Plan.COLUMN_NAME_TYPE + " DATETIME," +
                    Plan.COLUMN_NAME_STATUS + " BOOLEAN," +
                    "FOREIGN KEY(" + Plan.COLUMN_NAME_USER + ") "+
                    "REFERENCES " + DeviceUser.TABLE_NAME + "(" + DeviceUser.COLUMN_NAME_ID + ")," +
                    "FOREIGN KEY(" + Plan.COLUMN_NAME_TYPE + ") "+
                    "REFERENCES " + PlanType.TABLE_NAME + "(" + PlanType.COLUMN_NAME_ID + "))";


    private static final String SQL_DELETE_PLAN =
            "DROP TABLE IF EXISTS " + Plan.TABLE_NAME;

    /* Plan_TYPE DDL */
    private static final String SQL_CREATE_PLAN_TYPE =
            "CREATE TABLE " + PlanType.TABLE_NAME + " (" +
                    PlanType.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    PlanType.COLUMN_NAME_NAME + " TEXT)";


    private static final String SQL_DELETE_PLAN_TYPE =
            "DROP TABLE IF EXISTS " + PlanType.TABLE_NAME;


    /* DEVICE_USERS DDL */
    private static final String SQL_CREATE_DEVICE_USERS =
            "CREATE TABLE " + DeviceUser.TABLE_NAME + " (" +
            DeviceUser.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
            DeviceUser.COLUMN_NAME_EMAIL + " TEXT PRIMARY KEY," +
            DeviceUser.COLUMN_NAME_NAME + " TEXT," +
            DeviceUser.COLUMN_NAME_DOB + " DATE," +
            DeviceUser.COLUMN_NAME_GENDER + " BOOLEAN," +
            DeviceUser.COLUMN_NAME_HEIGHT + " INTEGER," +
            DeviceUser.COLUMN_NAME_WEIGHT + " REAL," +
            DeviceUser.COLUMN_NAME_PROFILE_PIC + " TEXT)";

    private static final String SQL_DELETE_DEVICE_USERS =
            "DROP TABLE IF EXISTS " + DeviceUser.TABLE_NAME;


    /* PLAN DDL */
    private static final String SQL_CREATE_USER_WEIGHT_HIST =
            "CREATE TABLE " + UserWeightHist.TABLE_NAME + " (" +
                    UserWeightHist.COLUMN_NAME_ID + " INTEGER PRIMARY KEY," +
                    UserWeightHist.COLUMN_NAME_USER + " INTEGER," +
                    UserWeightHist.COLUMN_NAME_WEIGHT + " REAL," +
                    UserWeightHist.COLUMN_NAME_DATE + " DATETIME," +
                    "FOREIGN KEY(" + UserWeightHist.COLUMN_NAME_USER + ") "+
                    "REFERENCES " + DeviceUser.TABLE_NAME + "(" + DeviceUser.COLUMN_NAME_ID + "))";


    private static final String SQL_DELETE_USER_WEIGHT_HIST =
            "DROP TABLE IF EXISTS " + UserWeightHist.TABLE_NAME;


    /* SETS DDL */
    /*
    private static final String SQL_CREATE_SETS =
            "CREATE TABLE " + Sets.TABLE_NAME + " (" +
            Sets.COLUMN_NAME_HIST + " INTEGER," +
            Sets.COLUMN_NAME_NUM + " INTEGER," +
            Sets.COLUMN_NAME_REPS + " INTEGER," +
            Sets.COLUMN_NAME_INT + " FLOAT(3,1)," +
            Sets.COLUMN_NAME_WEIGHT + " INTEGER," +
            Sets.COLUMN_NAME_REST + " TIME," +
            Sets.COLUMN_NAME_AVGREP + " TIME," +
            Sets.COLUMN_NAME_REPDEV + " FLOAT," +
            "FOREIGN KEY(" + Sets.COLUMN_NAME_HIST + ") "+
            "REFERENCES " + ExerciseHistory.TABLE_NAME + "(" + ExerciseHistory.COLUMN_NAME_ID + ")," +
            "PRIMARY KEY(" + Sets.COLUMN_NAME_HIST + "," + Sets.COLUMN_NAME_NUM + "))";

    private static final String SQL_DELETE_SETS =
            "DROP TABLE IF EXISTS " + Sets.TABLE_NAME;
    */

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Muscle.db";

    public MuscleDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_EXERCISE);
        db.execSQL(SQL_CREATE_PLAN);
        db.execSQL(SQL_CREATE_EXERCISE_HISTORY);
        db.execSQL(SQL_CREATE_MUSCLE_GROUP);
        db.execSQL(SQL_CREATE_MUSCLE_TYPE);
        db.execSQL(SQL_CREATE_EXERCISE_MUSCLE_GROUP);
        db.execSQL(SQL_CREATE_EXERCISE_MUSCLE_TYPE);
        db.execSQL(SQL_CREATE_EXERCISE_IN_PLAN);
        db.execSQL(SQL_CREATE_PLAN_TYPE);
        db.execSQL(SQL_CREATE_DEVICE_USERS);
        db.execSQL(SQL_CREATE_USER_WEIGHT_HIST);

    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_USER_WEIGHT_HIST);
        db.execSQL(SQL_DELETE_DEVICE_USERS);
        db.execSQL(SQL_DELETE_PLAN_TYPE);
        db.execSQL(SQL_DELETE_EXERCISE_IN_PLAN);
        db.execSQL(SQL_DELETE_EXERCISE_MUSCLE_TYPE);
        db.execSQL(SQL_DELETE_EXERCISE_MUSCLE_GROUP);
        db.execSQL(SQL_DELETE_MUSCLE_TYPE);
        db.execSQL(SQL_DELETE_MUSCLE_GROUP);
        db.execSQL(SQL_DELETE_EXERCISE_HISTORY);
        db.execSQL(SQL_DELETE_PLAN);
        db.execSQL(SQL_DELETE_EXERCISE);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
