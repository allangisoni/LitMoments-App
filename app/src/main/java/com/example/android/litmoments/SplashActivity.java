package com.example.android.litmoments;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private static int splashTimeOut=8000;
    @BindView(R.id.tvsplash) TextView tvSplash;
    @BindView(R.id.ivSplash) ImageView ivSplash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this,MainActivity.class);
                startActivity(i);
                finish();
            }
        },splashTimeOut);

        Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
        tvSplash.setTypeface(myCustomFont);

        Animation myanim = AnimationUtils.loadAnimation(this,R.anim.mysplashanimation);
        ivSplash.startAnimation(myanim);
        myanim.setRepeatCount(Animation.INFINITE);
    }
}
