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
import java.util.ArrayList;

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
                ArrayList<Statie> listaParsataStatii = JSONArraytoListaStatii(listaStatii);
                lv.setAdapter(new CustomStatieAdapter(lv.getContext(), listaParsataStatii));
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

    private ArrayList<Statie> JSONArraytoListaStatii(JSONArray jsonStatii)
    {
        try {
            ArrayList<Statie> listaStatii = new ArrayList<Statie>();
            for (int i = 0; i < jsonStatii.length(); i++)
            {
                JSONObject jsonStatie = jsonStatii.getJSONObject(i);
                Statie statie = new Statie();
                statie.setActiva(true);
                String temp = jsonStatie.getString("type");
                switch (temp) {
                    case "bus":
                        String body = jsonStatie.getString("body");
                        if (body.contains("Express") && !body.contains("Autobuze"))
                            statie.setTipStatie(TipTransport.express);
                        else statie.setTipStatie(TipTransport.autobuz);
                        break;
                    case "metro":
                        statie.setTipStatie(TipTransport.metrou);
                        break;
                    case "bus-trolley":
                        statie.setTipStatie(TipTransport.troleibuz);
                        break;
                    case "tram":
                        statie.setTipStatie(TipTransport.tramvai);
                        break;
                    default:
                        statie.setTipStatie(TipTransport.invalid);
                        break;
                }
                statie.setLatitudine(Double.valueOf(jsonStatie.getString("lat")));
                statie.setLongitudine(Double.valueOf(jsonStatie.getString("lon")));
                statie.setNid(Integer.valueOf(jsonStatie.getString("nid")));
                statie.setNumeStatie(jsonStatie.getString("title"));
                temp = jsonStatie.getString("body");
                temp = temp.replace("\n", ", ");
                temp = temp.replace("Autobuze:", "");
                temp = temp.replace("Capat de linie autobuze:", "");
                temp = temp.replace("Capat de linie tramvaie:", "");
                temp = temp.replace("Express:", "");
                temp = temp.replace("Express :", "");
                temp = temp.replace("Metrouri:", "");
                temp = temp.replace("Troleibuze:", "");
                temp = temp.replace("Tramvai:", "");
                temp = temp.replace("Tramvai :", "");
                temp = temp.replace("Tramvaie:", "");
                temp = temp.replace("Tramvaie :", "");
                String[] splat = temp.split(", ");
                ArrayList<String> listaMijloace = new ArrayList<String>();
                for (int j = 0; j < splat.length; j++)
                    listaMijloace.add(splat[j].trim());
                statie.setListaMijloaceDeTransport(listaMijloace);
                listaStatii.add(statie);
            }
            return listaStatii;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
