package dam.project.easyroute;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class LoginActivity extends AppCompatActivity implements LoginFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle(R.string.login_string);
        JSONParser parser = new JSONParser();
        parser.incepeParsareJSON();
    }

    @Override
    public void onFragmentInteraction(String path) {
        if (path.equals("login"))
            startActivity(new Intent(this, MapsActivity.class));
        else if (path.equals("register"))
            startActivity(new Intent(this, RegisterActivity.class));
    }

}
