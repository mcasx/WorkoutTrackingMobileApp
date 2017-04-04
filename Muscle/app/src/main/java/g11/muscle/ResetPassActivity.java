package g11.muscle;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class ResetPassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_pass);
    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(ResetPassActivity.this, LoginActivity.class);

        startActivity(back);
    }
}
