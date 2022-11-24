package com.example.sqlprototype;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.lang.*;


public class P2DataBaseAccess {
    private static final String DATABASE_NAME="merged.db";
    private static final int DATABASE_VERSION=1;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static P2DataBaseAccess instance;
    Cursor c = null;

    // constructor
    private P2DataBaseAccess(Context context){
        this.openHelper = new SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = openHelper.getReadableDatabase();
    }

    public static P2DataBaseAccess getInstance(Context context){
        if(instance==null){
            instance = new P2DataBaseAccess(context);
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
        String conv_temp = "";
        if(Fuzzy.LevenshteinDistance(Conv,"ordinaria")<=2) {
            Conv = "%enero%";
            conv_temp = "%junio%";
        }
        else if(Fuzzy.LevenshteinDistance(Conv,"extraordinaria")<=3) {
            Conv = "%febrero%";
            conv_temp = "%julio%";
        }
        c = db.rawQuery("Select * from merged where lower(Tit)=? and " +
                "lower(Nombre)=? and (lower(Fecha) like ? or lower(Fecha) like ?)", new String[]{Carr, Asign,Conv,conv_temp});
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
