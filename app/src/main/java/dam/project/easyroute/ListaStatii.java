package dam.project.easyroute;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by duicu.bogdan on 12/5/2015.
 */

class CursorResults{

    private String nid = "";
    private String numeStatie = "";
    private String tipStatie = "";

    public String getRating() {
        return numeStatie;
    }

    public void setRating(String rating) {
        this.numeStatie = numeStatie;
    }

    public String getTitle() {
        return nid;
    }

    public void setTitle(String title) {
        this.nid = nid;
    }

    public String getRelease() {
        return tipStatie;
    }

    public void setRelease(String release) {
        this.tipStatie = tipStatie;
    }


}

class ViewHolder {
    TextView txtNid;
    TextView txtNumeStatie;
    TextView txtTipStatie;
}

class CustomStatieAdapter extends BaseAdapter
{

    private static ArrayList<Statie> listaStatii;
    private LayoutInflater statieInflater;

    public CustomStatieAdapter(Context context, ArrayList<Statie> results) {
        listaStatii = results;
        this.statieInflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return listaStatii.size();
    }

    @Override
    public Object getItem(int position) {
        return listaStatii.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null)
        {
            convertView = statieInflater.inflate(R.layout.custom_station_list,null);
            holder = new ViewHolder();
            holder.txtNid = (TextView)convertView.findViewById(R.id.statieNid);
            holder.txtNumeStatie = (TextView)convertView.findViewById(R.id.statieNumeStatie);
            holder.txtTipStatie = (TextView)convertView.findViewById(R.id.statieTipStatie);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();
        holder.txtNid.setText(listaStatii.get(position).getNid());
        String numeStatie = String.valueOf(listaStatii.get(position).getNumeStatie()).concat(" stars out of 5");
        holder.txtNumeStatie.setText(numeStatie);
        holder.txtTipStatie.setText(listaStatii.get(position).getTipStatie().toString());
        return convertView;
    }
}


public class ListaStatii extends AppCompatActivity {

    ArrayList<Statie> listaStatii;
    ListView lv = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);
        lv = (ListView)findViewById(R.id.statiiListView);
        StatieDB db = new StatieDB(this);
        String[] coloane = new String[]
                {
                        StatieDB.COL_NID,
                        StatieDB.COL_NUME_STATIE,
                        StatieDB.COL_LONGITUDINE,
                        StatieDB.COL_LATITUDINE,
                        StatieDB.COL_TIP_STATIE,
                        StatieDB.COL_ACTIVA,
                        StatieDB.COL_MIJLOACE_DE_TRANSPORT
                };
        Cursor statii = db.getCursorStatii(coloane, null, null);
        listaStatii = new ArrayList<Statie>();
        while (statii.moveToNext())
        {
            Statie s = new Statie();
            s.setNid(statii.getInt(0));
            s.setNumeStatie(statii.getString(1));
            s.setLongitudine(statii.getDouble(2));
            s.setLatitudine(statii.getDouble(3));
            s.setTipStatie(TipTransport.valueOf(statii.getString(4)));
            int bool = statii.getInt(5);
            s.setActiva(bool == 1 ? true : false);

            listaStatii.add(s);
            Log.d("StatieDB", s.toString());
        }
        lv.setAdapter(new CustomStatieAdapter(this, listaStatii));
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object o = lv.getItemAtPosition(position);
                Statie statie = (Statie)o;
                Intent newActivity = new Intent(ListaStatii.this, MainActivity.class);
                startActivityForResult(newActivity, 1);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_movie_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
    }
}
