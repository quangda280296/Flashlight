package com.vmb.flashlight.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.vmb.ads_in_app.GetConfig;
import com.vmb.flashlight.Config;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        finish();
                    }
                }, 500);
            }
        }, 1500);

        //GetConfig.callAPI(getApplicationContext(), Config.CODE_CONTROL_APP, Config.VERSION_APP, Config.PACKAGE_NAME);
    }
}