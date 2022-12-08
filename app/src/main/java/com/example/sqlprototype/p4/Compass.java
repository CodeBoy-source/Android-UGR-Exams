package com.example.sqlprototype.p4;

import android.hardware.SensorManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sqlprototype.R;

import java.util.Objects;


public class Compass {
    private ImageView img;
    private TextView txt;
    private float[] lastAccelerometer = new float[3];
    private float[] lastMagnetometer = new float[3];
    private boolean isLastAccelerometerCopied = false, isLastMagnetometerCopied = false;
    private long lastUpdatedTime = 0;
    private float currentDegree = 0f;

    private boolean isCompassEnabled = false;
    private float dirAngle = 0f;

    public Compass(ImageView img, TextView txt) {
        this.img = img;
        this.txt = txt;
    }

    public void setDirection(String direction) {
        if (direction.equals("down")) {
            disableCompass(R.drawable.downstairs);
        } else if (direction.equals("up")) {
            disableCompass(R.drawable.upstairs);
        } else {
            enableCompass(Integer.parseInt(direction));
        }
    }

    private void disableCompass(int newResource) {
        isCompassEnabled = false;
        img.setImageResource(newResource);
        img.clearAnimation();
        txt.setVisibility(View.INVISIBLE);
    }

    private void enableCompass(int dirAngle) {
        this.dirAngle = dirAngle;
        img.setImageResource(R.drawable.arrow);
        txt.setVisibility(View.INVISIBLE);
        isCompassEnabled = true;
    }

    public void updateAccelerometer(float[] values) {
        System.arraycopy(values,0,lastAccelerometer,0,values.length);
        isLastAccelerometerCopied = true;
        doRotation();
    }

    public void updateMagnetometer(float[] values) {
        System.arraycopy(values, 0, lastMagnetometer,0,values.length);
        isLastMagnetometerCopied = true;
        doRotation();
    }

    private void doRotation() {
        long elapsed = System.currentTimeMillis() - lastUpdatedTime;
        if (!isCompassEnabled || !isLastAccelerometerCopied || !isLastMagnetometerCopied || elapsed < 250)
            return;

        float[] rotationMatrix = new float[9];
        float[] Orientation = new float[3];
        SensorManager.getRotationMatrix(rotationMatrix,null,lastAccelerometer,lastMagnetometer);
        SensorManager.getOrientation(rotationMatrix,Orientation);

        float azimuthInRadians = Orientation[0];
        float azimuthToDegree = (float) Math.toDegrees(azimuthInRadians) + dirAngle;

        RotateAnimation rotateAnimation = new RotateAnimation(
                currentDegree,-azimuthToDegree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,0.5f
        );
        rotateAnimation.setDuration(250);
        rotateAnimation.setFillAfter(true);
        img.startAnimation(rotateAnimation);

        currentDegree=-azimuthToDegree;
        lastUpdatedTime = System.currentTimeMillis();
        int x = (int) azimuthToDegree;
        txt.setText("Gire: " + x + "·");
    }
}
