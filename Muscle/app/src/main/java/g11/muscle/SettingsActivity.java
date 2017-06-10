package g11.muscle;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import g11.muscle.Classes.MuscleProgressItem;
import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;


public class SettingsActivity  extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 1;
    private static final String ERROR_MSG = "Please try to reconnect";


    TextInputLayout height_input_layout;
    TextInputLayout weight_input_layout;
    //excelente nome
    TextView name_output_layout;

    String height;
    String weight;
    String email;
    String context;

    Integer retrievedWeight;
    String profile_pic;
    Integer retrievedHeight;
    Boolean gender;
    String name;


    //Boolean imageChosen = false;
    Button saveButton;
    Button fitbitButton;
    ImageView pickImage;
    //ImageView imgView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        //GUI elements
        height_input_layout = (TextInputLayout) findViewById(R.id.height_input_layout);
        weight_input_layout = (TextInputLayout) findViewById(R.id.weight_input_layout);
        pickImage = ((ImageView) findViewById(R.id.pick_profile_img));
        name_output_layout = ((TextView) findViewById(R.id.textView5));
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        saveButton = ((Button) findViewById(R.id.save_button));
        fitbitButton = ((Button) findViewById(R.id.fitbit_button));

        // get email
        Intent in= getIntent();
        Bundle b = in.getExtras();
        email = (String) b.get("email");
        context = (String) b.get("context");

        //get user settings
        String getUserUrl = DBConnect.serverURL + "/get_user_profile";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, getUserUrl,
                new Response.Listener<String>() {
                    public void onResponse(String response){
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            gender = jsonObject.getInt("Gender") == 1;
                            Log.i("TG",Boolean.toString(gender));
                            retrievedHeight = jsonObject.getInt("Height");
                            retrievedWeight = jsonObject.getInt("Weight");
                            name = jsonObject.getString("Name");

                            name_output_layout.setText(name);
                            height_input_layout.getEditText().setText(height);
                            weight_input_layout.getEditText().setText(weight);
                            profile_pic = jsonObject.getString("Profile_image");

                            if (profile_pic != null && !profile_pic.equals("null")) {

                                byte[] imageBytes = Base64.decode(profile_pic, Base64.DEFAULT);
                                Bitmap user_img = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
                                ImageView profile = ((ImageView) findViewById(R.id.pick_profile_img));
                                profile.setImageBitmap(user_img);
                            }
                            Log.i("TG","howdy partener");
                            set_progressBar_visibility(View.GONE);

                        }catch (JSONException e2){
                            e2.printStackTrace();
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        // Please connect your device to the Internet and try again
                        alertDialog.setMessage(ERROR_MSG);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        set_progressBar_visibility(View.GONE);
                    }
                }
        ){
            // user params are specified here
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();

                params.put("email", email);

                return params;
            }
        };

        VolleyProvider.getInstance(this).addRequest(stringRequest);

        //get_gender(email);

        final TextInputEditText weight_input = ((TextInputEditText) findViewById(R.id.weight_input));
        weight_input.addTextChangedListener(new TextWatcher() {
            int len=0;
            @Override
            public void afterTextChanged(Editable s) {

                String weight_in = weight_input.getText().toString();
                if(s.length()>=3 && len <s.length() && !weight_in.contains("."))
                    s.insert(3,".");

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

                String str = weight_input.getText().toString();
                len = str.length();
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });
    }

    public void onClickPickImage(View view) {
        Intent pickPhotoIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult( pickPhotoIntent,PICK_IMAGE_REQUEST);
    }

    public void onClickSave(View view) {

        set_progressBar_visibility(View.VISIBLE);

        height  = ((TextInputEditText) findViewById(R.id.height_input)).getText().toString();
        weight = ((TextInputEditText) findViewById((R.id.weight_input))).getText().toString();

        if(!validFields())
            return;

        // get selected radio button from radioGroup

        String addUserUrl = DBConnect.serverURL + "/update_user_char";

        StringRequest saveRequest = new StringRequest(Request.Method.POST, addUserUrl,
                new Response.Listener<String>() {
                    public void onResponse(String response){

                        if(response.equals("User " + email + " characteristics updated")) {

                            // Intent intent = new Intent(RegisterActivity.this, FormActivity.class);
                            // Later in development (like tomorrow <- xD lol nope)
                            // it will redirect to a page where user specifies more parameters
                            // For now it takes the user to the PickExerciseActivity

                            Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                            intent.putExtra("email", email);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();

                        }

                        else{

                            AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                            alertDialog.setTitle("Error");
                            // Please connect your device to the Internet and try again
                            alertDialog.setMessage(response.toString());
                            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                            set_progressBar_visibility(View.GONE);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        AlertDialog alertDialog = new AlertDialog.Builder(SettingsActivity.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        // Please connect your device to the Internet and try again
                        alertDialog.setMessage(ERROR_MSG);
                        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                        set_progressBar_visibility(View.GONE);
                    }
                }
        ){
            // user params are specified here
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();

                if(!TextUtils.isEmpty(height))
                    params.put("height", height);

                if(!TextUtils.isEmpty(weight))
                    params.put("weight", String.format("%.2f",Double.parseDouble(weight)));


                if(!TextUtils.isEmpty(profile_pic))
                    params.put("profile_pic",profile_pic);

                params.put("email", email);
                //Log.i("HELP",gender ? "1" : "0");

                params.put("gender", gender ? "1" : "0");

                return params;
            }
        };

        VolleyProvider.getInstance(this).addRequest(saveRequest);
    }

    public void onClickFitbit(View view) {
        String inURL= "https://www.fitbit.com/oauth2/authorize?response_type=code";
        inURL += "&client_id=228KV8";
        inURL += "&redirect_uri=https%3A%2F%2F138.68.158.127%2Fadd_fitbit_user";
        inURL += "&scope=activity%20heartrate%20profile%20weight";
        inURL += "&expires_in=31536000";
        inURL += "&state=" + getSharedPreferences("UserData",0).getString("email", null);

        Intent browse = new Intent( Intent.ACTION_VIEW , Uri.parse( inURL ) );

        startActivity( browse );
    }

    public void onClickPickDate(View v) {
        DialogFragment newFragment = new FormActivity.DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            //Bitmap user_img = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            //ImageView profile_pic = ((ImageView) findViewById(R.id.pick_profile_img));
            //profile_pic.setScaleType(ImageView.ScaleType.FIT_XY);
            //profile_pic.setImageBitmap(user_img);

            Log.i("TG","getting uri");

            Uri uri = data.getData();

            try {
                Log.i("TG","getting image");


                Bitmap user_img = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                Bitmap resized = Bitmap.createScaledBitmap(user_img, 100, 100, true);
                pickImage.setImageBitmap(resized);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                resized.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                byte [] byte_arr = stream.toByteArray();
                profile_pic = Base64.encodeToString(byte_arr, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean validFields(){

        weight_input_layout.setError(null);
        height_input_layout.setError(null);

        if(!TextUtils.isEmpty(weight)) {
            try {
                Double.parseDouble(weight);
            } catch (NumberFormatException e) {
                weight_input_layout.requestFocus();
                set_progressBar_visibility(View.GONE);
                weight_input_layout.setError("Invalid weight");
                return false;
            }
        }

        if(!TextUtils.isEmpty(height)){
            if(Integer.parseInt(height) >= 400) {
                height_input_layout.requestFocus();
                height_input_layout.setError("Invalid height");
                set_progressBar_visibility(View.GONE);
                return false;
            }
        }

        return true;
    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            c.add(Calendar.YEAR, -25);
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog dobForm = new DatePickerDialog(getActivity(), this, year, month, day);
            dobForm.getDatePicker().setMaxDate(new Date().getTime());
            // Create a new instance of DatePickerDialog and return it
            return dobForm;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            // Do something with the date chosen by the user
            TextView tv= (TextView) getActivity().findViewById(R.id.button_bod_picker);
            tv.setText(new StringBuilder().append(year).append("/")
                    .append(month).append("/").append(day));
        }
    }

    private void set_progressBar_visibility(int view){

        if(View.GONE == view){
            progressBar.setVisibility(View.GONE);
            height_input_layout.setVisibility(View.VISIBLE);
            weight_input_layout.setVisibility(View.VISIBLE);
            saveButton.setVisibility(View.VISIBLE);
            pickImage.setVisibility(View.VISIBLE);
        }

        else{
            progressBar.setVisibility(View.VISIBLE);
            height_input_layout.setVisibility(View.GONE);
            weight_input_layout.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            pickImage.setVisibility(View.GONE);
        }
    }
}
