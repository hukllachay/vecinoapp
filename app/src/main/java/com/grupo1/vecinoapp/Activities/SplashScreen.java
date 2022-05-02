package com.grupo1.vecinoapp.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.grupo1.vecinoapp.R;

public class SplashScreen extends AppCompatActivity {
    public static final String TAG = "TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        try {
            //counter to wait for few minutes and show the splash screen
            new CountDownTimer(3000, 1000) {
                @Override
                public void onFinish() {
                    Intent intent = new Intent(getBaseContext(), LogInActivity.class);
                    startActivity(intent);
                    finish();
                    //overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

                }

                @Override
                public void onTick(long millisUntilFinished) {

                }
            }.start();
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e.toString());
        }
    }
}