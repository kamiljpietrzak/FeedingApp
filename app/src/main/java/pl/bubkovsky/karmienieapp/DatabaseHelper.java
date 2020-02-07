package pl.bubkovsky.karmienieapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "Karmienie.db";
    private static final String TABLE_NAME = "Karmienie_table";
    private static final String COL_1 = "ID";
    private static final String COL_2 = "DATA_GODZINA_START";
    private static final String COL_3 = "DODATKOWE_INFO";
    private static final String COL_4 = "ILE_WYPITE";
    private static final String COL_5 = "DATA_GODZINA_STOP";
    private static final String COL_6 = "PIERS_LEWA";
    private static final String COL_7 = "PIERS_PRAWA";
    private static final String COL_8 = "BUTELKA";
    private Long current_database_size;
    SimpleDateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss");
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE table " + TABLE_NAME + " ("+ COL_1 +" INTEGER PRIMARY KEY AUTOINCREMENT, "+ COL_2 +" TEXT, " + COL_3 + " TEXT, "
                + COL_4 +" TEXT, " + COL_5 +" TEXT, " + COL_6 +" TEXT, " + COL_7 +" TEXT, " + COL_8 +" TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists " + TABLE_NAME );
        onCreate(db);
    }
    public boolean start_feed () {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, String.valueOf(Calendar.getInstance().getTime()));
        long result = db.insertOrThrow(TABLE_NAME, null, contentValues);
        current_database_size=result;
        return result != -1;
    }
    public boolean stop_feed (String info, String ilosc_mleka) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String id = current_database_size.toString();
        contentValues.put(COL_1, id);
        contentValues.put(COL_5, String.valueOf(Calendar.getInstance().getTime()));
        contentValues.put(COL_3, info);
        contentValues.put(COL_4, ilosc_mleka);
        db.update(TABLE_NAME,contentValues,"ID = ?", new String[]{id});
        return true;
    }
    public String getDataFromLastIndex() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT DATA_GODZINA_START, DATA_GODZINA_STOP FROM Karmienie_table ORDER BY id DESC LIMIT 1",null);
        res.moveToLast();
        return res.getString(0);
    }
    public boolean check_box (int wybor) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        String id = current_database_size.toString();
        contentValues.put(COL_1, id);
        switch(wybor){
            case 1:
                contentValues.put(COL_6, "yes");
                break;
            case 2:
                contentValues.put(COL_7, "yes");
                break;
            case 3:
                contentValues.put(COL_8, "yes");
                break;
        }
        db.update(TABLE_NAME,contentValues,"ID = ?", new String[]{id});
        return true;
    }
    public String calculateTimeOfFeeding() throws ParseException {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("SELECT DATA_GODZINA_START, DATA_GODZINA_STOP FROM Karmienie_table ORDER BY id DESC LIMIT 1",null);
        res.moveToLast();
        Date startTime = formatter.parse(res.getString(0).substring(0,19));
        Date stopTime = formatter.parse(res.getString(1).substring(0,19));
        assert stopTime != null;
        assert startTime != null;
        Long result = (stopTime.getTime() - startTime.getTime())/ (60 * 1000) % 60;
        return result.toString() + " minut";
    }
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("Select * from " + TABLE_NAME, null);
        return cursor;
    }
    }
