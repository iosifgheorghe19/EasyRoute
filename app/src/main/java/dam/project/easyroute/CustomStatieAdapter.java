package dam.project.easyroute;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by duicu.bogdan on 12/5/2015.
 */

class CursorStatii{

    private String nid = "";
    private String numeStatie = "";
    private String tipStatie = "";


}

class ViewHolder {
    TextView txtNid;
    TextView txtNumeStatie;
    TextView txtTipStatie;
    TextView txtMijloace;
    CheckBox boxFavorit;
}

public class CustomStatieAdapter extends BaseAdapter
{

    private static ArrayList<Statie> listaStatii;
    private LayoutInflater statieInflater;
    Context context;

    public CustomStatieAdapter(Context context, ArrayList<Statie> results) {
        listaStatii = results;
        this.context = context;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null)
        {
            convertView = statieInflater.inflate(R.layout.rand_statie,null);
            holder = new ViewHolder();
            holder.txtNid = (TextView)convertView.findViewById(R.id.textNid);
            holder.txtNumeStatie = (TextView)convertView.findViewById(R.id.textNume);
            holder.txtTipStatie = (TextView)convertView.findViewById(R.id.textTip);
            holder.txtMijloace = (TextView)convertView.findViewById(R.id.textMijloace);
            holder.boxFavorit = (CheckBox)convertView.findViewById(R.id.favorite_button);
            convertView.setTag(holder);
        }
        else
            holder = (ViewHolder)convertView.getTag();
        holder.txtNid.setText(Integer.toString(listaStatii.get(position).getNid()));
        holder.txtNumeStatie.setText(listaStatii.get(position).getNumeStatie());
        String tipStatie = listaStatii.get(position).getTipStatie().toString();
        holder.txtTipStatie.setText(tipStatie.substring(0, 1).toUpperCase() + tipStatie.substring(1));
        holder.txtMijloace.setText(TextUtils.join(", ", listaStatii.get(position).getListaMijloaceDeTransport()));
        holder.boxFavorit.setChecked(false);
        holder.boxFavorit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    StatieDB statieDB = new StatieDB(context);
                    listaStatii.get(position).setFavorita(true);
                    statieDB.insertRecord(listaStatii.get(position));
                    statieDB.close();
                }
            }
        });
        return convertView;
    }
}