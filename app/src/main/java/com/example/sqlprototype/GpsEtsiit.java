package com.example.sqlprototype;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class GpsEtsiit extends Fragment implements SensorEventListener {

    View root;

    public boolean mode;
    public float p1StartY,p1StopY,p2StartY,p2StopY;
    public float DOUBLE_SWIPE_THRESHOLD = 0.5f;

    SensorManager sensorManager;
    Sensor sensor, sensorgyroscope;

    public static AtomicInteger THRESHOLD_Z = new AtomicInteger(10);
    public static AtomicInteger THRESHOLD_Y = new AtomicInteger(10);
    public static float THRESHOLD_GY = 3.5f;
    public float EPSILON_Z = 3f;
    public float EPSILON_Y = 3f;
    public AtomicInteger orientation_change = new AtomicInteger(0);
    public boolean first_read = false;
    public float FCX, FCY, FCZ;
    public float lastX,lastY,lastZ = 0;
    public float EPSILON_O = 0.05f;
    public int SIGNY;
    public static boolean is_runningacc = false;
    private static boolean is_runninggyro = false;
    private static AtomicInteger countacc = new AtomicInteger(0);
    private static AtomicInteger countgyr = new AtomicInteger(0);
    private ScheduledExecutorService executorService;
    private ScheduledExecutorService executorService2;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentActivity parent = getActivity();

        sensorManager = (SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        sensorgyroscope = sensorManager.getSensorList(Sensor.TYPE_GYROSCOPE).get(0);

        sensorManager=(SensorManager) parent.getSystemService(Context.SENSOR_SERVICE);
        //add listener for accelerometer
        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_FASTEST);
        //add listener for gyroscope
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        root = inflater.inflate(R.layout.fragment_gps_etsiit, container, false);

        root.setOnTouchListener((v, event) -> {
            if(event.getPointerCount()>1) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_POINTER_DOWN:
                        // This happens when you touch the screen with two fingers
                        this.mode = true;
                        // event.getY(1) is for the second finger
                        p1StartY = event.getY(0);
                        p2StartY = event.getY(1);
                        break;

                    case MotionEvent.ACTION_POINTER_UP:
                        // This happens when you release the second finger
                        this.mode = true;
                        float p1Diff = p1StartY - p1StopY;
                        float p2Diff = p2StartY - p2StopY;

                        //this is to make sure that fingers go in same direction and
                        // swipe have certain length to consider it a swipe
                        if (Math.abs(p1Diff) > DOUBLE_SWIPE_THRESHOLD
                                && Math.abs(p2Diff) > DOUBLE_SWIPE_THRESHOLD &&
                                ((p1Diff > 0 && p2Diff > 0) || (p1Diff < 0 && p2Diff < 0))) {
                            if (p1StartY > p1StopY) {
                                // Swipe up
                                doubleSwipeUp();
                            } else {
                                //Swipe down
                                doubleSwipeDown();
                            }
                        }
                        this.mode = false;
                        break;

                    case MotionEvent.ACTION_MOVE:
                        if (this.mode == true) {
                            p1StopY = event.getY(0);
                            p2StopY = event.getY(1);
                        }
                        break;
                }
            }
            return true;

            });

        return root;
    }



    public void doubleSwipeUp(){
        Toast.makeText(getContext(),"SWIPE UP",Toast.LENGTH_SHORT).show();
    }

    public void doubleSwipeDown(){
        Toast.makeText(getContext(),"SWIPE DOWN",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if(event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
        {

            if(!first_read || orientation_change.get()>=20) {
               FCX = event.values[0];
               FCY = event.values[1];
               FCZ = event.values[2];
               SIGNY = Integer.signum((int)FCY);
               first_read = true;
               orientation_change.set(0);
               countacc.set(0);
            }

            float y = event.values[1];
            float z = event.values[2];
            if(Math.abs(z-lastZ)<=EPSILON_O && Math.abs(y-lastY)<=EPSILON_O)
                orientation_change.addAndGet(1);
            else
                orientation_change.set(0);
            lastY = y;
            lastZ = z;
            float diff_y = Math.abs(FCY - THRESHOLD_Y.get());
            float diff_z = Math.abs(FCZ - THRESHOLD_Z.get());
            boolean upd_y, upd_z;
            if(THRESHOLD_Y.get()!=0){
                upd_y = Math.abs(y - diff_y) <= EPSILON_Y;
                upd_z = z + diff_z <= EPSILON_Z;
            }else{
               upd_y = Math.abs(y - FCY) <= EPSILON_Y;
               upd_z = Math.abs(z - FCZ) <= EPSILON_Z;
            }
            System.out.println(upd_y + " -- " + upd_z + " -- " + is_runningacc + " -- " + countacc + " -- " + THRESHOLD_Y + " || " + orientation_change);
            if(upd_y && upd_z){
                if(is_runningacc && countacc.get()>=3){
                    is_runningacc = false;
                    countacc.set(0);
                    Toast.makeText(getContext(),"PATTERN READ",Toast.LENGTH_SHORT).show();
                }else{
                    THRESHOLD_Z.addAndGet(-10);
                    THRESHOLD_Y.addAndGet(-10);
                    THRESHOLD_Y.set(Math.abs(THRESHOLD_Y.get()));
                    THRESHOLD_Z.set(Math.abs(THRESHOLD_Z.get()));
                    countacc.getAndIncrement();
                    if(!is_runningacc) {
                        executorService = Executors.newSingleThreadScheduledExecutor();
                        executorService.schedule(GpsEtsiit::ResetPatternAcc, 3, TimeUnit.SECONDS);
                    }
                    is_runningacc = true;
                }
            }
        }
        if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE)
        {
            float y = event.values[1];
            System.out.println(y);
            if(Math.signum(THRESHOLD_GY)>=1){
               if(y>=THRESHOLD_GY) {
                   countgyr.getAndIncrement();
                   THRESHOLD_GY *= -1;
                   if (!is_runninggyro) {
                       executorService2 = Executors.newSingleThreadScheduledExecutor();
                       executorService2.schedule(GpsEtsiit::ResetPatternGyro, 3, TimeUnit.SECONDS);
                   }
                   is_runninggyro = true;
               }
            }else if(countgyr.get()%2==1){
                if(y<=THRESHOLD_GY) {
                    countgyr.getAndIncrement();
                    THRESHOLD_GY *=-1;
                }
            }
            if(is_runninggyro && countgyr.get()>=3){
                is_runninggyro = false;
                countgyr.set(0);
                Toast.makeText(getContext(),"PATTERN 2 READ",Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void ResetPatternAcc(){
        is_runningacc = false;
        THRESHOLD_Y.set(10);
        THRESHOLD_Z.set(10);
        countacc.set(0);
    }

    public static void ResetPatternGyro(){
        is_runninggyro = false;
        THRESHOLD_GY = THRESHOLD_GY*Math.signum(THRESHOLD_GY);
        countgyr.set(0);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
