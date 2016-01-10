package dam.project.easyroute;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
import java.util.List;
import java.util.TreeMap;

import static dam.project.easyroute.TipTransport.*;


public class JSONParser {

    public static ArrayList<Statie> listaStatii = new ArrayList<>();
    public void incepeParsareJSON(GoogleMap gm)
    {
        new descarcareJSONTask().execute(gm);
    }
    private class descarcareJSONTask extends AsyncTask<Object, Void, String>
    {
        private GoogleMap gm;
        @Override
        protected String doInBackground(Object... objects) {
            gm = (GoogleMap)objects[0];
            return descarcareJSONStatii();
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject obiect = new JSONObject(s);
                JSONArray listaTemp = obiect.getJSONObject("markers").getJSONArray("markers");
                TreeMap<Integer, Statie> mapParsatStatii = JSONArraytoListaStatii(listaTemp);
              //  bifeazaStatiiFavorite(mapParsatStatii);


                listaStatii = new ArrayList<Statie>(mapParsatStatii.values());
                for (Statie statie : listaStatii)
                {
                    int iconStatie;
                    switch (statie.getTipStatie()){
                        case autobuz: iconStatie = R.drawable.ic_bus1; break;
                        case troleibuz: iconStatie = R.drawable.ic_trolley; break;
                        case metrou: iconStatie = R.drawable.ic_subway; break;
                        case tramvai:  iconStatie = R.drawable.ic_railway; break;
                        default: iconStatie = R.drawable.ic_bus1; break;
                    }

                    Marker marker = gm.addMarker(new MarkerOptions().position(new LatLng(statie.getLatitudine(), statie.getLongitudine()))
                                .title(statie.getNumeStatie())
                                .snippet(statie.getTipStatie().toString() + ": " + Utile.convertArrayListToString(statie.getListaMijloaceDeTransport()))
                                .icon(BitmapDescriptorFactory.fromResource(iconStatie))
                         );

                    MapsActivity.treeMapMarkere.put(statie.getNid(),marker);
                }

                //TODO: modificat in Adaptor sa primeasca parametru TreeMap, daca merge
/*                CustomStatieAdapter customStatieAdapter = new CustomStatieAdapter(lv.getContext(), listaParsataStatii);
                lv.setAdapter(customStatieAdapter);
                pb.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        void bifeazaStatiiFavorite(TreeMap<Integer, Statie> mapStatii){
          for(Integer nid : MainActivity.listaNIDuriStatiiFavorite){
              mapStatii.get(nid).setFavorita(true);
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
                    Log.e("JSONParser", "Nu s-a putut descarca fisierul JSON");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return builder.toString();
        }


    }


    private TreeMap<Integer, Statie> JSONArraytoListaStatii(JSONArray jsonStatii)
    {
        try {
            TreeMap<Integer, Statie> listaStatii = new TreeMap<Integer, Statie>();
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
                            statie.setTipStatie(express);
                        else statie.setTipStatie(autobuz);
                        break;
                    case "metro":
                        statie.setTipStatie(metrou);
                        break;
                    case "bus-trolley":
                        statie.setTipStatie(troleibuz);
                        break;
                    case "tram":
                        statie.setTipStatie(tramvai);
                        break;
                    default:
                        statie.setTipStatie(invalid);
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
                listaStatii.put(statie.getNid(), statie);
            }
            return listaStatii;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

}
