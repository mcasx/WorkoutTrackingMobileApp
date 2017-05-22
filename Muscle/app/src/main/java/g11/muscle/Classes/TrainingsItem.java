package g11.muscle.Classes;

/**
 * Created by Xdye on 22/05/2017.
 */

public class TrainingsItem {
    private int id;
    private String name;

    public TrainingsItem (int id, String name){
        this.id = id;
        this.name = name;
    }

    public String getIdStr(){
        return String.valueOf(id);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return "\n#############\nID: " + id + "\nName: " + name;
    }
}
