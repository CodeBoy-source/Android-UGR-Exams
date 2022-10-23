package com.example.sqlprototype;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

class Group {
    EditText txt;
    ImageView btn;
    TextView lbl;

    public Group(EditText txt, ImageView btn, TextView lbl) {
        this.txt = txt;
        this.btn = btn;
        this.lbl = lbl;
    }
}

public class MainActivity extends AppCompatActivity {
    public static final Integer RecordAudioRequestCode = 1;
    Button btnSubmit;
    EditText txtCarrera, txtAsignatura, txtConvocatoria;
    ImageView btnCarrera, btnAsignatura, btnConvocatoria;
    TextView lblCarrera, lblAsignatura, lblConvocatoria;
    Group currentGroup;
    Group[] groups;
    ListView Q_res;

    SpeechRecognizer speechRecognizer;

    int getColor(int color, double transparency_perc) {
        assert 0 <= transparency_perc && transparency_perc <= 1;
        color &= 0xFFFFFF; // remove transparency if it has it
        int transparency = (int)(transparency_perc*255);
        return (transparency << (8*3)) | color;
    }

    void setGroupTransparency(Group group, double perc) {
        group.txt.setTextColor(getColor(R.color.black, perc));
        group.txt.setHintTextColor(getColor(R.color.black, Math.max(perc-0.2, 0)));
        group.lbl.setTextColor(getColor(R.color.black, perc));
        group.btn.setAlpha((float) perc);
    }

    void setFocusToCurrentGroup() {
        for (Group other_group : groups) {
            if (other_group != currentGroup) {
                setGroupTransparency(other_group, 0.4);
            }
        }
        setGroupTransparency(currentGroup, 1);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        btnSubmit = this.findViewById(R.id.btnSubmit);
        txtCarrera = this.findViewById(R.id.txtCarrera);
        txtAsignatura = this.findViewById(R.id.txtAsignatura);
        txtConvocatoria = this.findViewById(R.id.txtConvocatoria);
        btnCarrera = this.findViewById(R.id.btnCarrera);
        btnAsignatura = this.findViewById(R.id.btnAsignatura);
        btnConvocatoria = this.findViewById(R.id.btnConvocatoria);
        lblCarrera = this.findViewById(R.id.lblCarrera);
        lblAsignatura = this.findViewById(R.id.lblAsignatura);
        lblConvocatoria = this.findViewById(R.id.lblConvocatoria);
        Q_res = this.findViewById(R.id.Q_res);

        Group grpCarrera = new Group(txtCarrera, btnCarrera, lblCarrera);
        Group grpAsignatura = new Group(txtAsignatura, btnAsignatura, lblAsignatura);
        Group grpConvocatoria = new Group(txtConvocatoria, btnConvocatoria, lblConvocatoria);
        groups = new Group[]{grpCarrera, grpAsignatura, grpConvocatoria};
        currentGroup = grpCarrera;
        setFocusToCurrentGroup();

        final Intent speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        btnSubmit.setEnabled(false);

        // Set listeners to each btn, lbl and txt of each group
        for (Group group : groups) {
            group.btn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    currentGroup = group;
                    setFocusToCurrentGroup();

                    for (Group other_group : groups) {
                        if (other_group != group) {
                            other_group.btn.setImageResource(R.drawable.ic_mic_black_off);
                        }
                    }
                    group.btn.setImageResource(R.drawable.ic_mic_black_24dp);

                    speechRecognizer.cancel();
                    speechRecognizer.startListening(speechRecognizerIntent);
                }
            });

            group.lbl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentGroup = group;
                    setFocusToCurrentGroup();
                }
            });

            group.txt.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean focus) {
                    if (focus) {
                        currentGroup = group;
                        setFocusToCurrentGroup();
                    }
                }
            });

            group.txt.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    boolean all_filled = true;
                    for (Group group : groups) {
                        if (String.valueOf(group.txt.getText()).isEmpty()) {
                            all_filled = false;
                        }
                    }
                    btnSubmit.setEnabled(all_filled);
                }

                @Override
                public void afterTextChanged(Editable editable) {}
            });
        }

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float v) {}

            @Override
            public void onBufferReceived(byte[] bytes) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int i) {
                currentGroup.btn.setImageResource(R.drawable.ic_mic_black_off);
                currentGroup.txt.setText("Error: " + Integer.toString(i));
            }

            @Override
            public void onResults(Bundle bundle) {
                currentGroup.btn.setImageResource(R.drawable.ic_mic_black_off);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                currentGroup.txt.setText(data.get(0));
            }

            @Override
            public void onPartialResults(Bundle bundle) {}

            @Override
            public void onEvent(int i, Bundle bundle) {}
        });

        // Listener to make request to the DB
        btnSubmit.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                String asign = String.valueOf(txtAsignatura.getText());
                String carr = String.valueOf(txtCarrera.getText());
                String conv = String.valueOf(txtConvocatoria.getText());
                asign = asign.toLowerCase();
                carr = carr.toLowerCase();
                conv = conv.toLowerCase();
                try{
                    DataBaseAccess db = DataBaseAccess.getInstance(getBaseContext());
                    db.open();
                    String[] resultado = new String[1];
                    resultado[0] = db.getDate(asign,carr,conv);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }

}