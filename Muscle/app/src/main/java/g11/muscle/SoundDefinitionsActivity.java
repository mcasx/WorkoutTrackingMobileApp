package g11.muscle;

import android.content.DialogInterface;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import g11.muscle.Classes.Globals;

public class SoundDefinitionsActivity extends AppCompatActivity {

    //GUI
    private Spinner langSpinner;
    private RadioGroup radioGroup;
    private RadioButton radioMale, radioFemale, radioWater, radioSetVoice;
    private CheckBox soundsEnable,soundVoiceEnable, soundPopEnable, repSoundEnable,setsSoundEnable;

    //global variables
    private Globals glb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sound_definitions);
        setTitle("Sound Settings");

        glb = Globals.getInstance();

        // LANGUAGES
        langSpinner = (Spinner) findViewById(R.id.langSpinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,glb.getSoundLanguages());
        langSpinner.setAdapter(adapter);

        // REP SOUNDS
        repSoundEnable = (CheckBox) findViewById(R.id.checkRepsSound);
        repSoundEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (!isChecked)
                {
                    soundPopEnable.setEnabled(false);
                    soundVoiceEnable.setEnabled(false);

                    radioMale.setEnabled(false);
                    radioFemale.setEnabled(false);
                }
                else{
                    soundPopEnable.setEnabled(true);
                    soundVoiceEnable.setEnabled(true);

                    radioMale.setEnabled(true);
                    radioFemale.setEnabled(true);
                }
            }
        });
        radioMale = (RadioButton) findViewById(R.id.radioMale);
        radioFemale = (RadioButton) findViewById(R.id.radioFemale);

        soundPopEnable = (CheckBox) findViewById(R.id.soundPopEnable);
        soundVoiceEnable = (CheckBox) findViewById(R.id.soundVoiceEnable);
        soundVoiceEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (!isChecked)
                {
                    radioMale.setEnabled(false);
                    radioFemale.setEnabled(false);
                }
                else{
                    radioMale.setEnabled(true);
                    radioFemale.setEnabled(true);
                }
            }
        });

        // SET SOUNDS
        setsSoundEnable = (CheckBox) findViewById(R.id.checkSetSound);
        setsSoundEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (!isChecked)
                {
                    radioWater.setEnabled(false);
                    radioSetVoice.setEnabled(false);
                }
                else{
                    radioWater.setEnabled(true);
                    radioSetVoice.setEnabled(true);
                }
            }
        });
        radioWater = (RadioButton) findViewById(R.id.radioWater);
        radioSetVoice = (RadioButton) findViewById(R.id.radioSetVoice);

        // GLOBAL SOUNDS
        soundsEnable = (CheckBox) findViewById(R.id.soundsEnable);
        soundsEnable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
        {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
            {
                if (!isChecked)
                {
                    repSoundEnable.setChecked(false);
                    setsSoundEnable.setChecked(false);

                    repSoundEnable.setEnabled(false);
                    setsSoundEnable.setEnabled(false);

                    soundPopEnable.setEnabled(false);
                    soundVoiceEnable.setEnabled(false);
                }
                else{
                    repSoundEnable.setChecked(true);
                    setsSoundEnable.setChecked(true);

                    repSoundEnable.setEnabled(true);
                    setsSoundEnable.setEnabled(true);

                    soundPopEnable.setEnabled(true);
                    soundVoiceEnable.setEnabled(true);
                }
            }
        });

        if(glb.getSoundGender() == 0)
            radioMale.setChecked(true);
        else
            radioFemale.setChecked(true);

        soundPopEnable.setChecked(glb.getSoundPopEnable());
        soundVoiceEnable.setChecked(glb.getSoundVoiceEnable());

        if(glb.getSoundRepEnable())
            repSoundEnable.setChecked(true);
        else{
            defaultRepSoundConf();
            // repSoundEnable is already false
        }

        if(glb.getSetSoundEnabled()) {
            if (glb.getSetSoundEnable() == 1)
                radioWater.setChecked(true);
            else
                radioSetVoice.setChecked(true);

            setsSoundEnable.setChecked(true);
        }else{
            defaultSetSoundConf();
            // setsSoundEnable is already false
        }

        if( repSoundEnable.isChecked() && setsSoundEnable.isChecked()) // all sounds checked
            soundsEnable.setChecked(true);
        else
            soundsEnable.setChecked(false);

    }

    public void onClickCredits(View v){
        AlertDialog alertDialog = new AlertDialog.Builder(SoundDefinitionsActivity.this).create();
        alertDialog.setTitle("Licenses");
        //"Please connect your device to the Internet and try again")
        alertDialog.setMessage("Female voice courtesy of Corsica_S at \nhttp://www.freesound.org/people/Corsica_S \n" +
                               "\nPacks: \n    -\"Numbers 0-20\" \n    -\"Counting to 20\" \n    -\"Texas Hold 'Em\"\n" +
                               "\nUnder Attribution License \n" +
                               "https://creativecommons.org/licenses/by/3.0/\n");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void defaultRepSoundConf(){
        soundPopEnable.setEnabled(false);
        soundVoiceEnable.setEnabled(false);

        radioMale.setEnabled(false);
        radioFemale.setEnabled(false);
    }

    public void defaultSetSoundConf(){
        if(!(radioSetVoice.isChecked() || radioWater.isChecked()))
            radioSetVoice.setChecked(true);

        radioWater.setEnabled(false);
        radioSetVoice.setEnabled(false);
    }

    @Override
    public void onBackPressed(){
        glb.setSoundLang(langSpinner.getSelectedItem().toString().substring(0,3).toLowerCase());

        //REP SOUNDS
        if(repSoundEnable.isChecked()) {
            glb.setSoundPopEnable(soundPopEnable.isChecked());
            glb.setSoundVoiceEnable(soundVoiceEnable.isChecked());
            glb.setSoundRepEnable(true);
        }
        else{
            glb.setSoundRepEnable(false);
        }

        if(radioMale.isChecked())
            glb.setSoundGender(0);
        else
            glb.setSoundGender(1);


        //SET SOUNDS
        if(setsSoundEnable.isChecked()){
            if(radioWater.isChecked())
                glb.setSetSoundEnable(1);
            else
                glb.setSetSoundEnable(2);
        }
        else
            glb.setSetSoundEnable(0);

        super.onBackPressed();
    }
}
