package dam.project.easyroute;

import android.app.Application;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Theo on 04.12.2015.
 */
public class JSONParser {

    public void incepeParsareJSON(ListView lv, ProgressBar pb)
    {
        new descarcareJSONTask().execute(lv, pb);
    }
    private class descarcareJSONTask extends AsyncTask<View, Void, String>
    {
        private ListView lv;
        private ProgressBar pb;
        @Override
        protected String doInBackground(View... views) {
            lv = (ListView)views[0];
            pb = (ProgressBar)views[1];
            return descarcareJSONStatii();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject obiect = new JSONObject(s);
                JSONArray listaStatii = obiect.getJSONObject("markers").getJSONArray("markers");
                String statii[] = new String[listaStatii.length()];
                for (int i = 0; i < listaStatii.length(); i++)
                    statii[i] = listaStatii.getString(i);
                ListAdapter adaptor = new ArrayAdapter<String>(lv.getContext(), android.R.layout.simple_list_item_1, statii);
                lv.setAdapter(adaptor);
                pb.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private String descarcareJSONStatii() {
            StringBuilder builder = new StringBuilder();
            try {
                URL url = new URL("http://urbo.ro/openmap_transport/load_nid");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                if (conn.getResponseCode() == 200) {
                    InputStream content = new BufferedInputStream(conn.getInputStream());
                    BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        builder.append(line);
                    }
                } else {
                    Log.e("JSONParser", "Nu s-a putut descarca fisierul");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }
    }

}
