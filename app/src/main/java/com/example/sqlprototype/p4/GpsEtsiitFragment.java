package com.example.sqlprototype.p4;

import android.content.DialogInterface;
import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.widget.Toast;


import com.example.sqlprototype.R;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;

import com.example.sqlprototype.p4.DoubleSwipper;

import java.text.CollationElementIterator;
import java.util.HashMap;
import java.util.Map;


public class GpsEtsiitFragment extends Fragment implements DoubleSwipperCallbacks, SensorEventListener {
    View root;
    int currentNode = 0, destinyNode = 0;
    TextView txtInstructions, txtNextNode;
    ImageView imgInstructions;
    TextToSpeech textToSpeechEngine;
    DoubleSwipper doubleSwipper;
    static FindPattern findPattern = new FindPattern();

    ActivityResultLauncher<ScanOptions> barLauncher;

    SensorManager sensorManager;
    Sensor sensor, sensorgyroscope, magnetometerSensor;
    // Data structure for sensor operations
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private float[] rotationMatrix = new float[9];
    private float[] Orientation = new float[3];
    boolean isLastAccelerometerCopied = false, isLastMagnetometerCopied = false;
    long lastUpdatedTime = 0;
    float currentDegree = 0f;
    private float dir_angle = 0;
    private ImageView brujula;
    private TextView textoGrado;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity parent = getActivity();

        sensorManager = (SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        sensorgyroscope = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).get(0);
        magnetometerSensor = sensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);

        sensorManager=(SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
        //add listener for accelerometer
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);
        //add listener for gyroscope
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
        //add listener for magnetometer
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_gps_etsiit, container, false);

        txtInstructions = root.findViewById(R.id.txtInstructions);
        txtNextNode = root.findViewById(R.id.txtNextNode);
        imgInstructions = root.findViewById(R.id.imgInstructions);

        textoGrado = root.findViewById(R.id.textoGrado);
        brujula = root.findViewById(R.id.imgInstructions);

        doubleSwipper = new DoubleSwipper(root, this);

        textToSpeechEngine = new TextToSpeech(root.getContext(), status -> {
            if (status != TextToSpeech.SUCCESS) {
                Log.e("TTS", "Inicio de la síntesis fallido");
            }
        });

        if (destinyNode == 0)
            throw new RuntimeException("Destiny node is 0");

        barLauncher = registerForActivityResult(new ScanContract(), result -> {
            if (result.getContents() == null)
                return;

            String qr_result = result.getContents();
            currentNode = Integer.parseInt(qr_result);

            DataBaseAccess db = DataBaseAccess.getInstance(root.getContext());
            Instructions instr = db.getInstructions(currentNode, destinyNode);
            doInstructions(instr);
        });
        launchQr();

        return root;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            System.arraycopy(event.values,0,lastAccelerometer,0,event.values.length);
            isLastAccelerometerCopied = true;
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (findPattern.read_acc(x,y,z))
                Toast.makeText(getContext(), "PATTERN READ", Toast.LENGTH_SHORT).show();
                //launchQr();

        }
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE)
        {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            if (findPattern.read_gyro(x,y,z))
                Toast.makeText(getContext(),"PATTERN 2 READ",Toast.LENGTH_SHORT).show();
        }
        if(event.sensor == magnetometerSensor) {
            System.arraycopy(event.values, 0, lastMagnetometer,0,event.values.length);
            isLastMagnetometerCopied = true;
        }

        if(isLastAccelerometerCopied && isLastMagnetometerCopied && System.currentTimeMillis() - lastUpdatedTime>250){
            SensorManager.getRotationMatrix(rotationMatrix,null,lastAccelerometer,lastMagnetometer);
            SensorManager.getOrientation(rotationMatrix,Orientation);

            float azimuthInRadians = Orientation[0];
            float azimuthToDegree = (float) Math.toDegrees(azimuthInRadians) + dir_angle;

            RotateAnimation rotateAnimation =
                    new RotateAnimation(currentDegree,-azimuthToDegree, Animation.RELATIVE_TO_SELF, 0.5f,Animation.RELATIVE_TO_SELF,0.5f);
            rotateAnimation.setDuration(250);
            rotateAnimation.setFillAfter(true);
            brujula.startAnimation(rotateAnimation);

            currentDegree=-azimuthToDegree;
            lastUpdatedTime = System.currentTimeMillis();
            int x = (int) azimuthToDegree;
            textoGrado.setText("Gire: " + x + "·");

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}


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

    public void doubleSwipeUp(){
        txtInstructions.setVisibility(View.GONE);
    }

    public void doubleSwipeDown(){
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


}
