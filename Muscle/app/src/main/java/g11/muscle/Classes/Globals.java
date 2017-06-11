package g11.muscle.Classes;

/**
 * Created by Xdye on 11/06/2017.
 */

public class Globals{
    private static Globals instance;

    // Global variable
    private boolean soundEnable;
    private String soundLang;
    private int soundGender; // 0 - male | 1 - female

    // Restrict the constructor from being instantiated
    private Globals(){
        soundEnable = true;
        soundLang = "eng";
        soundGender = 1;
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }

    public void setSoundLang(String lan){
        this.soundLang=lan;
    }
    public String getSoundLang(){
        return this.soundLang;
    }

    public void setSoundEnable(boolean enable){
        this.soundEnable = enable;
    }

    public boolean getSoundEnable(){
        return this.soundEnable;
    }

    public void setSoundGender(int gender){
        this.soundGender = gender;
    }

    public int getSoundGender(){
        return this.soundGender;
    }
}
