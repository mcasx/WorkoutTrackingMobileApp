package g11.muscle;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;

public class RegisterActivity extends AppCompatActivity {
    //TODO: shared preferences;
    private String email;
    private String password;
    private String repeatedPassword;

    ProgressBar progressBar;
    TextInputLayout email_layout;
    TextInputLayout password_input_layout;
    TextInputLayout password_confirmation_layout;
    Button signUpButton;
    View note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // needed to set visibility
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        email_layout = (TextInputLayout) findViewById(R.id.email_input_layout);
        password_input_layout = (TextInputLayout) findViewById(R.id.password_input_layout);
        password_confirmation_layout = (TextInputLayout) findViewById(R.id.password_confirmation_layout);
        signUpButton = (Button) findViewById(R.id.sign_up_button);
        note = findViewById(R.id.textView2);

        progressBar.setVisibility(View.GONE);

        // all this code to submit when done button is pressed fcn java man
        final TextInputEditText password_confirmation = (TextInputEditText)findViewById(R.id.password_confirmation);
        password_confirmation.setOnEditorActionListener(
                new TextInputEditText.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_SEARCH ||
                                actionId == EditorInfo.IME_ACTION_DONE ||
                                event.getAction() == KeyEvent.ACTION_DOWN &&
                                        event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {

                            InputMethodManager inputManager = (InputMethodManager)
                                    getSystemService(Context.INPUT_METHOD_SERVICE);

                            inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                                    InputMethodManager.HIDE_NOT_ALWAYS);

                            onClickSignUp(v.getRootView());
                            password_confirmation.setImeOptions(EditorInfo.IME_ACTION_DONE);


                            return true;
                        }
                        return false;
                    }
                });
    }

    public void onClickSignUp(View view){

        TextInputEditText email_input, password_input, repPass_input;

        email  = ((TextInputEditText) findViewById(R.id.email_input)).getText().toString().trim();

        password = ((TextInputEditText) findViewById((R.id.password_input))).getText().toString();

        repeatedPassword = ((TextInputEditText) findViewById((R.id.password_confirmation))).getText().toString();

        set_progressBar_visibility(View.VISIBLE);

        if(!validFields())
            return;

        String addUserUrl = DBConnect.serverURL + "/add_user";
        //Create the list items through a request
        //MuscleDbHelper dbHelper= new MuscleDbHelper(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST,addUserUrl,
                new Response.Listener<String>() {
                    public void onResponse(String response){
                        //int id;

                        //JSONObject jsonObject = new JSONObject(response);
                        //String status = (String) jsonObject.get("status");
                        if(response.equals("User added")) {
                            //int id = (Integer) jsonObject.get("id");
                            Intent intent = new Intent(RegisterActivity.this, FormActivity.class);
                            //intent.putExtra("id", id);
                            // email is kept for now
                            intent.putExtra("email", email);
                            intent.putExtra("context", "register");
                            startActivity(intent);
                            finish();
                        }
                        else if(response.equals("User already Registered")){
                            email_layout.setError("Email already in use");
                            set_progressBar_visibility(View.GONE);
                        }
                        else{
                            AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                            alertDialog.setTitle("Error");
                            alertDialog.setMessage(response);
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

                        AlertDialog alertDialog = new AlertDialog.Builder(RegisterActivity.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        // Please connect your device to the Internet and try again
                        alertDialog.setMessage(error.toString());
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
            // use params are specified here
            // DoB, height, gender and weight are specified later, for now they have default values
            // effin not nulls
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("email", email);
                params.put("name", email);
                params.put("password", password);
                // in this step we put name = email, because fkn not nulls

                params.put("date_of_birth", getDefaultAgeDoB());
                // we literally assume the gender
                params.put("gender", "1");
                // i assume it's in centimeters, could be height and weight were mixed tho
                params.put("height", "170");
                params.put("weight", "70.0");
                return params;
            }
        };


        VolleyProvider.getInstance(this).addRequest(stringRequest);

    }

    private void set_progressBar_visibility(int view){

        if(View.GONE == view){
            progressBar.setVisibility(View.GONE);
            email_layout.setVisibility(View.VISIBLE);
            password_input_layout.setVisibility(View.VISIBLE);
            password_confirmation_layout.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
            note.setVisibility(View.VISIBLE);
        }

        else{
            progressBar.setVisibility(View.VISIBLE);
            email_layout.setVisibility(View.GONE);
            password_input_layout.setVisibility(View.GONE);
            password_confirmation_layout.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
        }
    }

    private boolean validFields(){

        // reset fields
        email_layout.setError(null);
        password_input_layout.setError(null);
        password_confirmation_layout.setError(null);

        if(email.equals("")){
            email_layout.setError("Please insert your Muscle account email address!");
            set_progressBar_visibility(View.GONE);
             return false;
        }

        if(!isValidEmail(email)){
            email_layout.setError("Invalid Email");
            set_progressBar_visibility(View.GONE);
            return false;
        }

        if(password.length() < 6){
            password_input_layout.setError("Invalid Password!");
            set_progressBar_visibility(View.GONE);
            return false;
        }

        if(password.length() < 6){
            password_input_layout.setError("Invalid Password!");
            set_progressBar_visibility(View.GONE);
            return false;
        }

        if(repeatedPassword.equals("")){
            password_confirmation_layout.setError("Please confirm your password!");
            set_progressBar_visibility(View.GONE);
            return false;
        }

        if(!repeatedPassword.equals(password)) {
            password_confirmation_layout.setError("Passwords don't match!");
            set_progressBar_visibility(View.GONE);
            return false;
        }
        return true;
    }

    private final static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }

    public String getDefaultAgeDoB(){
        String pattern = "dd-MM-yyyy";
        Calendar cal = Calendar.getInstance();
        Date today = cal.getTime();
        cal.add(Calendar.YEAR, -25); // get date minus 25 years
        Date age = cal.getTime();
        SimpleDateFormat formatter = new SimpleDateFormat(pattern);
        String mysqlDateString = formatter.format(age);
        return mysqlDateString;
    }

    public void FormIntent(View view) {
        Intent intent = new Intent(RegisterActivity.this, FormActivity.class);
        intent.putExtra("id", 10);
        intent.putExtra("email", "ola@ua.pt");
        SharedPreferences sp = getSharedPreferences("UserData", 0);
        sp.edit().putString("email", email).apply();
        startActivity(intent);
    }

    @Override
    public void onBackPressed()
    {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    }
