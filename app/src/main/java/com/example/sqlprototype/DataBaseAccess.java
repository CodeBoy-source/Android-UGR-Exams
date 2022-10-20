package com.example.sqlprototype;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DataBaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DataBaseAccess instance;
    Cursor c = null;

    // constructor
    private DataBaseAccess(Context context){
        this.openHelper = new DataBaseHelper(context);
    }

    public static DataBaseAccess getInstance(Context context){
        if(instance==null){
            instance = new DataBaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.db = openHelper.getWritableDatabase();
    }

    public void close(){
        if(this.db!=null){
            this.db.close();
        }
    }

    public String getDate(String Asign, String Carr){
        c = db.rawQuery("Select * from merged where lower(Tit)=? and lower(Nombre)=?", new String[]{Carr, Asign});
        StringBuffer buffer = new StringBuffer();
        if(c.getCount()>0) {
            while (c.moveToNext()) {
                String Fecha = c.getString(1);
                buffer.append("\n" + Fecha);
            }
        }

        return buffer.toString();
    }
}
