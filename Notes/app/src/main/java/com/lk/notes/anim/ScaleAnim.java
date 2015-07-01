package com.lk.notes.anim;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by lk on 07/02.
 */
public class ScaleAnim extends Animation {

    int centerX;
    int centerY;

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);

        centerX = width / 2;
        centerY = height / 2;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        /*if (interpolatedTime <= 0.2) {
            interpolatedTime = (float) 0.1;
        }*/
        t.getMatrix().setScale(15* interpolatedTime,15* interpolatedTime, centerX, centerY);
    }
}
