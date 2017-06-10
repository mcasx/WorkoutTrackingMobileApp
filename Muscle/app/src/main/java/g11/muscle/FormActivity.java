package g11.muscle;

import android.app.DatePickerDialog;
import android.app.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
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

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;


public class FormActivity extends AppCompatActivity {
    private final int PICK_IMAGE_REQUEST = 1;
    private static final String ERROR_MSG = "Please try to reconnect";

    TextInputLayout height_input_layout;
    TextInputLayout weight_input_layout;
    TextInputLayout name_input_layout;

    String height;
    String weight;
    String name;
    String dob;
    String gender;
    String email;
    String profile_pic;
    String context;
    //Boolean imageChosen = false;
    TextView skipButton;
    Button saveButton;
    ImageView pickImage;
    Button pickDoB;
    RadioGroup radioGenderGroup;
    TextView dobInput;
    TextView viewDob;
    //ImageView imgView;
    ProgressBar progressBar;
    Uri picURI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);

        height_input_layout = (TextInputLayout) findViewById(R.id.height_input_layout);
        weight_input_layout = (TextInputLayout) findViewById(R.id.weight_input_layout);
        name_input_layout = (TextInputLayout) findViewById(R.id.name_input_layout);

        saveButton = ((Button) findViewById(R.id.save_button));
        pickImage = ((ImageView) findViewById(R.id.pick_profile_img));
        //imgView = ((ImageView)findViewById(R.id.userCommentImage));
        pickDoB = ((Button) findViewById(R.id.button_bod_picker));
        dobInput = ((TextView) findViewById(R.id.textView));
        viewDob = ((TextView) findViewById(R.id.textViewDob));
        progressBar = (ProgressBar) findViewById(R.id.progressBar2);
        radioGenderGroup = (RadioGroup) findViewById(R.id.radioSex);

        progressBar.setVisibility(View.GONE);

        // get email
        Intent in= getIntent();
        Bundle b = in.getExtras();
        email = (String) b.get("email");
        context = (String) b.get("context");
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
        CropImage.activity(picURI)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setFixAspectRatio(true)
                .setInitialCropWindowPaddingRatio(0)
                .start( this);
    }


    public void onClickSave(View view) {

        set_progressBar_visibility(View.VISIBLE);

        height  = ((TextInputEditText) findViewById(R.id.height_input)).getText().toString();
        weight = ((TextInputEditText) findViewById((R.id.weight_input))).getText().toString();
        name = ((TextInputEditText) findViewById((R.id.name_input))).getText().toString().trim();
        dob = ((Button) findViewById((R.id.button_bod_picker))).getText().toString();


        if(!validFields())
            return;

        // get selected radio button from radioGroup
        int selectedId = radioGenderGroup.getCheckedRadioButtonId();
        gender = Integer.toString(((RadioButton) findViewById(selectedId)).getText().equals("Female") ? 1:0 );

        String addUserUrl = DBConnect.serverURL + "/update_user_char";

        StringRequest stringRequest = new StringRequest(Request.Method.POST, addUserUrl,
                new Response.Listener<String>() {
                    public void onResponse(String response){

                        if(response.equals("User " + email + " characteristics updated")) {

                            // Intent intent = new Intent(RegisterActivity.this, FormActivity.class);
                            // Later in development (like tomorrow <- xD lol nope)
                            // it will redirect to a page where user specifies more parameters
                            // For now it takes the user to the PickExerciseActivity
                            if(context.equals("register")) {
                                Intent intent = new Intent(FormActivity.this, HomeActivity.class);
                                intent.putExtra("email", email);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intent);
                                finish();
                            }
                        }

                        else{

                                AlertDialog alertDialog = new AlertDialog.Builder(FormActivity.this).create();
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

                        AlertDialog alertDialog = new AlertDialog.Builder(FormActivity.this).create();
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


                if(!TextUtils.isEmpty(name))
                    params.put("name", name);

                if(!TextUtils.isEmpty(height))
                    params.put("height", height);

                if(!TextUtils.isEmpty(weight))
                    params.put("weight", String.format("%.2f",Double.parseDouble(weight)));

                if(!TextUtils.isEmpty(dob)) {
                    Log.i("HELP",dob);
                    params.put("date_of_birth", dob);
                }
                else
                    Log.i("HELP","nodob");

                if(!TextUtils.isEmpty(profile_pic))
                    params.put("profile_pic",profile_pic);

                params.put("email", email);

                params.put("gender", gender);

                return params;
            }
        };

        VolleyProvider.getInstance(this).addRequest(stringRequest);
    }

    public void onClickPickDate(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                try {
                    Uri resultUri = result.getUri();

                    Picasso.with(getApplication()).load(resultUri).into(pickImage);

                    Bitmap user_img = MediaStore.Images.Media.getBitmap(getContentResolver(), resultUri);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    user_img.compress(Bitmap.CompressFormat.JPEG, 80, stream);
                    byte [] byte_arr = stream.toByteArray();
                    profile_pic = Base64.encodeToString(byte_arr, Base64.DEFAULT);


                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }

        }
    }

    private boolean validFields(){

        name = ((TextInputEditText) findViewById((R.id.name_input))).getText().toString().trim();

        weight_input_layout.setError(null);
        height_input_layout.setError(null);
        name_input_layout.setError(null);

        if(!TextUtils.isEmpty(weight)) {
            try {
                Double weight_value = Double.parseDouble(weight);
                if(weight_value > 300)
                    return false;

            } catch (NumberFormatException e) {
                weight_input_layout.requestFocus();
                set_progressBar_visibility(View.GONE);
                weight_input_layout.setError("Invalid weight");
                return false;
            }
        }
        else{
            weight_input_layout.requestFocus();
            set_progressBar_visibility(View.GONE);
            weight_input_layout.setError("Invalid weight");
            return false;
        }

        if(!TextUtils.isEmpty(height)){
            if(Integer.parseInt(height) >= 400) {
                height_input_layout.requestFocus();
                height_input_layout.setError("Invalid height");
                set_progressBar_visibility(View.GONE);
                return false;
            }
        }
        if(TextUtils.isEmpty(name)){
            name_input_layout.requestFocus();
            name_input_layout.setError("Please enter name");
            set_progressBar_visibility(View.GONE);
            return false;
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
            //imgView.setVisibility(View.VISIBLE);
            pickDoB.setVisibility(View.VISIBLE);
            viewDob.setVisibility(View.VISIBLE);
            dobInput.setVisibility(View.VISIBLE);
            name_input_layout.setVisibility(View.VISIBLE);
            radioGenderGroup.setVisibility(View.VISIBLE);
        }

        else{
            progressBar.setVisibility(View.VISIBLE);
            height_input_layout.setVisibility(View.GONE);
            weight_input_layout.setVisibility(View.GONE);
            saveButton.setVisibility(View.GONE);
            pickImage.setVisibility(View.GONE);
            pickDoB.setVisibility(View.GONE);
            viewDob.setVisibility(View.GONE);
            dobInput.setVisibility(View.GONE);
            name_input_layout.setVisibility(View.GONE);
            radioGenderGroup.setVisibility(View.GONE);
        }
    }
}
