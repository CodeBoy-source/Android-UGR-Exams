package com.example.sqlprototype.p4;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;


public class DataBaseAccess {
    private static final String DATABASE_NAME="p4.db";
    private static final int DATABASE_VERSION=1;
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase db;
    private static DataBaseAccess instance;
    Cursor c = null;

    // constructor
    private DataBaseAccess(Context context){
        this.openHelper = new SQLiteAssetHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.db = openHelper.getReadableDatabase();
    }

    public static DataBaseAccess getInstance(Context context){
        if(instance==null){
            instance = new DataBaseAccess(context);
        }
        return instance;
    }

    private String getNextNodo(String node1_str, String destiny_str) {
        c = db.rawQuery(
        "SELECT nodo2 FROM rutas WHERE nodo1=? and destino=?;",
            new String[]{node1_str, destiny_str}
        );
        if (c.getCount() == 0)
            return null;
        c.moveToNext();
        return c.getString(0);
    }

    public Instructions getInstructions(int node1, int destiny) {
        String node1_str = String.valueOf(node1), destiny_str = String.valueOf(destiny);
        String node2_str = getNextNodo(node1_str, destiny_str);
        c = db.rawQuery(
        "SELECT instrucciones, imagen FROM instrucciones WHERE nodo1=? and nodo2=?",
            new String[]{node1_str, node2_str}
        );
        if (c.getCount() == 0)
            return null;
        c.moveToNext();
        String instructions = c.getString(0), image = c.getString(1);
        int node2 = Integer.parseInt(node2_str);
        String node2Name = getNodeName(node2);
        return new Instructions(instructions, node2Name, node2);
    }

    public String getNodeName(int node) {
        String node_str = String.valueOf(node);
        c = db.rawQuery("SELECT nombre FROM nodos WHERE nodo=?", new String[]{node_str});
        if (c.getCount() == 0)
            return null;
        c.moveToNext();
        return c.getString(0);
    }

    public int getNodeByName(String nodeName) {
        c = db.rawQuery("SELECT nodo FROM nodos WHERE nombre=?", new String[]{nodeName});
        if (c.getCount() == 0)
            return 0;
        c.moveToNext();
        return Integer.parseInt(c.getString(0));
    }


}
