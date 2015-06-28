package com.lk.notes.Fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.lk.notes.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ClockFragment extends Fragment {


    public ClockFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_clock, container, false);
        ImageView iv_zhizhen = (ImageView)view.findViewById(R.id.iv_zhizhen);
        ImageView iv_shizhen = (ImageView)view.findViewById(R.id.iv_shizhen);
        RotateAnimation rotateAnimation = new RotateAnimation(0,1200*360*5, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation.setDuration(120*10000);
        iv_zhizhen.setAnimation(rotateAnimation);
        RotateAnimation rotateAnimation1 = new RotateAnimation(0,100*360*5, Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
        rotateAnimation1.setDuration(120*10*1000);
        iv_shizhen.setAnimation(rotateAnimation1);



        return view;
    }


}
