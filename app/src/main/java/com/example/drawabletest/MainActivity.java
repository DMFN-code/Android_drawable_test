package com.example.drawabletest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View main =  findViewById(R.id.main_activity);
        main.setBackgroundResource(R.drawable.background);

        AnimationDrawable animation = (AnimationDrawable) main.getBackground();
        animation.setEnterFadeDuration(250);
        animation.setExitFadeDuration(3000);
        animation.start();

    }

}