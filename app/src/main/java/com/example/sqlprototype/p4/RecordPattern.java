package com.example.sqlprototype.p4;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlprototype.R;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class RecordPattern extends Fragment implements SensorEventListener {

    View rootView;
    TextView coordX,coordY,coordZ,coordXPitch,coordYRoll,coordZYaw;
    public boolean record, filecreated;
    SensorManager sensorManager;
    Sensor sensor, sensorgyroscope;
    FileWriter accwriter,gyrowriter;
    File accfile, gyrofile;
    static FindPattern findPattern = new FindPattern();

    public int index = 0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_record_pattern, container, false);
        Button start = (Button) rootView.findViewById(R.id.recordstart);
        Button stop = (Button) rootView.findViewById(R.id.recordstop);
        Button reset = (Button) rootView.findViewById(R.id.reset);
        coordX = (TextView)rootView.findViewById(R.id.coordX);
        coordY = (TextView)rootView.findViewById(R.id.coordY);
        coordZ = (TextView)rootView.findViewById(R.id.coordZ);

        coordXPitch = (TextView)rootView.findViewById(R.id.coordXPitch);
        coordYRoll = (TextView)rootView.findViewById(R.id.coordYRoll);
        coordZYaw = (TextView)rootView.findViewById(R.id.coordZYaw);

        stop.setOnClickListener(v -> record = false);
        start.setOnClickListener(v -> record = true);
        reset.setOnClickListener(v -> createFiles());
        return rootView;
    }

    public void createFiles(){
        try
        {
            File root = new File(Environment.getExternalStorageDirectory(), "Notes");
            if (!root.exists()) {
                root.mkdirs();
            }
            this.accfile = new File(root, "accpatt.txt");
            if(accfile.exists())
                accfile.delete();
            this.accfile.createNewFile();
            this.accfile.setWritable(true);
            this.gyrofile = new File(root,"gyropatt.txt");
            if(gyrofile.exists())
                gyrofile.delete();
            this.gyrofile.createNewFile();
            this.gyrofile.setWritable(true);
            if(accfile.canWrite() && gyrofile.canWrite()) {
                this.accwriter = new FileWriter(accfile);
                this.gyrowriter = new FileWriter(gyrofile);
                Toast.makeText(getContext(), "DIRECTORY READY", Toast.LENGTH_SHORT).show();
                this.filecreated = true;
            }else {
                Toast.makeText(getContext(), "UNABLE TO CREATE DIRECTORY/FILE", Toast.LENGTH_SHORT).show();
                this.filecreated = false;
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();

        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createFiles();
        FragmentActivity parent = getActivity();

        sensorManager = (SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        sensorgyroscope = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).get(0);

        sensorManager=(SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
        //add listener for accelerometer
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_NORMAL);
        //add listener for gyroscope
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {

            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            coordX.setText("X: "+x);
            coordY.setText("Y: "+y);
            coordZ.setText("Z: "+z);

            if(findPattern.read_acc(event.values))
                Toast.makeText(getContext(), "ACCELEROMETER", Toast.LENGTH_SHORT).show();

            if(record && filecreated)
            {
                try
                {
                    accwriter.append(Float.toString(event.values[0]) + "\t" +
                            Float.toString(event.values[1]) + "\t" + Float.toString(event.values[2]) + "\n");
                    accwriter.flush();
                }
                catch (IOException e)
                {
                    System.out.println("Exception: " + e.toString());
                }

            }
        }

        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE)
        {

            coordXPitch.setText("Orientation X (Roll) :" + Float.toString(event.values[0]));
            coordYRoll.setText("Orientation Y (Pitch) :" + Float.toString(event.values[1]));
            coordZYaw.setText("Orientation Z (Yaw):" + Float.toString(event.values[2]));

            if(findPattern.read_gyro(event.values))
                Toast.makeText(getContext(), "GYROSCOPIO", Toast.LENGTH_SHORT).show();

            if(record && filecreated ) {
                try {
                    gyrowriter.append(Float.toString(event.values[0]) +
                            "\t" + Float.toString(event.values[1]) +
                            "\t" + Float.toString(event.values[2]) + "\n");
                    gyrowriter.flush();
                }
                catch (IOException e)
                {
                    System.out.println("Exception: " + e.toString());
                }

            }
        }

    }
}