package dam.project.easyroute;

import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    public static ArrayList<Statie> listaStatiiFavorite = null;
    public static ArrayList<Integer> listaNIDuriStatiiFavorite = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView lv = (ListView)findViewById(R.id.statiiListView);
        ProgressBar pb = (ProgressBar)findViewById(R.id.progressBar);
       // listaStatiiFavorite = getStatiiFavoriteList();
        listaNIDuriStatiiFavorite = getNIDuriStatiiFavorite();

/*        JSONParser parser = new JSONParser();
        parser.incepeParsareJSON(lv, pb);*/
    }

    ArrayList<Integer> getNIDuriStatiiFavorite(){
        ArrayList<Integer> listaNIDuriStatiiFavorite = new ArrayList<Integer>();

        StatieDB statieDB = new StatieDB(this);
        Cursor c = statieDB.getCursorStatii(null,null,null);
        while(c.moveToNext()){
            listaNIDuriStatiiFavorite.add(c.getInt(0));
        }
        statieDB.close();
        return listaNIDuriStatiiFavorite;
    }


    ArrayList<Statie> getStatiiFavoriteList(){
        ArrayList<Statie> listaStatiiFavorite = new ArrayList<Statie>();

        StatieDB statieDB = new StatieDB(this);
        Cursor c = statieDB.getCursorStatii(null,null,null);
        while(c.moveToNext()){
            Statie s = new Statie();
            s.setNid(c.getInt(0));
            s.setNumeStatie(c.getString(1));
            s.setLongitudine(c.getDouble(2));
            s.setLatitudine(c.getDouble(3));
            int tipStatieInt = c.getInt(4);
            switch (tipStatieInt) {
                case 1:
                    s.setTipStatie(TipTransport.autobuz);
                    break;
                case 2:
                    s.setTipStatie(TipTransport.troleibuz);
                    break;
                case 3:
                    s.setTipStatie(TipTransport.autobuzTroleibuz);
                    break;
                case 4:
                    s.setTipStatie(TipTransport.metrou);
                    break;
                case 5:
                    s.setTipStatie(TipTransport.tramvai);
                    break;
                default:
                    s.setTipStatie(TipTransport.invalid);
                    break;
            }
            s.setActiva( c.getInt(5)==1  );
            s.setListaMijloaceDeTransport(Utile.convertStringToArrayList(c.getString(6)));

            listaStatiiFavorite.add(s);
        }
        return listaStatiiFavorite;
    }


}
