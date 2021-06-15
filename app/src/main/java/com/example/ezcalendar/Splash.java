package com.example.ezcalendar;

import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;


import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import android.view.WindowManager;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;


public class Splash extends AppCompatActivity {
    //Inicijalizacija varijabli i elemenata na zaslonu
    int logoDuration = 1800;
    int textDuration = 1000;
    int splashDelay = 5500;
    private ImageView imageView;
    private TextView textView;
    private View view;
    private MaterialButton skipSplash;
    final Handler handler = new Handler(Looper.getMainLooper());
    Runnable r;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //UI/Fullscreen scaling fixes
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        imageView = (ImageView) findViewById(R.id.AnimiranLogo);
        textView = (TextView) findViewById(R.id.ezcalc);
        skipSplash = (MaterialButton) findViewById(R.id.skipButton);
        handleAnimation(view);

        skipSplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Splash.this.startActivity(new Intent(Splash.this, MainActivity.class));
                Splash.this.finish();
            }
        });
    }

    public void handleAnimation (View view) {

        PropertyValuesHolder logoMove = PropertyValuesHolder.ofFloat("y",1000f,-75f);
        PropertyValuesHolder logoScaleX = PropertyValuesHolder.ofFloat(View.SCALE_X,0.05f,0.7f);
        PropertyValuesHolder logoScaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y,0.05f,0.7f);

        ObjectAnimator logo = ObjectAnimator.ofPropertyValuesHolder(imageView,logoMove,logoScaleX,logoScaleY).setDuration(logoDuration);

        ObjectAnimator ezcalcNull = ObjectAnimator.ofFloat(textView,View.ALPHA,1.0f, 0.0f).setDuration(0);
        ObjectAnimator ezcalcAlpha = ObjectAnimator.ofFloat(textView,View.ALPHA, 0.0f, 1.0f).setDuration(textDuration);


        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially(ezcalcNull,logo,ezcalcAlpha);
        animatorSet.start();


        handler.postDelayed(r = () -> {
            //Kod u ovoj funkciji se izvede nakon što zadano vrijeme splashDelay istekne.
            Splash.this.startActivity(new Intent(Splash.this, MainActivity.class));
            Splash.this.finish();
        }, splashDelay);
    }

    protected void onDestroy () {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

}