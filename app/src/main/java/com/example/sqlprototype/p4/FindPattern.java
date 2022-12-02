package com.example.sqlprototype.p4;

import android.widget.Toast;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FindPattern {

    public static int CONST_Y = 10;
    public static int CONST_Z = 10;
    public static AtomicInteger THRESHOLD_Z = new AtomicInteger(CONST_Z);
    public static AtomicInteger THRESHOLD_Y = new AtomicInteger(CONST_Y);
    public static float THRESHOLD_GY = 3.5f;
    public float EPSILON_Z = 3f;
    public float EPSILON_Y = 3f;
    public float EPSILON_X = 1f;
    public AtomicInteger orientation_change = new AtomicInteger(0);
    public boolean first_read = false;
    public float FCX, FCY, FCZ;
    public float lastGX, lastGY, lastGZ;
    public float EPSILON_GZ = 0.05f;
    public float EPSILON_GY = 0.05f;
    public float EPSILON_GX = 0.05f;
    public float lastX,lastY,lastZ = 0;
    public float EPSILON_O = 0.05f;
    public int SIGNY;
    public static boolean is_runningacc = false;
    private static boolean is_runninggyro = false;
    private static AtomicInteger countacc = new AtomicInteger(0);
    private static AtomicInteger countgyr = new AtomicInteger(0);
    private ScheduledExecutorService executorService;
    private ScheduledExecutorService executorService2;
    public static int update_rate = 30;

    public boolean read_acc(float x, float y, float z){
        if(!first_read || orientation_change.get()>=update_rate) {
            FCX = x;
            FCY = y;
            FCZ = z;
            SIGNY = Integer.signum((int)FCY);
            first_read = true;
            orientation_change.set(0);
            countacc.set(0);
        }
        if(Math.abs(z-lastZ)<=EPSILON_O && Math.abs(y-lastY)<=EPSILON_O)
            orientation_change.addAndGet(1);
        else
            orientation_change.set(0);
        lastY = y;
        lastZ = z;
        float diff_y = Math.abs(FCY - THRESHOLD_Y.get());
        float diff_z = Math.abs(FCZ - THRESHOLD_Z.get());
        boolean upd_y, upd_z, upd_x;
        upd_x = Math.abs(FCX - x) <= EPSILON_X;
        if(THRESHOLD_Y.get()!=0){
            upd_y = Math.abs(y - diff_y) <= EPSILON_Y;
            upd_z = z + diff_z <= EPSILON_Z;
        }else{
            upd_y = Math.abs(y - FCY) <= EPSILON_Y;
            upd_z = Math.abs(z - FCZ) <= EPSILON_Z;
        }
        System.out.println(upd_x + " -- " + upd_y + " -- " + upd_z + " -- " + is_runningacc + " -- " + countacc.get() + " -- " + THRESHOLD_Y + " || " + orientation_change);
        System.out.println(FCY + " -- " + FCZ);
        System.out.println(y + " -- " + z);
        System.out.println(diff_y + " -- " + diff_z);
        if(upd_y && upd_z){
            THRESHOLD_Y.set(Math.abs(THRESHOLD_Y.get()-CONST_Y));
            THRESHOLD_Z.set(Math.abs(THRESHOLD_Z.get()-CONST_Z));
            countacc.incrementAndGet();
            if(!is_runningacc) {
                executorService = Executors.newSingleThreadScheduledExecutor();
                executorService.schedule(FindPattern::ResetPatternAcc, 4, TimeUnit.SECONDS);
            }
            is_runningacc = true;
        }
        if(upd_x && is_runningacc && countacc.get()>=3){
            is_runningacc = false;
            countacc.set(0);
            return true;
        }else {
            return false;
        }
    }
    public boolean read_gyro(float x, float y, float z){
        boolean res = false;
        if(Math.abs(lastGX - x)<=EPSILON_GX && Math.abs(lastGZ-z)<=EPSILON_GZ) {
            if (Math.signum(THRESHOLD_GY) >= 1) {
                if (y >= THRESHOLD_GY) {
                    countgyr.getAndIncrement();
                    THRESHOLD_GY *= -1;
                    if (!is_runninggyro) {
                        executorService2 = Executors.newSingleThreadScheduledExecutor();
                        executorService2.schedule(FindPattern::ResetPatternGyro, 3, TimeUnit.SECONDS);
                    }
                    is_runninggyro = true;
                }
            } else if (countgyr.get() % 2 == 1) {
                if (y <= THRESHOLD_GY) {
                    countgyr.getAndIncrement();
                    THRESHOLD_GY *= -1;
                }
            }
            if (is_runninggyro && countgyr.get() >= 3) {
                is_runninggyro = false;
                countgyr.set(0);
                return true;
            }
        }
        lastGY = y;
        lastGX = x;
        lastGZ = z;
        return false;
    }

    public static void ResetPatternAcc(){
        is_runningacc = false;
        THRESHOLD_Y.set(CONST_Y);
        THRESHOLD_Z.set(CONST_Z);
        countacc.set(0);
    }

    public static void ResetPatternGyro(){
        is_runninggyro = false;
        THRESHOLD_GY = THRESHOLD_GY*Math.signum(THRESHOLD_GY);
        countgyr.set(0);
    }
}
