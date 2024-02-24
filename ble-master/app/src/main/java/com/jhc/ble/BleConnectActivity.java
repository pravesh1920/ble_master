package com.jhc.ble;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.cfblelibrary.CFBLEManager;
import com.example.cfblelibrary.callback.IConnectCallback;
import com.example.cfblelibrary.callback.IStopInventoryCallback;
import com.jhc.ble.adapter.BluetoothDeviceAdapter;
import com.jhc.ble.utils.Permission;


import java.util.ArrayList;
import java.util.List;

import cn.wch.blelib.exception.BLELibException;
import cn.wch.blelib.host.core.ConnRuler;
import cn.wch.blelib.host.core.Connection;
import cn.wch.blelib.host.scan.ScanObserver;

public class BleConnectActivity extends AppCompatActivity {
    private static final int REQ_PERMISSION_CODE = 1;
    private static final int PERMISSION_COARSE_LOCATION = 1;

    private boolean isConnected = false;
    private List<BluetoothGattService> serviceList;
    private RecyclerView recyclerview;
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDeviceAdapter deviceAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private Handler handler = new Handler();
    private Runnable stopScanRunnable = this::stopScanBluetoothDevices;
    private Runnable notifyRunnable = new Runnable() {
        @Override
        public void run() {
            deviceAdapter.notifyDataSetChanged();
            handler.postDelayed(this, 500);
        }
    };

    private CFBLEManager cfbleManager;
    private ScanObserver scanObserver = new ScanObserver() {
        @Override
        public void OnScanDevice(BluetoothDevice bluetoothDevice, int i, byte[] bytes) {
            // Log.d("pxw=", "bluetoothDevice.getAddress()=" + bluetoothDevice.getAddress());
            deviceAdapter.addBluetoothDevice(bluetoothDevice);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ble_connect);
        requestPermission();
        //BarUtils.transparentStatusBar(this);
        // Log.d("onCreate=======","onCreate");
        cfbleManager = CFBLEManager.GetInstance();
        try {
            cfbleManager.InitBLE(this.getApplication());
        } catch (BLELibException e) {
            throw new RuntimeException(e);
        }

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        recyclerview = findViewById(R.id.recyclerview);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (!bluetoothAdapter.isEnabled()) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            bluetoothAdapter.enable();
        }else {
            deviceAdapter = new BluetoothDeviceAdapter(new ArrayList<BluetoothDevice>(), BleConnectActivity.this, new BluetoothDeviceAdapter.onClickListener() {
                @Override
                public void onClick(BluetoothDevice device) throws BLELibException {
                    stopScanBluetoothDevices();
                    Log.d("pxw=====","点击的mac地址是="+device.getAddress());
                    if (cfbleManager.IsConnected(device.getAddress())){
                        Log.d("pxw=====","点击的mac地址"+device.getAddress()+"已经连接");
                        //startActivity(new Intent(BleConnectActivity.this, HomeActivity.class));
                    } else {
                        //connectBluetoothDevice(device.getAddress());
                        Intent mIntent = new Intent(BleConnectActivity.this, SplashActivity.class);
                        mIntent.putExtra("_device_address", device.getAddress());
                        startActivity(mIntent);
                        finish();
                    }
                }
            });
            recyclerview.setAdapter(deviceAdapter);

            // 设置下拉刷新监听器
            swipeRefreshLayout.setOnRefreshListener(this::scanBluetoothDevices);

        }
        // 初始化时搜索蓝牙设备
        scanBluetoothDevices();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    private void scanBluetoothDevices() {
        deviceAdapter.clearBluetoothDevices();
        deviceAdapter.notifyDataSetChanged();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        try {
            Log.d("pxw=======","StartScan");
            cfbleManager.StartScan(null,scanObserver);
        } catch (BLELibException e) {
            throw new RuntimeException(e);
        }
        handler.postDelayed(stopScanRunnable, 10000);
        handler.postDelayed(notifyRunnable, 500);
    }
    private void stopScanBluetoothDevices(){
        cfbleManager.StopScan();
        handler.removeCallbacks(stopScanRunnable);
        handler.removeCallbacks(notifyRunnable);
        swipeRefreshLayout.setRefreshing(false);
    }

    protected void connectBluetoothDevice(String mac) {
        ConnRuler connRuler=new ConnRuler.Builder(mac).connectTimeout(10000).build();
        try {
            cfbleManager.Connect(connRuler,connectCallback);
        } catch (BLELibException e) {
            e.printStackTrace();
        }

    }
    private IConnectCallback connectCallback = new IConnectCallback() {
        @Override
        public void OnError(String mac, Throwable t) {

            Log.d("pxw=====","OnError");
        }

        @Override
        public void OnConnecting(String mac) {
            Log.d("pxw=====","OnConnecting");
            //Toast.makeText(BleConnectActivity.this, "正在连接中......", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void OnConnectSuccess(String mac, Connection connection) {

            Log.d("pxw=====","OnConnectSuccess");
            //Toast.makeText(BleConnectActivity.this, "连接成功！", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void OnDiscoverService(String mac, List<BluetoothGattService> list) {
            Log.d("pxw=====","OnDiscoverService");
            startActivity(new Intent(BleConnectActivity.this, HomeActivity.class));
            isConnected = true;
            serviceList = list;
        }

        @Override
        public void OnConnectTimeout(String mac) {
            Log.d("pxw=====","OnDiscoverService");
        }

        @Override
        public void OnDisconnect(String mac, BluetoothDevice bluetoothDevice, int status) {
            Log.d("pxw=====","OnDisconnect");
        }
    };
    private void requestPermission(){
        Permission permission = new Permission();
        permission.checkPermissions(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == Permission.RequestCode) {
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //Log.e("pxw","拒绝的权限名称：" + permissions[i]);
                    //Log.e("pxw","有权限未授权，可以弹框出来，让客户去手机设置界面授权。。。");
                }else {
                    //Log.e("pxw","授权的权限名称：" + permissions[i]);
                }
            }
        }
    }
}