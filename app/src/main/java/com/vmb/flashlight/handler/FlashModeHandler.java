package com.vmb.flashlight.handler;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

import com.vmb.ads_in_app.util.PermissionUtil;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.model.Flashlight;

public class FlashModeHandler {

    private static FlashModeHandler flashModeHandler;

    private boolean isOn = true;
    private boolean isWifiOn = true;
    private String launch = "";

    private int offsetTime = 0;
    private int indicator = 0;

    private Handler handler = new Handler();

    public static FlashModeHandler getInstance() {
        if (flashModeHandler == null) {
            synchronized (FlashModeHandler.class) {
                flashModeHandler = new FlashModeHandler();
            }
        }
        return flashModeHandler;
    }

    public boolean isWifiOn() {
        return isWifiOn;
    }

    public void setWifiOn(boolean wifiOn) {
        isWifiOn = wifiOn;
    }

    public String getLaunch() {
        return launch;
    }

    public void setLaunch(String launch) {
        this.launch = launch;
    }

    public int getIndicator() {
        return indicator;
    }

    public void setIndicator(int indicator) {
        this.indicator = indicator;
    }

    public void setInstance(FlashModeHandler flashModeHandler) {
        FlashModeHandler.flashModeHandler = flashModeHandler;
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            if (Flashlight.getInstance().isFlashLightOn()) {
                if (isOn) {
                    // Turn off flashlight
                    Flashlight.getInstance().toggle(false);
                    isOn = false;
                } else {
                    // Turn on flashlight
                    Flashlight.getInstance().toggle(true);
                    isOn = true;
                }
                handler.postDelayed(this, offsetTime);
            }
        }
    };

    public void setMode(Activity activity) {
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            PermissionUtil.requestPermission(activity, Config.RequestCode.PERMISSION_CAMERA,
                    Manifest.permission.CAMERA);
            return;
        }
        execute();
    }

    public void execute() {
        handler.removeCallbacks(runnable);

        switch (indicator) {
            case 0:
                Flashlight.getInstance().toggle(true);
                break;

            case 1:
                offsetTime = 1000;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 2:
                offsetTime = 950;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 3:
                offsetTime = 900;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 4:
                offsetTime = 850;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 5:
                offsetTime = 800;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 6:
                offsetTime = 750;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 7:
                offsetTime = 700;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 8:
                offsetTime = 650;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 9:
                offsetTime = 600;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 10:
                offsetTime = 550;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 11:
                offsetTime = 500;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 12:
                offsetTime = 450;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 13:
                offsetTime = 400;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 14:
                offsetTime = 350;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 15:
                offsetTime = 300;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 16:
                offsetTime = 250;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 17:
                offsetTime = 200;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 18:
                offsetTime = 150;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 19:
                offsetTime = 100;
                handler.postDelayed(runnable, offsetTime);
                break;

            case 20:
                offsetTime = 50;
                handler.postDelayed(runnable, offsetTime);
                break;

            default:
                break;
        }
    }
}