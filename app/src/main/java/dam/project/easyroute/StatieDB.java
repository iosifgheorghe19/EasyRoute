package dam.project.easyroute;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

class AccessDB extends SQLiteOpenHelper{

    public static String TABLE_STATII_FAVORITE = "statii_favorite";
    public static String DATABASE = "statii.db";
    public static String CREATE_TABLE_STATII_FAVORITE = "CREATE TABLE " + TABLE_STATII_FAVORITE +
            "(nid INTEGER PRIMARY KEY, "+
            "nume_statie TEXT" +
            "longitudine REAL" +
            "latitudine REAL" +
            "tipStatie INTEGER" +
            "activa INTEGER" +
            "mijloace_de_transport TEXT)";

    public AccessDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
     try {
         db.execSQL(CREATE_TABLE_STATII_FAVORITE);
     }catch (Exception ex){
            Log.e("StatieDB", ex.getMessage());
     }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ CREATE_TABLE_STATII_FAVORITE);
        onCreate(db);
    }
}
/*

 */
public class StatieDB {
    AccessDB referenceDB = null;
    protected static final String COL_NID = "nid";
    protected static final String COL_NUME_STATIE = "nume_statie";
    protected static final String COL_LONGITUDINE = "longitudine";
    protected static final String COL_LATITUDINE = "latitudine";
    protected static final String COL_TIP_STATIE = "tipStatie";
    protected static final String COL_ACTIVA = "activa";
    protected static final String COL_MIJLOACE_DE_TRANSPORT = "mijloace_de_transport";

    public StatieDB(Context context){
        referenceDB = new AccessDB(context, AccessDB.DATABASE, null, 1);
    }

    public void close() { referenceDB.close(); }

    long insertRecord(Statie statie){
        long rezInsert = -1;
        SQLiteDatabase database = null;
        ContentValues valoriCont = new ContentValues();
        try{
            database = referenceDB.getWritableDatabase();
            valoriCont.put(COL_NID, statie.getNid());
            valoriCont.put(COL_NUME_STATIE, statie.getNumeStatie());
            valoriCont.put(COL_LONGITUDINE, statie.getLongitudine());
            valoriCont.put(COL_LATITUDINE, statie.getLatitudine());
                int tipStatieInt = statie.getTipStatie().ordinal();
            valoriCont.put(COL_TIP_STATIE, tipStatieInt);
            valoriCont.put(COL_ACTIVA, (statie.isActiva())?1:0 );
            valoriCont.put(COL_MIJLOACE_DE_TRANSPORT, Utile.convertArrayListToString(statie.getListaMijloaceDeTransport()));

            rezInsert = database.insert(AccessDB.TABLE_STATII_FAVORITE, null, valoriCont);
            Log.d("StatieDB", "id inregistrare " + rezInsert);
            database.close();

        }catch(Exception ex){
            Log.e("StatieDB", ex.getMessage());
        }
        return  rezInsert;
    }

    Cursor getCursorStatii(String[] coloane, String cond, String[] paramC){

        SQLiteDatabase database = null;
        Cursor results = null;
        try{
            database = referenceDB.getReadableDatabase();
            results = database.query(AccessDB.TABLE_STATII_FAVORITE, coloane, cond, paramC, null, null, COL_NID + " ASC");
        }catch (Exception ex){
            Log.d("StatieDB", ex.getMessage());

        }
        return results;
    }
}
