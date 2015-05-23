package com.lk.notes.anim;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by lk on 05/21.
 */
public class ShakeAnim extends Animation{

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        t.getMatrix().setTranslate((float) (Math.sin(interpolatedTime*10)*30),0);
        super.applyTransformation(interpolatedTime, t);
    }
}
