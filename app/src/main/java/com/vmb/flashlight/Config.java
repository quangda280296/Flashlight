package com.vmb.flashlight;

import com.quangda280296.flashlight.BuildConfig;

public class Config {
    public static final String PACKAGE_NAME = BuildConfig.APPLICATION_ID;
    public static final String VERSION_APP = BuildConfig.VERSION_NAME;
    public static final String CODE_CONTROL_APP = "11384";
    public static final String ACTION_NOTI = "noti";

    public class RequestCode {
        public static final int PERMISSION_CAMERA = 0;
        public static final int COMPASS = 11;
        public static final int FLASH = 12;
        public static final int WIFI = 13;
        public static final int GPRS = 14;
        public static final int ICON_INTERACT_NOTI = 15;
    }

    public class SharePrefferenceKey {
        public static final String SOUND = "sound";
        public static final String STATUS_BAR = "status_bar";
    }

    public class Notification {
        public static final int ID_INTERACTIVE = 2;
    }

    public class Event {
        public static final String TURN_ON_FLASH = "turn_on_flash";
        public static final String TURN_OFF_FLASH = "turn_off_flash";
        public static final String TURN_ON_COMPASS = "turn_on_flash";
    }
}