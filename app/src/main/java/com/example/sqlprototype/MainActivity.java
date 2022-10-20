package com.example.sqlprototype;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import com.example.sqlprototype.R;

import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button Submit;
    EditText Carrera, Asignatura;
    ListView Q_res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Submit = this.findViewById(R.id.Submit);
        Carrera = this.findViewById(R.id.Carrera);
        Asignatura = this.findViewById(R.id.Asignatura);
        Q_res = this.findViewById(R.id.Q_res);

        Submit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String asign = String.valueOf(Asignatura.getText());
                String carr = String.valueOf(Carrera.getText());
                asign = asign.toLowerCase();
                carr = carr.toLowerCase();
                try{
                    DataBaseAccess db = DataBaseAccess.getInstance(getBaseContext());
                    db.open();
                    String[] resultado = new String[1];
                    resultado[0] = db.getDate(asign,carr);
                    String test = db.getDate(asign, carr);
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(),
                            android.R.layout.simple_list_item_1,resultado);
                    // Assign adapter to ListView
                    Q_res.setAdapter(adapter);
                    Toast.makeText(MainActivity.this,"Query Submitted",Toast.LENGTH_SHORT).show();
                    db.close();
                }
                catch (Exception e){
                    Toast.makeText(MainActivity.this,"There's been an Error",Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

}