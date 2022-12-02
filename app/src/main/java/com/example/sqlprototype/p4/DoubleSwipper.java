package com.example.sqlprototype.p4;

import android.view.View;
import android.view.MotionEvent;


interface DoubleSwipperCallbacks {
    void doubleSwipeDown();
    void doubleSwipeUp();
}

public class DoubleSwipper {
    private boolean mode;
    private float p1StartY,p1StopY,p2StartY,p2StopY;
    private float DOUBLE_SWIPE_THRESHOLD = 0.5f;

	public DoubleSwipper(View root, DoubleSwipperCallbacks callbacks) {
        root.setOnTouchListener((v, event) -> {
            v.performClick();
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
                                callbacks.doubleSwipeUp();
                            } else {
                                //Swipe down
                                callbacks.doubleSwipeDown();
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
    }
}
