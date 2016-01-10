package dam.project.easyroute;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by duicu.bogdan on 1/9/2016.
 */

class AccessUserDB extends SQLiteOpenHelper {

    public static String TABLE_USERS = "users";
    public static String DATABASE = "users.db";
    public static String CREATE_TABLE_USERS = "CREATE TABLE " + TABLE_USERS +
            "(uid INTEGER PRIMARY KEY AUTOINCREMENT, "+
            "userName TEXT" +
            "userPassword TEXT)";

    public AccessUserDB(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CREATE_TABLE_USERS);
        }catch (Exception ex){
            Log.e("UserDB", ex.getMessage());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ CREATE_TABLE_USERS);
        onCreate(db);
    }
}

public class UserDB {

    AccessUserDB referenceDB = null;
    protected static final String COL_UID = "uid";
    protected static final String COL_USERNAME = "userName";
    protected static final String COL_USERPASSWORD = "userPassword";

    public UserDB(Context context){
        referenceDB = new AccessUserDB(context, AccessUserDB.DATABASE, null, 1);
    }

    public void close() { referenceDB.close(); }

    long insertUser(User user){
        long rezInsert = -1;
        SQLiteDatabase database = null;
        ContentValues valoriCont = new ContentValues();
        try{
            database = referenceDB.getWritableDatabase();
            valoriCont.put(COL_UID, user.getUid());
            valoriCont.put(COL_USERNAME, user.getUserName());
            valoriCont.put(COL_USERPASSWORD, user.getUserPassword());


            rezInsert = database.insert(AccessUserDB.TABLE_USERS, null, valoriCont);
            Log.d("UserDB", "id inregistrare " + rezInsert);
            database.close();

        }catch(Exception ex){
            Log.e("UserDB", ex.getMessage());
        }
        return  rezInsert;
    }

    Cursor getCursorUsers(String[] coloane, String cond, String[] paramC){

        SQLiteDatabase database = null;
        Cursor results = null;
        try{
            database = referenceDB.getReadableDatabase();
            results = database.query(AccessUserDB.TABLE_USERS, coloane, cond, paramC, null, null, COL_UID + " ASC");
        }catch (Exception ex){
            Log.d("UserDB", ex.getMessage());

        }
        return results;
    }
}
