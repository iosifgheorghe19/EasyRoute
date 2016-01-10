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
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static dam.project.easyroute.TipTransport.*;


public class JSONParser {

    public static ArrayList<Statie> listaStatii = new ArrayList<>();
    public void incepeParsareJSON()
    {
       new descarcareJSONTask().execute();                   //new descarcareJSONTask().execute(gm);
    }

    private class descarcareJSONTask extends AsyncTask<Void, Statie, Void>
    {
        long startTime;

        //private GoogleMap gm;
        @Override
        protected Void doInBackground(Void... objects) {  //only method that runs in background
           // gm = (GoogleMap)objects[0];   // gm e copia hartii cum e ea prima data, inainte sa fie gata :))

            //debug
            startTime = System.currentTimeMillis();
            Log.d("timer", String.valueOf(startTime));

            descarcareJSONStatii();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            long estimatedTime = System.currentTimeMillis() - startTime;  //estimated time in milliseconds
            Log.d("timer-finish", String.valueOf(estimatedTime) + " ms");
        }

        @Override
        protected void onProgressUpdate(Statie... values) {

            Statie statie = values[0];
            int iconStatie;
            switch (statie.getTipStatie()){
                case troleibuz: iconStatie = R.drawable.ic_trolley; break;
                case metrou: iconStatie = R.drawable.ic_subway; break;
                case tramvai:  iconStatie = R.drawable.ic_railway; break;
                default: iconStatie = R.drawable.ic_bus1; break;
            }

            Marker marker = MapsActivity.mMap.addMarker(new MarkerOptions().position(new LatLng(statie.getLatitudine(), statie.getLongitudine()))
                            .title(statie.getNumeStatie())
                            .snippet(statie.getTipStatie().toString() + ": " + Utile.convertArrayListToString(statie.getListaMijloaceDeTransport()))
                            .icon(BitmapDescriptorFactory.fromResource(iconStatie))
            );

            MapsActivity.treeMapMarkere.put(statie.getNid(), marker);
        }

        private void parseazaStatiisiAdaugaMarkere(final String s){


                Thread t = new Thread( new Runnable() {
                    @Override
                    public void run() {
                        //debug
                        long startTimeparSiMarkere= System.currentTimeMillis();

                        try {

                        JSONObject obiect = new JSONObject(s);
                        JSONArray listaTemp = obiect.getJSONObject("markers").getJSONArray("markers");
                        Map<Integer, Statie> mapParsatStatii = JSONArraytoListaStatii(listaTemp);
                        //  bifeazaStatiiFavorite(mapParsatStatii);
                        listaStatii = new ArrayList<Statie>(mapParsatStatii.values());

                        while(!MapsActivity.hartaGata){
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                          //  try { Thread.sleep(250);  } catch (InterruptedException e) {  e.printStackTrace();  }

                            for (Statie statie : listaStatii)
                        {
                            publishProgress(statie);
                        }

                        long startTimestartTimeparSiMarkererefin = System.currentTimeMillis() - startTimeparSiMarkere;
                        Log.d("timer-parSiMarkere", String.valueOf(startTimestartTimeparSiMarkererefin)+ " finissh ");

                        //TODO: modificat in Adaptor sa primeasca parametru TreeMap, daca merge
/*                CustomStatieAdapter customStatieAdapter = new CustomStatieAdapter(lv.getContext(), listaParsataStatii);
                lv.setAdapter(customStatieAdapter);
                pb.setVisibility(View.GONE);
                lv.setVisibility(View.VISIBLE);*/
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    }
                });
            t.start();



        }

        void bifeazaStatiiFavorite(TreeMap<Integer, Statie> mapStatii){
          for(Integer nid : MainActivity.listaNIDuriStatiiFavorite){
              mapStatii.get(nid).setFavorita(true);
          }
        }

        private void descarcareJSONStatii() {
            //debug
            long startTimedescarcare = System.currentTimeMillis();

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

            long startTimedescarcarefin = System.currentTimeMillis() - startTimedescarcare;
            Log.d("timer-descarcare", String.valueOf(startTimedescarcarefin)+ " finissh ");

            parseazaStatiisiAdaugaMarkere(builder.toString());
        }
    }


    private Map<Integer, Statie> JSONArraytoListaStatii(JSONArray jsonStatii)
    {
        //debug
        long startTimejsonarrayToListaStatii = System.currentTimeMillis();

        try {
            final Map<Integer, Statie> listaStatii = Collections.synchronizedMap(new TreeMap<Integer, Statie>());

            int cores = Runtime.getRuntime().availableProcessors();
//            ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(cores,cores,1,TimeUnit.SECONDS,new LinkedBlockingQueue<Runnable>());

            ExecutorService executorService_pool = Executors.newFixedThreadPool(cores);
            Collection<Future<?>> futures = new LinkedList<Future<?>>();

            for (int i = 0; i < jsonStatii.length(); i++)
            {
                final JSONObject jsonStatie = jsonStatii.getJSONObject(i);

                Thread t = new Thread(new Runnable() {
                    JSONObject jStatie = jsonStatie;
                    @Override
                    public void run() {
                        try {
                        Statie statie = new Statie();
                        statie.setActiva(true);
                        String temp = null;

                            temp = jStatie.getString("type");

                        switch (temp) {
                            case "bus":
                                String body = jStatie.getString("body");
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
                        statie.setLatitudine(Double.valueOf(jStatie.getString("lat")));
                        statie.setLongitudine(Double.valueOf(jStatie.getString("lon")));
                        statie.setNid(Integer.valueOf(jStatie.getString("nid")));
                        statie.setNumeStatie(jStatie.getString("title"));
                        temp = jStatie.getString("body");
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


                futures.add(executorService_pool.submit(t));

            }
            for (Future<?> future:futures) {
                future.get();
            }

            long startTimejsonarrayToListaStatiifin = System.currentTimeMillis() - startTimejsonarrayToListaStatii;
            Log.d("timer-jsonArraytoLista", String.valueOf(startTimejsonarrayToListaStatiifin)+ " finissh jsonArrayToListaStatii ");

            return listaStatii;
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }


}
