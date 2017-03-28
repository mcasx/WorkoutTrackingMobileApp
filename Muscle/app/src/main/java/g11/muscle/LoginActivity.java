package g11.muscle;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


public class LoginActivity extends AppCompatActivity {

    private String email;
    ProgressBar         progressBar;
    TextInputLayout    email_layout;
    TextInputLayout    password_layout;
    Button             signInButton;
    Button             signUpButton;
    TextView           textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        email_layout = (TextInputLayout) findViewById(R.id.email_input_layout);
        password_layout = (TextInputLayout) findViewById(R.id.password_input_layout);
        signInButton = (Button) findViewById(R.id.sign_in_button);
        //signUpButton = (Button) findViewById(R.id.sign_up);
        progressBar.setVisibility(View.GONE);
        //textView = (TextView)findViewById(R.id.textView);

    }

    public void onClickSignIn(View view){

        email = ((EditText) findViewById(R.id.email_input)).getText().toString();
        String password = ((EditText) findViewById((R.id.password_input))).getText().toString();
        set_progressBar_visibility(View.VISIBLE);


        if(email.equals("")){
            email_layout.setError("Insert your account's email address");
            set_progressBar_visibility(View.GONE);
            return;
        }
        else
            email_layout.setError(null);

        if(!isValidEmail(email)){
            email_layout.setError("Invalid Email");
            set_progressBar_visibility(View.GONE);
            return;
        }
        else
            email_layout.setError(null);


        if(password.length() < 6){
            password_layout.setError("Password Invalid");
            set_progressBar_visibility(View.GONE);
            return;
        }
        else
            password_layout.setError(null);


        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://138.68.158.127/user_login/" + email + "/" + password;

        //Create the list items through a request

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response){
                        if(response.equals("True")) {

                            Intent intent = new Intent(LoginActivity.this, PickExerciseActivity.class);
                            intent.putExtra("email", email);
                            startActivity(intent);
                        }
                        else{
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
                        alertDialog.setMessage("Please connect your device to the Internet and try again");
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
        );

        queue.add(stringRequest);
    }

    public void set_progressBar_visibility(int view){
        if(View.GONE == view){
            progressBar.setVisibility(View.GONE);
            email_layout.setVisibility(View.VISIBLE);
            password_layout.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.VISIBLE);
            signUpButton.setVisibility(View.VISIBLE);
            textView.setVisibility(View.VISIBLE);
        }

        else{
            progressBar.setVisibility(View.VISIBLE);
            email_layout.setVisibility(View.GONE);
            password_layout.setVisibility(View.GONE);
            signInButton.setVisibility(View.GONE);
            signUpButton.setVisibility(View.GONE);
            textView.setVisibility(View.GONE);
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


