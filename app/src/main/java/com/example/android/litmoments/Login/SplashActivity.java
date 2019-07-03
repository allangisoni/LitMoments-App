package com.example.android.litmoments.Login;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.litmoments.Main.MainActivity;
import com.example.android.litmoments.R;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SplashActivity extends AppCompatActivity {

    private static int splashTimeOut = 8000;
    @BindView(R.id.tvsplash)
    TextView tvSplash;
    @BindView(R.id.ivSplash)
    ImageView ivSplash;
    @BindView(R.id.splashLayout)
    ConstraintLayout splashLayout;

    private FirebaseAuth mAuth;

    private FirebaseAuth.AuthStateListener mAuthListner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);

        FirebaseApp.initializeApp(getApplicationContext());

        mAuth = FirebaseAuth.getInstance();
       /** if (mAuth.getCurrentUser() != null) {
            updateUI(true);
        } **/
        splashLayout.setBackgroundColor(getResources().getColor(R.color.colorAccent));

        Typeface myCustomFont = ResourcesCompat.getFont(this, R.font.parisienneregular);
        tvSplash.setTypeface(myCustomFont);

        Animation myanim = AnimationUtils.loadAnimation(this, R.anim.mysplashanimation);
        ivSplash.startAnimation(myanim);
        myanim.setRepeatCount(Animation.INFINITE);

       // String currentUid = mAuth.getCurrentUser().getUid();

        if( mAuth.getCurrentUser()==null){
            updateUI(false);}

            else{
                updateUI(true);
            }


        mAuthListner = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    //   startActivity(new Intent(SplashActivity.this, SplashActivity.class));
                }
            }
        };

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
              //  updateUI(true);
            }
        }, splashTimeOut);



    }


    private void updateUI(boolean isSignedIn) {

        if (isSignedIn) {
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

        } else {

            Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        }
    }

}