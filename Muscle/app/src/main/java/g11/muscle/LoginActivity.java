package g11.muscle;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import java.util.HashMap;
import java.util.Map;

import g11.muscle.DB.DBConnect;
import g11.muscle.DB.VolleyProvider;

import android.util.Log;

public class LoginActivity extends AppCompatActivity {

    private String             email;
    private String             password;
    private ProgressBar        progressBar;
    private TextInputLayout    email_layout;
    private TextInputLayout    password_layout;
    private Button             signInButton;
    private TextView           signUp;
    private TextView           forgotPass;
    private ImageView          logo;
    private SharedPreferences  sp;

    private static final String ERROR_MSG = "Please try to reconnect";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        progressBar     =      (ProgressBar) findViewById(R.id.progressBar);
        email_layout    =      (TextInputLayout) findViewById(R.id.email_input_layout);
        password_layout =      (TextInputLayout) findViewById(R.id.password_input_layout);
        signInButton    =      (Button) findViewById(R.id.sign_in_button);
        signUp          =      (TextView) findViewById(R.id.sign_up);
        forgotPass      =      (TextView) findViewById(R.id.forgotPass);
        logo            =      (ImageView) findViewById(R.id.logoView);
        sp = getSharedPreferences("UserData", 0);
        if(sp.contains("email")){
            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
            intent.putExtra("email", sp.getString("email", null));
            startActivity(intent);
            finish();
        }
        progressBar.setVisibility(View.GONE);
    }

    //Pressed back on Log in activity
    //Confirm user wants to quit the app
    @Override
    public void onBackPressed() {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Quit Muscle")
                .setMessage("Are you sure you want to close Muscle?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        android.os.Process.killProcess(android.os.Process.myPid());
                        finish();
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

    public void onClickSignIn(View view){

        email = ((EditText) findViewById(R.id.email_input)).getText().toString().trim();
        password = ((EditText) findViewById((R.id.password_input))).getText().toString();
        set_progressBar_visibility(View.VISIBLE);


        if(email.equals("")){
            email_layout.setError("Please insert your Muscle account email address!");
            set_progressBar_visibility(View.GONE);
            return;
        }
        else
            email_layout.setError(null);

        if(!isValidEmail(email)){
            email_layout.setError("Invalid email!");
            set_progressBar_visibility(View.GONE);
            return;
        }
        else
            email_layout.setError(null);


        if(password.length() < 6){
            password_layout.setError("Invalid password!");
            set_progressBar_visibility(View.GONE);
            return;
        }
        else
            password_layout.setError(null);

        String url = DBConnect.serverURL + "/user_login";

        //Create the list items through a request

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        if(response.equals("True")) {

                            Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                            finish();


                            sp.edit().putString("email", email).apply();

                        }
                        else{
                            Log.e("DB CONNECT ERROR",String.valueOf(response));

                            AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                            alertDialog.setTitle("Wrong Credentials");
                            alertDialog.setMessage("Invalid username or password");
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
                        AlertDialog alertDialog = new AlertDialog.Builder(LoginActivity.this).create();
                        alertDialog.setTitle("No Internet Connection");
                        //"Please connect your device to the Internet and try again")
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
            // use params are specified here
            // DoB, height, gender and weight are specified later, for now they have default values
            // effin not nulls
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<>();
                params.put("email", email);
                params.put("password", password);
                return params;
            }
        };

        //queue.add(stringRequest);
        VolleyProvider.getInstance(this).addRequest(stringRequest);

    }

    public void onClickSignUp(View view){

        Intent signUpInt = new Intent(LoginActivity.this, RegisterActivity.class);

        LoginActivity.this.startActivity(signUpInt);

        finish();
    }

    public void onClickForgotPass(View view){

        Intent forgotPassInt = new Intent(LoginActivity.this, ResetPassActivity.class);

        LoginActivity.this.startActivity(forgotPassInt);
    }

    public void set_progressBar_visibility(int view){
        if(View.GONE == view){
            progressBar.setVisibility(View.GONE);
            email_layout.setVisibility(View.VISIBLE);
            password_layout.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            signUp.setVisibility(View.VISIBLE);
            forgotPass.setVisibility(View.VISIBLE);
            //textView.setVisibility(View.VISIBLE);
        }

        else{
            progressBar.setVisibility(View.VISIBLE);
            email_layout.setVisibility(View.GONE);
            password_layout.setVisibility(View.GONE);
            signInButton.setVisibility(View.GONE);
            signUp.setVisibility(View.GONE);
            forgotPass.setVisibility(View.GONE);
            //textView.setVisibility(View.GONE);
        }
    }


    public final static boolean isValidEmail(String target) {
        if (target == null) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
        }
    }
}


