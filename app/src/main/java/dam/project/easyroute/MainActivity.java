package dam.project.easyroute;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv = (ListView)findViewById(R.id.statiiListView);
        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
        JSONParser parser = new JSONParser();
        parser.incepeParsareJSON(lv, pb);
    }
}
