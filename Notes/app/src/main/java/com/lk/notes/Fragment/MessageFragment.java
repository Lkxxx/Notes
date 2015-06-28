package com.lk.notes.Fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lk.notes.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class MessageFragment extends Fragment {

    private TextView tv_GitHub;
    private View view;

    public MessageFragment() {

        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_message, container, false);
        initView();

        return view;
    }

    private void initView() {
        LinearLayout ll_message = (LinearLayout) view.findViewById(R.id.ll_message);
        TranslateAnimation translateAnimation = new TranslateAnimation(-50,0,1000,0);
        translateAnimation.setDuration(300);
        ll_message.setAnimation(translateAnimation);

        tv_GitHub = (TextView) view.findViewById(R.id.tv_GitHub);
        tv_GitHub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("https://github.com/Lkxxx/Notes");
                startActivity(new Intent(Intent.ACTION_VIEW, uri));
            }
        });
        final ImageView iv_launcher = (ImageView) view.findViewById(R.id.iv_launcher);
        final RotateAnimation animation = new RotateAnimation(0, 7200, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(100000);
        iv_launcher.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                iv_launcher.startAnimation(animation);
                return true;
            }
        });
        iv_launcher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                iv_launcher.clearAnimation();
            }
        });

    }

}
