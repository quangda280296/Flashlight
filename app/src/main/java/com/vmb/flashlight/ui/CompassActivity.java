package com.vmb.flashlight.ui;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

import com.flash.light.bright.R;
import com.vmb.ads_in_app.handler.AdsHandler;
import com.vmb.ads_in_app.util.ToastUtil;
import com.vmb.flashlight.handler.FlashModeHandler;

import jack.com.servicekeep.act.BaseVMAppCompatActivity;

public class CompassActivity extends BaseVMAppCompatActivity implements SensorEventListener {

    private ImageView img_compass;

    private boolean open_from_status_bar = false;

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compass);

        img_compass = findViewById(R.id.img_compass);

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // for the system's orientation sensor registered listeners
        if (mSensorManager != null)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_GAME);
        else
            ToastUtil.longToast(getApplicationContext(), getString(R.string.not_support_compass));

        Intent intent = getIntent();
        if (intent != null && intent.getAction() != null) {
            String action = intent.getAction();

            if (action.equals("COMPASS")) {
                Log.i("initData()", "open_from_status_bar = true");
                open_from_status_bar = true;
            }
        }

        AdsHandler.getInstance().displayInterstitial(CompassActivity.this);
        AdsHandler.getInstance().initBanner(CompassActivity.this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // get the angle around the z-axis rotated
        float degree = Math.round(event.values[0]);

        // create a rotation animation (reverse turn degree degrees)
        RotateAnimation ra = new RotateAnimation(
                currentDegree,
                -degree,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);

        // how long the animation will take place
        ra.setDuration(210);

        // set the animation after the end of the reservation status
        ra.setFillAfter(true);

        currentDegree = -degree;

        if (img_compass != null) {
            // Start the animation
            img_compass.startAnimation(ra);
        }

        Log.i("onSensorChanged", "currentDegree = " + currentDegree);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume()", "onResume()");
    }

    @Override
    protected void onDestroy() {
        destroy();
        super.onDestroy();
    }

    public void destroy() {
        // to stop the listener and save battery
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);
    }

    @Override
    public void onBackPressed() {
        Log.i("onKeyBack", "onKeyBack()");

        if (open_from_status_bar)
            FlashModeHandler.getInstance().setLaunch("");

        finish();
        AdsHandler.getInstance().displayInterstitial(CompassActivity.this);
    }
}