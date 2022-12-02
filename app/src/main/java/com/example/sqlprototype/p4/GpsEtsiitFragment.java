package com.example.sqlprototype.p4;

import android.content.DialogInterface;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.sqlprototype.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import java.util.HashMap;
import java.util.Map;


public class GpsEtsiitFragment extends Fragment {
    View root;
    int currentNode = 0, destinyNode = 0;
    TextView txtInstructions, txtNextNode;
    ImageView imgInstructions;
    TextToSpeech textToSpeechEngine;

    ActivityResultLauncher<ScanOptions> barLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() == null)
            return;

        String qr_result = result.getContents();
        currentNode = Integer.parseInt(qr_result);

        DataBaseAccess db = DataBaseAccess.getInstance(root.getContext());
        Instructions instr = db.getInstructions(currentNode, destinyNode);
        doInstructions(instr);
    });

    private void doInstructions(Instructions instr) {
        // Say instructions
        textToSpeechEngine.speak(instr.instructions, TextToSpeech.QUEUE_FLUSH, null, "tts1");

        // Set text instructions (which are invisible by default)
        txtInstructions.setText(instr.instructions);

        // Set image
        int img = InstructionsImgMap.getImg(currentNode, instr.nextNode);
        imgInstructions.setImageResource(img);

        // Display where we are going to
        txtNextNode.setText("Siguiente punto: " + instr.nextNodeName);

        // TODO set brujuleishion
        //brujuleishion.setDir(X);
    }

    private void doubleSwipeUp(){
        txtInstructions.setVisibility(View.GONE);
    }

    private void doubleSwipeDown(){
        txtInstructions.setVisibility(View.VISIBLE);
    }

    private void launchQr() {
        ScanOptions options = new ScanOptions();
        options.setBeepEnabled(true);
        options.setOrientationLocked(false);
        options.setCaptureActivity(CaptureAct.class);
        options.setPrompt("Escanea el código QR");
        barLauncher.launch(options);
    }

    public void setDestinyNode(int destinyNode) {
        this.destinyNode = destinyNode;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_gps_etsiit, container, false);

        txtInstructions = root.findViewById(R.id.txtInstructions);
        txtNextNode = root.findViewById(R.id.txtNextNode);
        imgInstructions = root.findViewById(R.id.imgInstructions);

        textToSpeechEngine = new TextToSpeech(root.getContext(), status -> {
            if (status != TextToSpeech.SUCCESS) {
                Log.e("TTS", "Inicio de la síntesis fallido");
            }
        });

        if (destinyNode == 0)
            throw new RuntimeException("Destiny node is 0");

        launchQr();

        return root;
    }

}
