package g11.muscle.Classes;

/**
 * Created by Xdye on 11/06/2017.
 */

public class Globals{
    private static Globals instance;

    // Global variable
    private String soundLang;
    private String[] soundLanguages;
    private int soundGender; // 0 - male | 1 - female
    private boolean soundRepEnable;
    private boolean soundPopEnable;
    private boolean soundVoiceEnable;

    private int soundSetEnable;

    // Restrict the constructor from being instantiated
    private Globals(){
        soundLanguages = new String[] {"English"};
        soundLang = "eng";
        //rep sounds
        soundRepEnable = true;
        soundPopEnable = true;
        soundVoiceEnable = true;
        soundGender = 1;
        //set sounds
        soundSetEnable = 2; // 0 - disabled, 1 - water drop , 2 - voice
    }

    public static synchronized Globals getInstance(){
        if(instance==null){
            instance=new Globals();
        }
        return instance;
    }

    public String[] getSoundLanguages(){
        return this.soundLanguages;
    }

    public void setSoundLang(String lan){
        this.soundLang=lan;
    }

    public String getSoundLang(){
        return this.soundLang;
    }

    public void setSoundGender(int gender){
        this.soundGender = gender;
    }

    public int getSoundGender(){
        return this.soundGender;
    }

    // REP SOUNDS
    public void setSoundRepEnable(boolean enable) { this.soundRepEnable = enable; }

    public boolean getSoundRepEnable() { return this.soundRepEnable; }

    public void setSoundPopEnable(boolean enable) { this.soundPopEnable = enable; }

    public boolean getSoundPopEnable() { return this.soundPopEnable; }

    public void setSoundVoiceEnable(boolean enable){
        this.soundVoiceEnable = enable;
    }

    public boolean getSoundVoiceEnable(){
        return this.soundVoiceEnable;
    }

    // SET SOUNDS
    public void setSetSoundEnable(int mode) {this.soundSetEnable = mode; }

    public int getSetSoundEnable() {return this.soundSetEnable; }

    public boolean getSetSoundEnabled() { return this.soundSetEnable != 0; }
}
