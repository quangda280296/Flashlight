package com.vmb.flashlight.model;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.camera2.CameraManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.text.TextUtils;

import com.flash.light.bright.R;
import com.vmb.ads_in_app.util.SharedPreferencesUtil;
import com.vmb.flashlight.Config;

public class Flashlight {

    private static Flashlight flashlight;

    private Camera camera;
    private Camera.Parameters parameters;

    private CameraManager cameraManager;
    private String cameraId;

    private boolean isFlashLightOn = false;

    public static Flashlight getInstance() {
        if (flashlight == null) {
            synchronized (Flashlight.class) {
                flashlight = new Flashlight();
            }
        }
        return flashlight;
    }

    public void setInstance(Flashlight flashlight) {
        Flashlight.flashlight = flashlight;
    }

    public void toggle(final boolean isOn) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    if (cameraManager == null || TextUtils.isEmpty(cameraId))
                        return;
                    try {
                        cameraManager.setTorchMode(cameraId, isOn);
                    } catch (Exception e) {
                        e.printStackTrace();
                        try {
                            String array[] = cameraManager.getCameraIdList();
                            for (int i = 1; i < array.length; i++) {
                                try {
                                    cameraId = cameraManager.getCameraIdList()[i];
                                    cameraManager.setTorchMode(cameraId, isOn);
                                    return;
                                } catch (Exception a) {
                                    a.printStackTrace();
                                }
                            }
                        } catch (Exception b) {
                            b.printStackTrace();
                        }
                    }

                } else {
                    if (parameters == null || camera == null)
                        return;
                    try {
                        if (isOn) {
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            camera.setParameters(parameters);
                            camera.startPreview();
                        } else {
                            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            camera.setParameters(parameters);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    public void playToggleSound(Context context) {
        if (context == null)
            return;

        boolean check = SharedPreferencesUtil.getPrefferBool(context, Config.SharePrefferenceKey.SOUND, true);
        if (!check)
            return;

        MediaPlayer mp = MediaPlayer.create(context, R.raw.sound_toggle);
        if (mp == null)
            return;

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp == null)
                    return;

                mp.reset();
            }
        });
        mp.start();
    }

    public void playMoveSound(Context context) {
        if (context == null)
            return;

        boolean check = SharedPreferencesUtil.getPrefferBool(context, Config.SharePrefferenceKey.SOUND, true);
        if (!check)
            return;

        MediaPlayer mp = MediaPlayer.create(context, R.raw.adjustment_move);
        if (mp == null)
            return;

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp == null)
                    return;

                mp.reset();
            }
        });
        mp.start();
    }

    public void playEndSound(Context context) {
        if (context == null)
            return;

        boolean check = SharedPreferencesUtil.getPrefferBool(context, Config.SharePrefferenceKey.SOUND, true);
        if (!check)
            return;

        MediaPlayer mp = MediaPlayer.create(context, R.raw.adjustment_end);
        if (mp == null)
            return;

        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (mp == null)
                    return;

                mp.reset();
            }
        });
        mp.start();
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public Camera.Parameters getParameters() {
        return parameters;
    }

    public void setParameters(Camera.Parameters parameters) {
        this.parameters = parameters;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    public void setCameraManager(CameraManager cameraManager) {
        this.cameraManager = cameraManager;
    }

    public String getCameraId() {
        return cameraId;
    }

    public void setCameraId(String cameraId) {
        this.cameraId = cameraId;
    }

    public boolean isFlashLightOn() {
        return isFlashLightOn;
    }

    public void setFlashLightOn(boolean flashLightOn) {
        isFlashLightOn = flashLightOn;
    }
}