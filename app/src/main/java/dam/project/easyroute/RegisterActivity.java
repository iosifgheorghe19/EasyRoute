package dam.project.easyroute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    EditText usrBox,pwdBox,pwdConfBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button regButton = (Button)findViewById(R.id.register_button);
        usrBox = (EditText)findViewById(R.id.usernameText);
        pwdBox = (EditText)findViewById(R.id.pwdText);
        pwdConfBox = (EditText)findViewById(R.id.pwdConfText);
        regButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (pwdBox.getText().toString().equals(pwdConfBox.getText().toString())) {
            UserDB db = new UserDB(this);
            db.insertUser(new User(usrBox.getText().toString(), pwdBox.getText().toString()));
            Toast.makeText(this, R.string.usr_registered_string, Toast.LENGTH_SHORT).show();
            finish();
        }
        else
            Toast.makeText(this, R.string.pwds_not_same_string, Toast.LENGTH_SHORT).show();
    }
}
