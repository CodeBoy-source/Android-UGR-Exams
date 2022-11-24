package com.example.sqlprototype;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.speech.tts.TextToSpeech;

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

public class HomeFragment extends Fragment {
    View root;

    public static final Integer RecordAudioRequestCode = 1;
    Button btnSubmit;
    EditText txtCarrera, txtAsignatura, txtConvocatoria;
    ImageView btnCarrera, btnAsignatura, btnConvocatoria;
    TextView lblCarrera, lblAsignatura, lblConvocatoria;
    Group currentGroup;
    Group[] groups;
    TextView Q_res;
    private TextToSpeech textToSpeechEngine;
    private Button ttsButton;

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

    void textToSpeechGroup(Group group){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeechEngine.speak(group.lbl.getText(), TextToSpeech.QUEUE_FLUSH, null, "tts1");
        }
    }

    void setFocusToCurrentGroup(boolean tts) {
        for (Group other_group : groups) {
            if (other_group != currentGroup) {
                setGroupTransparency(other_group, 0.4);
            }
        }
        setGroupTransparency(currentGroup, 1);
        if(tts)
            textToSpeechGroup(currentGroup);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_home, container, false);
        super.onCreate(savedInstanceState);

        btnSubmit = root.findViewById(R.id.btnSubmit);
        txtCarrera = root.findViewById(R.id.txtCarrera);
        txtAsignatura = root.findViewById(R.id.txtAsignatura);
        txtConvocatoria = root.findViewById(R.id.txtConvocatoria);
        btnCarrera = root.findViewById(R.id.btnCarrera);
        btnAsignatura = root.findViewById(R.id.btnAsignatura);
        btnConvocatoria = root.findViewById(R.id.btnConvocatoria);
        lblCarrera = root.findViewById(R.id.lblCarrera);
        lblAsignatura = root.findViewById(R.id.lblAsignatura);
        lblConvocatoria = root.findViewById(R.id.lblConvocatoria);
        Q_res = root.findViewById(R.id.Q_res);

        textToSpeechEngine = new TextToSpeech(root.getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.SUCCESS) {
                    Log.e("TTS", "Inicio de la síntesis fallido");
                }
            }
        });

        Group grpCarrera = new Group(txtCarrera, btnCarrera, lblCarrera);
        Group grpAsignatura = new Group(txtAsignatura, btnAsignatura, lblAsignatura);
        Group grpConvocatoria = new Group(txtConvocatoria, btnConvocatoria, lblConvocatoria);
        groups = new Group[]{grpCarrera, grpAsignatura, grpConvocatoria};
        currentGroup = grpCarrera;
        setFocusToCurrentGroup(true);

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
                    setFocusToCurrentGroup(false);

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
                    setFocusToCurrentGroup(true);
                }
            });

            group.txt.setOnFocusChangeListener(new View.OnFocusChangeListener(){
                @Override
                public void onFocusChange(View v, boolean focus) {
                    if (focus) {
                        currentGroup = group;
                        setFocusToCurrentGroup(true);
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

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(root.getContext());
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
                    DataBaseAccess db = DataBaseAccess.getInstance(root.getContext());
                    db.open();
                    String resultado = db.getDate(asign,carr,conv);

                    String intro = "Tendrás el examen el";
                    if(resultado.isEmpty()){
                        intro = "Lo siento, no hemos encontrado ningún examen";
                    }
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        textToSpeechEngine.speak(intro + resultado, TextToSpeech.QUEUE_FLUSH, null, "tts1");
                    }

                    for (Group group : groups) {
                        group.txt.setText("");
                    }

                    // Assign adapter to ListView
                    Q_res.setText(intro + resultado);
                    Toast.makeText(root.getContext(),"Query Submitted",Toast.LENGTH_SHORT).show();
                    db.close();
                }
                catch (Exception e){
                    Toast.makeText(root.getContext(),"There's been an Error",Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }
}