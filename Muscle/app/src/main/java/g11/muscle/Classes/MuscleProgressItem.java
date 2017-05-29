package g11.muscle.Classes;

/**
 * Created by xarez on 29-05-2017.
 */

public class MuscleProgressItem {
    private double var;
    private String name;

    public MuscleProgressItem (double var, String name){
        this.var = var;
        this.name = name;
    }

    public double getVar(){ return var; }
    public String getVarStr(){
        return String.valueOf(var);
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString(){
        return "\n#############\nID: " + var + "\nName: " + name;
    }
}
