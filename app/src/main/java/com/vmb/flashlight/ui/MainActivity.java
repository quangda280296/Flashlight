package com.vmb.flashlight.ui;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.PointF;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.facebook.CallbackManager;
import com.quangda280296.flashlight.R;
import com.vmb.ads_in_app.Interface.IUpdateNewVersion;
import com.vmb.ads_in_app.handler.AdsHandler;
import com.vmb.ads_in_app.util.FireAnaUtil;
import com.vmb.ads_in_app.util.OnTouchClickListener;
import com.vmb.ads_in_app.util.PermissionUtil;
import com.vmb.ads_in_app.util.ToastUtil;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.adapter.ItemAdapter;
import com.vmb.flashlight.handler.FlashModeHandler;
import com.vmb.flashlight.handler.NotificationHandler;
import com.vmb.flashlight.model.Flashlight;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener, SensorEventListener, IUpdateNewVersion {
    public CallbackManager callbackManager;

    private View root;
    private ViewGroup container;
    //private ViewGroup layout_dialog;
    private ViewGroup layout_compass;

    private ImageView img_switch;
    private ImageView img_setting;
    private ImageView img_compass;
    //private ImageView img_close;

    private TextView lbl_indicator_light;
   /* private TextView lbl_title;
    private TextView lbl_content;*/

    /*private Button btn_a;
    private Button btn_b;
    private Button btn_ok;

    private boolean show_rate = false;
    private boolean require_update = false;*/

    // record the compass picture angle turned
    private float currentDegree = 0f;

    // device sensor manager
    private SensorManager mSensorManager;

    // parameter Toggle button interface
    private int limitY_top = 0;
    private int limitY_bottom;
    private int view_height;

    private int count = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("onCreate()", "onCreate()");

        setContentView(getResLayout());
        initView();
        initData();
    }

    protected int getResLayout() {
        return R.layout.activity_main;
    }

    protected void initView() {
        root = findViewById(R.id.root);
        container = findViewById(R.id.container);
        //layout_dialog = findViewById(R.id.layout_dialog);
        layout_compass = findViewById(R.id.layout_compass);

        img_switch = findViewById(R.id.imb_switch);
        img_setting = findViewById(R.id.img_setting);
        img_compass = findViewById(R.id.img_compass);
        //img_close = findViewById(R.id.img_close);

        /*lbl_title = findViewById(R.id.lbl_title);
        lbl_content = findViewById(R.id.lbl_content);*/
        lbl_indicator_light = findViewById(R.id.lbl_indicator_light);

        /*btn_a = findViewById(R.id.btn_a);
        btn_b = findViewById(R.id.btn_b);
        btn_ok = findViewById(R.id.btn_ok);*/
    }

    protected void initData() {
        //check();
        //GetConfig.init(MainActivity.this, MainActivity.this);

        if (!isFlashSupported()) {
            ToastUtil.longToast(getApplicationContext(), getString(R.string.not_support));
        } else {
            setupBehavior();
            initRecyclerView();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    /*PermissionUtils.requestPermission(MainActivity.this, Config.RequestCode.PERMISSION_CAMERA,
                            Manifest.permission.CAMERA);*/
                } else {
                    Log.i("CheckPermission", "Permission OK");
                    setupCamera();
                }
            } else {
                Log.i("CheckPermission", "<M");
                setupCamera();
            }
        }

        img_setting.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*if (require_update)
                    return;*/
                count = 0;
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        }, getApplicationContext()));

        layout_compass.setOnClickListener(this);

        /*new Thread(new Runnable() {
            @Override
            public void run() {
                CountryCodeUtil.setCountryCode(getApplicationContext(),
                        Config.CODE_CONTROL_APP, Config.VERSION_APP, Config.PACKAGE_NAME);
                callbackManager = CallbackManager.Factory.create();
                PrintKeyHash.print(getApplicationContext());

                int count_play = SharedPreferencesUtil.getPrefferInt(getApplicationContext(),
                        LibrayData.KeySharePrefference.COUNT_PLAY, 0);
                count_play++;
                SharedPreferencesUtil.putPrefferInt(getApplicationContext(),
                        LibrayData.KeySharePrefference.COUNT_PLAY, count_play);
                boolean rate = SharedPreferencesUtil.getPrefferBool(getApplicationContext(),
                        LibrayData.KeySharePrefference.SHOW_RATE, false);
                if (!rate) {
                    if (count_play >= 5)
                        show_rate = true;
                }

                RefreshToken.getInstance().checkSendToken(getApplicationContext(),
                        Config.CODE_CONTROL_APP, Config.VERSION_APP, Config.PACKAGE_NAME);
            }
        }).start();*/

        // initialize your android device sensor capabilities
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        // for the system's orientation sensor registered listeners
        if (mSensorManager != null)
            mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                    SensorManager.SENSOR_DELAY_GAME);
        else
            ToastUtil.longToast(getApplicationContext(), getString(R.string.not_support_compass));

        /*WifiManager wifi = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi.isWifiEnabled())
            FlashModeHandler.getInstance().setWifiOn(true);
        else
            FlashModeHandler.getInstance().setWifiOn(false);*/
    }

    public void check() {
        /*Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            if (!TextUtils.isEmpty(action))
                if (action.equals(Config.ACTION_NOTI)) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            root.setVisibility(View.VISIBLE);
                            GetConfig.init(MainActivity.this, MainActivity.this);
                        }
                    }, 3000);
                    return;
                }
        }*/
    }

    private boolean isFlashSupported() {
        PackageManager pm = getPackageManager();
        if (pm != null)
            return pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        else
            return false;
    }

    public void setupCamera() {
        String TAG = "setupCamera()";

        if (Flashlight.getInstance().getCamera() != null)
            return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            try {
                CameraManager camManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                if (camManager == null) {
                    Log.e(TAG, "camManager == null");
                    ToastUtil.longToast(getApplicationContext(), getString(R.string.failed_camera));
                    return;
                }
                Flashlight.getInstance().setCameraManager(camManager);
                String array[] = camManager.getCameraIdList();
                Log.i(TAG, "length = " + array.length);
                if (array.length > 0)
                    Flashlight.getInstance().setCameraId(camManager.getCameraIdList()[0]);
                else
                    ToastUtil.longToast(getApplicationContext(), getString(R.string.failed_camera));

            } catch (Exception e) {
                ToastUtil.longToast(getApplicationContext(), getString(R.string.failed_camera));
                Log.e(TAG, "Error: " + e.toString());
            }

        } else {
            Camera camera = null;
            try {
                camera = Camera.open();
            } catch (Exception e) {
                Log.e(TAG, "Error: " + e.getMessage());
                ToastUtil.longToast(getApplicationContext(), getString(R.string.failed_camera));
                return;
            }
            if (camera == null) {
                Log.e(TAG, "camera == null");
                ToastUtil.longToast(getApplicationContext(), getString(R.string.failed_camera));
                return;
            }
            Flashlight.getInstance().setCamera(camera);
            Flashlight.getInstance().setParameters(camera.getParameters());
        }
    }

    public void initRecyclerView() {
        final RecyclerView recycler = findViewById(R.id.recycler);

        // If the size of views will not change as the data changes.
        recycler.setHasFixedSize(true);

        // Setting the LayoutManager.
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recycler.setLayoutManager(layoutManager);

        // Setting the adapter.
        ItemAdapter adapter = new ItemAdapter(MainActivity.this);
        recycler.setAdapter(adapter);

        recycler.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int check = FlashModeHandler.getInstance().getIndicator();
                int indicator = recyclerView.computeHorizontalScrollOffset() / recyclerView.getChildAt(0).getMeasuredWidth();
                lbl_indicator_light.setText(indicator + "");

                if (check != indicator) {
                    Flashlight.getInstance().playMoveSound(getApplicationContext());

                    if (indicator == 0 || indicator == 20)
                        Flashlight.getInstance().playEndSound(getApplicationContext());
                }

                FlashModeHandler.getInstance().setIndicator(indicator);

                if (Flashlight.getInstance().isFlashLightOn())
                    FlashModeHandler.getInstance().setMode(MainActivity.this);
            }
        });
    }

    public void setupBehavior() {
        final PointF DownPT = new PointF(); // Record Mouse Position When Pressed Down
        final PointF StartPT = new PointF(); // Record Start Position of 'img'

        container.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressLint("NewApi")
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                final int container_height = container.getHeight();
                limitY_bottom = limitY_top + container_height;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
                    container.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                else
                    container.getViewTreeObserver().removeGlobalOnLayoutListener(this);

                view_height = img_switch.getHeight();
                final int x = (int) img_switch.getX();

                img_switch.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (isFinishing())
                            return false;

                        switch (event.getAction()) {
                            case MotionEvent.ACTION_MOVE:
                                /*if (require_update)
                                    return false;*/
                                count = 0;
                                int testY = (int) (StartPT.y + event.getY() - DownPT.y);

                                if (testY <= limitY_top) {
                                    // Set switch to ON mode position
                                    img_switch.setY(limitY_top);

                                    if (Flashlight.getInstance().isFlashLightOn())
                                        break;

                                    // Turn on flashlight
                                    Flashlight.getInstance().setFlashLightOn(true);
                                    FlashModeHandler.getInstance().setMode(MainActivity.this);
                                    img_switch.setImageResource(R.drawable.img_switch_on);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Flashlight.getInstance().playToggleSound(getApplicationContext());
                                            NotificationHandler handler = new NotificationHandler(getApplicationContext());
                                            handler.addNotify();
                                            FireAnaUtil.logEvent(getApplicationContext(), Config.Event.TURN_ON_FLASH);
                                        }
                                    }).start();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            AdsHandler.getInstance().displayInterstitial(MainActivity.this);
                                        }
                                    }, 1000);

                                    break;
                                }

                                if (testY + view_height >= limitY_bottom) {
                                    // Set switch to OFF mode position
                                    img_switch.setY(limitY_bottom - view_height);

                                    if (!Flashlight.getInstance().isFlashLightOn())
                                        break;

                                    // Turn off flashlight
                                    Flashlight.getInstance().setFlashLightOn(false);
                                    Flashlight.getInstance().toggle(false);
                                    img_switch.setImageResource(R.drawable.img_switch_off);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Flashlight.getInstance().playToggleSound(getApplicationContext());
                                            NotificationHandler handler = new NotificationHandler(getApplicationContext());
                                            handler.addNotify();
                                            FireAnaUtil.logEvent(getApplicationContext(), Config.Event.TURN_OFF_FLASH);
                                        }
                                    }).start();

                                    new Handler().postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            AdsHandler.getInstance().displayInterstitial(MainActivity.this);
                                        }
                                    }, 1000);

                                    break;
                                }

                                img_switch.setY(testY);
                                StartPT.set(x, testY);
                                break;

                            case MotionEvent.ACTION_DOWN:
                                /*if (require_update)
                                    return false;*/
                                count = 0;
                                DownPT.set(event.getX(), event.getY());
                                StartPT.set(img_switch.getX(), img_switch.getY());
                                break;

                            case MotionEvent.ACTION_UP:
                                /*if (require_update)
                                    return false;*/
                                count = 0;
                                /*if (show_rate)
                                    showRate();*/
                                int Y = (int) img_switch.getY();
                                if ((Y - limitY_top + view_height / 2) <= (container_height / 2)) {
                                    // Set switch to ON mode position
                                    img_switch.setY(limitY_top);

                                    if (Flashlight.getInstance().isFlashLightOn())
                                        break;

                                    // Turn on flashlight
                                    Flashlight.getInstance().setFlashLightOn(true);
                                    FlashModeHandler.getInstance().setMode(MainActivity.this);
                                    img_switch.setImageResource(R.drawable.img_switch_on);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Flashlight.getInstance().playToggleSound(getApplicationContext());
                                            NotificationHandler handler = new NotificationHandler(getApplicationContext());
                                            handler.addNotify();
                                            FireAnaUtil.logEvent(getApplicationContext(), Config.Event.TURN_ON_FLASH);
                                        }
                                    }).start();

                                } else {
                                    // Set switch to OFF mode position
                                    img_switch.setY(limitY_bottom - view_height);

                                    if (!Flashlight.getInstance().isFlashLightOn())
                                        break;

                                    // Turn off flashlight
                                    Flashlight.getInstance().setFlashLightOn(false);
                                    Flashlight.getInstance().toggle(false);
                                    img_switch.setImageResource(R.drawable.img_switch_off);

                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            Flashlight.getInstance().playToggleSound(getApplicationContext());
                                            NotificationHandler handler = new NotificationHandler(getApplicationContext());
                                            handler.addNotify();
                                            FireAnaUtil.logEvent(getApplicationContext(), Config.Event.TURN_OFF_FLASH);
                                        }
                                    }).start();
                                }
                                break;

                            default:
                                break;
                        }

                        return true;
                    }
                });

                if (Flashlight.getInstance().isFlashLightOn()) {
                    // Set switch to ON mode position
                    img_switch.setImageResource(R.drawable.img_switch_on);
                    img_switch.setY(limitY_top);
                } else {
                    // Set switch to OFF mode position
                    img_switch.setImageResource(R.drawable.img_switch_off);
                    img_switch.setY(limitY_bottom - view_height);
                }
            }
        });
    }

    /*public void showRate() {
        show_rate = false;
        SharedPreferencesUtil.putPrefferBool(getApplicationContext(), LibrayData.KeySharePrefference.SHOW_RATE, true);

        lbl_title.setText(R.string.rate_title);
        lbl_content.setText(R.string.rate_content);

        btn_ok.setVisibility(View.GONE);

        btn_a.setText(R.string.share);
        btn_a.setVisibility(View.VISIBLE);
        btn_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    ToastUtil.shortToast(getApplicationContext(), getString(R.string.no_internet));
                    return;
                }
                *//*ShareRateUtil.showShareFB(MainActivity.this, callbackManager,
                        Config.CODE_CONTROL_APP, Config.VERSION_APP, Config.PACKAGE_NAME);*//*
                ShareRateUtil.shareApp(MainActivity.this);
            }
        });

        btn_b.setText(R.string.rate);
        btn_b.setVisibility(View.VISIBLE);
        btn_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!NetworkUtil.isNetworkAvailable(getApplicationContext())) {
                    ToastUtil.shortToast(getApplicationContext(), getString(R.string.no_internet));
                    return;
                }
                ShareRateUtil.rateApp(MainActivity.this);
            }
        });

        img_close.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_dialog.setVisibility(View.GONE);
            }
        }, getApplicationContext()));

        layout_dialog.setVisibility(View.VISIBLE);
    }*/

    /*public void showUpdate() {
        String title = AdsConfig.getInstance().getUpdate_title();
        if (TextUtils.isEmpty(title))
            title = "Update";

        String content = AdsConfig.getInstance().getUpdate_message();
        if (TextUtils.isEmpty(content))
            content = "There is a new version, please update soon !";

        lbl_title.setText(title);
        lbl_content.setText(content);

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = AdsConfig.getInstance().getUpdate_url();
                if (TextUtils.isEmpty(url))
                    url = "https://play.google.com/store/apps/developer?id=Fruit+Game+Studio";

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivityForResult(intent, LibrayData.RequestCode.REQUEST_CODE_UPDATE);
            }
        });

        img_close.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (require_update)
                    return;

                layout_dialog.setVisibility(View.GONE);
            }
        }, getApplicationContext()));

        layout_dialog.setVisibility(View.VISIBLE);
    }*/

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.layout_compass:
                /*if (require_update)
                    return;*/
                count = 0;
                startActivity(new Intent(MainActivity.this, CompassActivity.class));
                FireAnaUtil.logEvent(getApplicationContext(), Config.Event.TURN_ON_COMPASS);
                break;

            default:
                break;
        }
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case Config.RequestCode.PERMISSION_CAMERA:
                if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                        != PackageManager.PERMISSION_GRANTED) {
                    ToastUtil.longToast(getApplicationContext(), getString(R.string.warning_request_permission));
                    PermissionUtil.checkPermissionRationale(MainActivity.this,
                            Manifest.permission.CAMERA);

                } else {
                    setupCamera();
                    if (Flashlight.getInstance().isFlashLightOn())
                        FlashModeHandler.getInstance().setMode(MainActivity.this);
                }
                break;

            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume()", "onResume()");

        if (FlashModeHandler.getInstance().getLaunch().equals("COMPASS"))
            startActivity(new Intent("COMPASS"));

        if (limitY_bottom != 0 && view_height != 0) {
            if (Flashlight.getInstance().isFlashLightOn()) {
                // Set switch to ON mode position
                img_switch.setImageResource(R.drawable.img_switch_on);
                img_switch.setY(limitY_top);

                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
                        PackageManager.PERMISSION_GRANTED) {
                    PermissionUtil.requestPermission(MainActivity.this, Config.RequestCode.PERMISSION_CAMERA,
                            Manifest.permission.CAMERA);
                } else {
                    // do something
                }
            } else {
                // Set switch to OFF mode position
                img_switch.setImageResource(R.drawable.img_switch_off);
                img_switch.setY(limitY_bottom - view_height);
            }
        } else {
            // do something
        }

        NotificationHandler handler = new NotificationHandler(getApplicationContext());
        handler.addNotify();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i("onPause()", "onPause()");
    }

    @Override
    protected void onDestroy() {
        // to stop the listener and save battery
        if (mSensorManager != null)
            mSensorManager.unregisterListener(this);

        AdsHandler.getInstance().destroyInstance();
        if (!Flashlight.getInstance().isFlashLightOn()) {
            if (Flashlight.getInstance().getCamera() != null) {
                Flashlight.getInstance().getCamera().stopPreview();
                Flashlight.getInstance().getCamera().release();
                Flashlight.getInstance().setInstance(null);
            }
        }
        FlashModeHandler.getInstance().setInstance(null);

        NotificationHandler handler = new NotificationHandler(getApplicationContext());
        handler.addNotify();

        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.i("onKeyBack", "onKeyBack()");
        count++;
        if (count >= 2)
            finish();
        else
            ToastUtil.shortToast(getApplicationContext(), "Press again to exit");
        /*if (findViewById(R.id.layout_dialog).getVisibility() == View.VISIBLE) {
            if (require_update)
                return;

            findViewById(R.id.layout_dialog).setVisibility(View.GONE);
            return;
        }*/

        /*if (Flashlight.getInstance().isFlashLightOn())
            AdsHandler.getInstance().showCofirmDialog(MainActivity.this, new AdsHandler.ExitDialogListener() {
                @Override
                public void onClickButton(boolean yes) {
                    if(yes)
                        showTurn();
                }
            });
        else
            AdsHandler.getInstance().showCofirmDialog(MainActivity.this);*/
    }

    /*public void showTurn() {
        lbl_title.setText(R.string.confirm);
        lbl_content.setText(R.string.are_you_sure);

        btn_ok.setVisibility(View.GONE);

        btn_a.setText(R.string.yes);
        btn_a.setVisibility(View.VISIBLE);
        btn_a.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Turn off flashlight
                Flashlight.getInstance().setFlashLightOn(false);
                Flashlight.getInstance().toggle(false);
                finish();
            }
        });

        btn_b.setText(R.string.no);
        btn_b.setVisibility(View.VISIBLE);
        btn_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        img_close.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_dialog.setVisibility(View.GONE);
            }
        }, getApplicationContext()));

        layout_dialog.setVisibility(View.VISIBLE);
    }*/

    @Override
    public void onGetConfig(boolean require_update) {
        /*this.require_update = require_update;
        showUpdate();*/
    }
}