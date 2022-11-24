package com.example.sqlprototype;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class GpsEtsiit extends Fragment {

    View root;

    public boolean mode;
    public float p1StartY,p1StopY,p2StartY,p2StopY;
    public float DOUBLE_SWIPE_THRESHOLD = 0.5f;

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
}
