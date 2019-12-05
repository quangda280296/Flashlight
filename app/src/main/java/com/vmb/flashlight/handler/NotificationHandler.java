package com.vmb.flashlight.handler;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.widget.RemoteViews;

import com.quangda280296.flashlight.R;
import com.vmb.ads_in_app.util.SharedPreferencesUtil;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.model.Flashlight;
import com.vmb.flashlight.service.NotificationIntentService;

/**
 * Created by keban on 6/15/2018.
 */

public class NotificationHandler {

    private Context context;
    private NotificationManager notificationManager;

    public NotificationHandler(Context context) {
        this.context = context;
    }

    public void addNotify() {
        if (context == null)
            return;

        //define a notification manager
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        boolean check = SharedPreferencesUtil.getPrefferBool(context, Config.SharePrefferenceKey.STATUS_BAR, false);
        if (!check) {
            notificationManager.cancel(Config.Notification.ID_INTERACTIVE);
            return;
        }

        thread.run();
    }

    // Handle notification
    Thread thread = new Thread(new Runnable() {
        @Override
        public void run() {
            String TAG = "NotificationHandler";

            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.ic_notification_small)
                    .setWhen(System.currentTimeMillis())
                    .setOngoing(true);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setVisibility(Notification.VISIBILITY_SECRET);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                RemoteViews contentView = new RemoteViews(context.getPackageName(),
                        R.layout.layout_interactive_notificartion);

                if (FlashModeHandler.getInstance().isWifiOn())
                    contentView.setImageViewResource(R.id.img_wifi, R.drawable.ic_noti_wifi);
                else
                    contentView.setImageViewResource(R.id.img_wifi, R.drawable.ic_wifi_off);

                /*if (NetworkUtil.isMobileDataEnabled(context))
                    contentView.setImageViewResource(R.id.img_gprs, R.drawable.ic_noti_gprs);
                else
                    contentView.setImageViewResource(R.id.img_gprs, R.drawable.ic_gprs_off);*/

                if (Flashlight.getInstance().isFlashLightOn())
                    contentView.setImageViewResource(R.id.img_flash, R.drawable.ic_noti_flash_on);
                else
                    contentView.setImageViewResource(R.id.img_flash, R.drawable.ic_noti_flash);

                // adding action to the icon button
                Intent icon = new Intent(context, NotificationIntentService.class);
                icon.setAction("icon");
                contentView.setOnClickPendingIntent(R.id.layout_icon, PendingIntent.getService(context,
                        Config.RequestCode.ICON_INTERACT_NOTI, icon, PendingIntent.FLAG_UPDATE_CURRENT));

                // adding action to the flash button
                Intent flash = new Intent(context, NotificationIntentService.class);
                flash.setAction("flash");
                contentView.setOnClickPendingIntent(R.id.layout_flash, PendingIntent.getService(context,
                        Config.RequestCode.FLASH, flash, PendingIntent.FLAG_UPDATE_CURRENT));

                // adding action to the compass button
                Intent compass = new Intent(context, NotificationIntentService.class);
                compass.setAction("compass");
                contentView.setOnClickPendingIntent(R.id.layout_compass, PendingIntent.getService(context,
                        Config.RequestCode.COMPASS, compass, PendingIntent.FLAG_UPDATE_CURRENT));

                // adding action to the wifi button
                Intent wifi = new Intent(context, NotificationIntentService.class);
                wifi.setAction("wifi");
                contentView.setOnClickPendingIntent(R.id.layout_wifi, PendingIntent.getService(context,
                        Config.RequestCode.WIFI, wifi, PendingIntent.FLAG_UPDATE_CURRENT));

                // adding action to the browser button
                Intent gprs = new Intent(context, NotificationIntentService.class);
                gprs.setAction("gprs");
                contentView.setOnClickPendingIntent(R.id.layout_gprs, PendingIntent.getService(context,
                        Config.RequestCode.GPRS, gprs, PendingIntent.FLAG_UPDATE_CURRENT));

                builder.setCustomContentView(contentView);

            } else {
                RemoteViews contentView = new RemoteViews(context.getPackageName(),
                        R.layout.layout_interactive_notificartion_below);

                if (FlashModeHandler.getInstance().isWifiOn())
                    contentView.setImageViewResource(R.id.img_wifi, R.drawable.ic_noti_wifi);
                else
                    contentView.setImageViewResource(R.id.img_wifi, R.drawable.ic_wifi_off);

                /*if (NetworkUtil.isMobileDataEnabled(context))
                    contentView.setImageViewResource(R.id.img_gprs, R.drawable.ic_noti_gprs);
                else
                    contentView.setImageViewResource(R.id.img_gprs, R.drawable.ic_gprs_off);*/

                if (Flashlight.getInstance().isFlashLightOn())
                    contentView.setImageViewResource(R.id.img_flash, R.drawable.ic_noti_flash_on);
                else
                    contentView.setImageViewResource(R.id.img_flash, R.drawable.ic_noti_flash);

                // adding action to the icon button
                Intent icon = new Intent(context, NotificationIntentService.class);
                icon.setAction("icon");
                contentView.setOnClickPendingIntent(R.id.layout_icon, PendingIntent.getService(context,
                        Config.RequestCode.ICON_INTERACT_NOTI, icon, PendingIntent.FLAG_UPDATE_CURRENT));

                // adding action to the flash button
                Intent flash = new Intent(context, NotificationIntentService.class);
                flash.setAction("flash");
                contentView.setOnClickPendingIntent(R.id.layout_flash, PendingIntent.getService(context,
                        Config.RequestCode.FLASH, flash, PendingIntent.FLAG_UPDATE_CURRENT));

                // adding action to the compass button
                Intent compass = new Intent(context, NotificationIntentService.class);
                compass.setAction("compass");
                contentView.setOnClickPendingIntent(R.id.layout_compass, PendingIntent.getService(context,
                        Config.RequestCode.COMPASS, compass, PendingIntent.FLAG_UPDATE_CURRENT));

                // adding action to the wifi button
                Intent wifi = new Intent(context, NotificationIntentService.class);
                wifi.setAction("wifi");
                contentView.setOnClickPendingIntent(R.id.layout_wifi, PendingIntent.getService(context,
                        Config.RequestCode.WIFI, wifi, PendingIntent.FLAG_UPDATE_CURRENT));

                // adding action to the browser button
                Intent gprs = new Intent(context, NotificationIntentService.class);
                gprs.setAction("gprs");
                contentView.setOnClickPendingIntent(R.id.layout_gprs, PendingIntent.getService(context,
                        Config.RequestCode.GPRS, gprs, PendingIntent.FLAG_UPDATE_CURRENT));

                builder.setContent(contentView);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                String channel_Id = context.getString(R.string.default_notification_channel_id);

                NotificationChannel notificationChannel =
                        new NotificationChannel(channel_Id, "Flashlight", NotificationManager.IMPORTANCE_LOW);
                notificationChannel.setDescription("Flashlight");

                notificationChannel.setLightColor(Color.RED);
                notificationChannel.enableLights(false);

                notificationChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notificationChannel.enableVibration(false);

                notificationManager.createNotificationChannel(notificationChannel);
                builder.setChannelId(channel_Id);
            }

            Notification notification = builder.build();
            notification.flags |= Notification.FLAG_NO_CLEAR;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notificationManager.notify(Config.Notification.ID_INTERACTIVE, notification);
        }
    });
}