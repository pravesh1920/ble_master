package com.jhc.ble.adapter;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.pm.PackageManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;


import com.jhc.ble.R;

import java.util.List;

import cn.wch.blelib.exception.BLELibException;

public class BluetoothDeviceAdapter extends RecyclerView.Adapter<BluetoothDeviceAdapter.ViewHolder> {
    private List<BluetoothDevice> deviceList;
    private Context mContext;

    private onClickListener onClickListener;

    // 构造方法，传入蓝牙设备列表
    public BluetoothDeviceAdapter(List<BluetoothDevice> devices, Context mContext, onClickListener onClickListener) {
        this.deviceList = devices;
        this.mContext = mContext;
        this.onClickListener=onClickListener;
    }

    // ViewHolder类，用于缓存View组件
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView deviceNameTextView;
        public TextView deviceAddressTextView;

        public ViewHolder(View itemView) {
            super(itemView);
            deviceNameTextView = itemView.findViewById(R.id.name);
            deviceAddressTextView = itemView.findViewById(R.id.mac);
        }
    }

    // 创建ViewHolder
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_devices, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    // 绑定ViewHolder并设置数据
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        BluetoothDevice device = deviceList.get(position);
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO 检测蓝牙权限
        }
        //Log.d("pxw=====","显示蓝牙数据=");
        holder.deviceNameTextView.setText(device.getName());
        holder.deviceAddressTextView.setText(device.getAddress());
        holder.itemView.setOnClickListener(view -> {
            if(onClickListener!=null){
                try {
                    onClickListener.onClick(device);
                } catch (BLELibException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    // 获取列表项数量
    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    public void addBluetoothDevice(BluetoothDevice device){
        if(deviceList==null){
            return;
        }
        for (int i=0;i<deviceList.size();i++) {
            BluetoothDevice bluetoothDevice=deviceList.get(i);
            if(bluetoothDevice.getAddress().equalsIgnoreCase(device.getAddress())){
                //更新rssi值
/*                rssiMap.put(device.getAddress(),rssi);
                notifyItemChanged(i,new RSSIMSG(bluetoothDevice.getAddress(),rssi));
                return;*/
                return;
            }
        }
        deviceList.add(device);
        notifyItemInserted(getItemCount());
    }
    public void clearBluetoothDevices(){
        deviceList.clear();
        notifyItemInserted(getItemCount());
    }
    public interface onClickListener{
        void onClick(BluetoothDevice device) throws BLELibException;
    }
}
