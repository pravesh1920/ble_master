package com.jhc.ble;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.os.Bundle;

import com.airbnb.lottie.LottieAnimationView;
import com.blankj.utilcode.util.ToastUtils;
import com.example.cfblelibrary.CFBLEManager;
import com.example.cfblelibrary.callback.IConnectCallback;
import com.example.cfblelibrary.callback.IStopInventoryCallback;

import java.util.List;

import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.host.core.ConnRuler;
import cn.wch.blelib.host.core.Connection;

public class SplashActivity extends AppCompatActivity {

    private LottieAnimationView lav_splash_lottie;
    private CFBLEManager cfbleManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        initData();
        lav_splash_lottie = findViewById(R.id.lav_splash_lottie);

        lav_splash_lottie.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
                super.onAnimationCancel(animation);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                lav_splash_lottie.removeAnimatorListener(this);
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                super.onAnimationRepeat(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }

            @Override
            public void onAnimationPause(Animator animation) {
                super.onAnimationPause(animation);
            }

            @Override
            public void onAnimationResume(Animator animation) {
                super.onAnimationResume(animation);
            }
        });
    }

    private void initData() {
        cfbleManager = CFBLEManager.GetInstance();
        try {
            cfbleManager.InitBLE(this.getApplication());
        } catch (BLELibException e) {
            throw new RuntimeException(e);
        }

        Intent intent = getIntent();
        connectBluetoothDevice(intent.getStringExtra("_device_address"));
    }

    protected void connectBluetoothDevice(String mac) {
        ConnRuler connRuler=new ConnRuler.Builder(mac).connectTimeout(10000).build();
        try {
            cfbleManager.Connect(connRuler, new IConnectCallback() {
                @Override
                public void OnError(String mac, Throwable t) {
                    ToastUtils.showShort("连接失败");
                }

                @Override
                public void OnConnecting(String mac) {

                }

                @Override
                public void OnConnectSuccess(String mac, Connection connection) {
                }

                @Override
                public void OnDiscoverService(String mac, List<BluetoothGattService> list) {
                    //发现服务
                    startActivity(new Intent(SplashActivity.this, HomeActivity.class));
                    finish();
                }

                @Override
                public void OnConnectTimeout(String mac) {

                }

                @Override
                public void OnDisconnect(String mac, BluetoothDevice bluetoothDevice, int status) {

                }
            });
        } catch (BLELibException e) {
            e.printStackTrace();
        }
    }
}