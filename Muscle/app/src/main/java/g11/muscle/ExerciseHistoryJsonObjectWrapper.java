package g11.muscle;

import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by david on 29-04-2017.
 */

public class ExerciseHistoryJsonObjectWrapper implements Serializable {

    private JSONObject obj;

    public ExerciseHistoryJsonObjectWrapper(JSONObject obj){
        this.obj = obj;
    }
}
