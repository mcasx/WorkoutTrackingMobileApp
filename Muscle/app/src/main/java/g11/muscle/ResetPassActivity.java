package g11.muscle;

import android.content.Intent;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;


public class ResetPassActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private String email;
    private TextInputLayout email_layout;
    private Button submitButton;
    private View note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        email_layout = (TextInputLayout) findViewById(R.id.email_input_layout);
        submitButton = (Button) findViewById(R.id.reset_pass);
        note = findViewById(R.id.textView3);

        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void onClickSubmit(View view) {

        email = ((EditText) findViewById(R.id.email_input)).getText().toString().trim();
        set_progressBar_visibility(View.VISIBLE);


        if (email.equals("")) {
            email_layout.setError("Please insert your Muscle account email address!");
            set_progressBar_visibility(View.GONE);
            return;
        } else
            email_layout.setError(null);

        if (!isValidEmail(email)) {
            email_layout.setError("Invalid email!");
            set_progressBar_visibility(View.GONE);
            return;
        } else
            email_layout.setError(null);

        //TODO check if the email exists in database??

        //TODO actually queue the email with the reset link

        return;
    }

    private void set_progressBar_visibility(int view) {

        if (View.GONE == view) {
            progressBar.setVisibility(View.GONE);
            email_layout.setVisibility(View.VISIBLE);
            submitButton.setVisibility(View.VISIBLE);
            note.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.VISIBLE);
            email_layout.setVisibility(View.GONE);
            submitButton.setVisibility(View.GONE);
            note.setVisibility(View.GONE);
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
