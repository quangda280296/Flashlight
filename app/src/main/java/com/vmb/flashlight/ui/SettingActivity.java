package com.vmb.flashlight.ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.facebook.CallbackManager;
import com.flash.light.bright.R;
import com.rey.material.widget.Switch;
import com.vmb.ads_in_app.Interface.IOnSendFeedback;
import com.vmb.ads_in_app.handler.AdsHandler;
import com.vmb.ads_in_app.util.OnTouchClickListener;
import com.vmb.ads_in_app.util.PushFeedback;
import com.vmb.ads_in_app.util.ShareRateUtil;
import com.vmb.ads_in_app.util.SharedPreferencesUtil;
import com.vmb.ads_in_app.util.ToastUtil;
import com.vmb.flashlight.Config;
import com.vmb.flashlight.base.BaseActivity;
import com.vmb.flashlight.handler.NotificationHandler;

public class SettingActivity extends BaseActivity implements View.OnClickListener, IOnSendFeedback {
    private CallbackManager callbackManager;

    private LinearLayout sound;
    private LinearLayout noti_bar;

    private ImageView img_back;
    private ImageView img_share;
    private ImageView img_rate;
    private ImageView img_feedback;

    private Switch sw_sound;
    private Switch sw_noti_bar;

    private View layout_feedback;
    private EditText txt_feedback;
    private Button btn_send;

    protected int getResLayout() {
        return R.layout.activity_setting;
    }

    protected void initView() {
        sound = findViewById(R.id.sound);
        noti_bar = findViewById(R.id.noti_bar);

        img_back = findViewById(R.id.img_back);
        img_share = findViewById(R.id.img_share);
        img_rate = findViewById(R.id.img_rate);
        img_feedback = findViewById(R.id.img_feedback);

        sw_sound = findViewById(R.id.sw_sound);
        sw_noti_bar = findViewById(R.id.sw_noti_bar);

        layout_feedback = findViewById(R.id.layout_feedback);
        txt_feedback = findViewById(R.id.txt_feedback);
        btn_send = findViewById(R.id.btn_send);
    }

    protected void initData() {
        callbackManager = CallbackManager.Factory.create();
        final InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        img_back.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        }, getApplicationContext()));

        btn_send.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txt_feedback.getText().toString().length() < 10) {
                    ToastUtil.longToast(getApplicationContext(), getString(R.string.write_at_least));
                    return;
                }
                PushFeedback.push(getApplicationContext(), txt_feedback.getText().toString(), SettingActivity.this,
                        Config.CODE_CONTROL_APP, Config.VERSION_APP, Config.PACKAGE_NAME);
            }
        }, getApplicationContext()));

        sound.setOnClickListener(this);
        noti_bar.setOnClickListener(this);

        // handle switch sound
        sw_sound.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                SharedPreferencesUtil.putPrefferBool(getApplicationContext(), Config.SharePrefferenceKey.SOUND, checked);
            }
        });

        boolean check = SharedPreferencesUtil.
                getPrefferBool(getApplicationContext(), Config.SharePrefferenceKey.SOUND, true);
        if (check)
            sw_sound.setChecked(true);
        else
            sw_sound.setChecked(false);

        // handle switch status bar
        sw_noti_bar.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(Switch view, boolean checked) {
                SharedPreferencesUtil.putPrefferBool(getApplicationContext(), Config.SharePrefferenceKey.STATUS_BAR, checked);
                NotificationHandler handler = new NotificationHandler(getApplicationContext());
                handler.addNotify();
            }
        });

        boolean test = SharedPreferencesUtil.
                getPrefferBool(getApplicationContext(), Config.SharePrefferenceKey.STATUS_BAR, false);
        if (test)
            sw_noti_bar.setChecked(true);
        else
            sw_noti_bar.setChecked(false);

        img_share.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareRateUtil.shareFB(SettingActivity.this, callbackManager,
                        Config.CODE_CONTROL_APP, Config.VERSION_APP, Config.PACKAGE_NAME);
                //ShareRateUtil.shareApp(SettingActivity.this);
            }
        }, getApplicationContext()));

        img_rate.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareRateUtil.rateApp(SettingActivity.this);
            }
        }, getApplicationContext()));

        img_feedback.setOnTouchListener(new OnTouchClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (layout_feedback.getVisibility() == View.INVISIBLE) {
                    layout_feedback.setVisibility(View.VISIBLE);
                    txt_feedback.requestFocus();
                    imm.showSoftInput(txt_feedback, InputMethodManager.SHOW_IMPLICIT);
                } else {
                    layout_feedback.setVisibility(View.INVISIBLE);
                    imm.hideSoftInputFromWindow(txt_feedback.getWindowToken(), 0);
                }
            }
        }, getApplicationContext()));

        AdsHandler.getInstance().displayInterstitial(SettingActivity.this);
        AdsHandler.getInstance().initBanner(SettingActivity.this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (callbackManager != null)
            callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i("onResume()", "onResume()");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sound:
                sw_sound.toggle();
                break;

            case R.id.noti_bar:
                sw_noti_bar.toggle();
                break;
        }
    }

    @Override
    public void onSendFeedback() {
        layout_feedback.setVisibility(View.INVISIBLE);
        txt_feedback.setText("");
        txt_feedback.clearFocus();
    }

    @Override
    public void onBackPressed() {
        Log.i("onKeyBack", "onKeyBack()");
        finish();
        AdsHandler.getInstance().displayInterstitial(SettingActivity.this);
    }
}