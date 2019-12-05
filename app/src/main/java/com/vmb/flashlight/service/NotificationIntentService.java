package com.vmb.flashlight.service;

import android.app.IntentService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.Log;

import com.vmb.ads_in_app.util.NetworkUtil;
import com.vmb.flashlight.handler.FlashModeHandler;
import com.vmb.flashlight.handler.NotificationHandler;
import com.vmb.flashlight.ui.MainActivity;

public class NotificationIntentService extends IntentService {
    String TAG = "NotificationIntentService";

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     */
    public NotificationIntentService() {
        super("notificationIntentService");
    }

    @Override
    protected void onHandleIntent(@Nullable final Intent intent) {
        Log.i(TAG, "onHandleIntent");

        switch (intent.getAction()) {
            case "icon":
                Log.i(TAG, "icon");

                Handler icon = new Handler();
                icon.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent launchIntent = new Intent(getApplicationContext(), MainActivity.class);
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launchIntent);

                        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        sendBroadcast(it);
                    }
                });
                break;

            case "flash":
                Log.i(TAG, "flash");

                Handler toggle = new Handler(Looper.getMainLooper());
                toggle.post(new Runnable() {
                    @Override
                    public void run() {
                        /*if (Flashlight.getInstance().isFlashLightOn()) {
                            // Turn off flashlight
                            Flashlight.getInstance().setFlashLightOn(false);
                            Flashlight.getInstance().toggle(Camera.Parameters.FLASH_MODE_OFF);
                        } else {
                            // Turn on flashlight
                            Flashlight.getInstance().setFlashLightOn(true);
                            FlashModeHandler.getInstance().setMode();
                        }*/

                        Intent launchIntent = new Intent(getApplicationContext(), MainActivity.class);
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launchIntent);

                        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        sendBroadcast(it);
                    }
                });
                break;

            case "compass":
                Log.i(TAG, "compass");

                Handler compass = new Handler(Looper.getMainLooper());
                compass.post(new Runnable() {
                    @Override
                    public void run() {
                        FlashModeHandler.getInstance().setLaunch("COMPASS");

                        Intent launchIntent = new Intent(getApplicationContext(), MainActivity.class);
                        launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(launchIntent);

                        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        sendBroadcast(it);
                    }
                });
                break;

            case "wifi":
                Log.i(TAG, "wifi");

                Handler wifi = new Handler(Looper.getMainLooper());
                wifi.post(new Runnable() {
                    @Override
                    public void run() {
                        WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
                        if (wifi.isWifiEnabled()) {
                            FlashModeHandler.getInstance().setWifiOn(false);
                            wifi.setWifiEnabled(false);
                        } else {
                            FlashModeHandler.getInstance().setWifiOn(true);
                            wifi.setWifiEnabled(true);
                        }

                        NotificationHandler handler = new NotificationHandler(getApplicationContext());
                        handler.addNotify();
                    }
                });
                break;

            case "gprs":
                Log.i(TAG, "gprs");

                Handler gprs = new Handler(Looper.getMainLooper());
                gprs.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
                        sendBroadcast(it);

                        try {
                            boolean enabledData = NetworkUtil.isMobileDataEnabled(getApplicationContext()).booleanValue();
                            if (enabledData) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                    Intent intent = new Intent();
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    intent.setComponent(new ComponentName("com.android.settings",
                                            "com.android.settings.Settings$DataUsageSummaryActivity"));
                                    startActivity(intent);
                                    return;
                                }
                                NetworkUtil.setMobileDataEnabled(getApplicationContext(), false);
                                return;
                            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                Intent intent = new Intent();
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                intent.setComponent(new ComponentName("com.android.settings",
                                        "com.android.settings.Settings$DataUsageSummaryActivity"));
                                startActivity(intent);
                                return;
                            } else {
                                NetworkUtil.setMobileDataEnabled(getApplicationContext(), true);
                            }
                        } catch (Exception e) {
                            Log.i(TAG, "Gprs Error: " + e.getMessage());
                        }
                    }
                });
                break;

            default:
                break;
        }
    }
}