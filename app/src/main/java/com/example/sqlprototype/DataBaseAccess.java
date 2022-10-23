package com.example.sqlprototype;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.lang.*;


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

    public String getBestCarrMatch(String str_a ){
        str_a.toLowerCase();
        c = db.rawQuery("Select distinct lower(Tit) from merged", new String[]{});
        String valor;
        if(c.getCount()>0){
            while(c.moveToNext()){
                valor = c.getString(0);
                if (Fuzzy.LevenshteinDistance(str_a,valor)<=2){
                    str_a = valor;
                    break;
                }
            }
        }
        return str_a;
    }

    public String getBestAsignMatch(String str_a){
        str_a.toLowerCase();
        c = db.rawQuery("Select distinct lower(Nombre) from merged", new String[]{});
        String valor;
        if(c.getCount()>0){
            while(c.moveToNext()){
                valor = c.getString(0);
                if (Fuzzy.LevenshteinDistance(str_a,valor)<=2){
                    str_a = valor;
                    break;
                }
            }
        }
        return str_a;
    }

    public String getDate(String Asign, String Carr, String Conv){
        Asign = getBestAsignMatch(Asign) ;
        Carr = getBestCarrMatch(Carr) ;
        if(Fuzzy.LevenshteinDistance(Conv,"ordinaria")<=2)
            Conv = "%Enero%";
        else if(Fuzzy.LevenshteinDistance(Conv,"extraordinaria")<=3)
            Conv = "%Febrero%";
        c = db.rawQuery("Select * from merged where lower(Tit)=? and lower(Nombre)=? and Fecha like ?", new String[]{Carr, Asign,Conv});
        StringBuffer buffer = new StringBuffer();
        if(c.getCount()>0) {
            while (c.moveToNext()) {
                String Fecha = c.getString(7);
                buffer.append("\n" + Fecha);
            }
        }

        return buffer.toString();
    }
}
