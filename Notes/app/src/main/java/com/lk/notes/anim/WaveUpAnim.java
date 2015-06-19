package com.lk.notes.anim;

import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Created by lk on 06/19.
 */
public class WaveUpAnim extends Animation{

    private float Y;
    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }
    public WaveUpAnim(float y){
        this.Y = y;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        float m = Y;
        if (interpolatedTime>=0.5&&interpolatedTime<=1){
            interpolatedTime = (float) 0.5;
        }
        t.getMatrix().setTranslate(0, (float) (2.5*interpolatedTime*interpolatedTime*m-2.5*m*interpolatedTime));
        super.applyTransformation(interpolatedTime, t);
    }
}
