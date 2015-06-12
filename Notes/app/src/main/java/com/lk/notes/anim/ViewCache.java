package com.lk.notes.anim;

/**
 * Created by lk on 06/08.
 */

import android.view.View;
import android.widget.ImageView;

import com.lk.notes.R;

public class ViewCache {
    private View baseView;

    private ImageView imageView;

    public ViewCache(View baseView) {
        this.baseView = baseView;
    }

    public ImageView getImageView() {
        if (imageView == null) {
            imageView = (ImageView) baseView.findViewById(R.id.image);
        }
        return imageView;
    }
}