package com.ivxin.autowifiap;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class BlueToothListener extends BroadcastReceiver {
	public static final String TAG = BlueToothListener.class.getSimpleName();
	private SharedPreferences sp;
	private Context context;

	@Override
	public void onReceive(Context context, Intent intent) {
		this.context = context;
		sp = context.getSharedPreferences(AppConstants.SP_FILE_NAME, Context.MODE_PRIVATE);
		switch (intent.getAction()) {
		case BluetoothAdapter.ACTION_STATE_CHANGED:
			onBTStateChange(intent);
			break;
		case BluetoothDevice.ACTION_BOND_STATE_CHANGED:
			onBTBondStateChange(intent);
			break;
		case BluetoothDevice.ACTION_ACL_CONNECTED:// 监听蓝牙设备连接和连接断开的广播
			onBTDeviceConnected(intent);
			break;
		case BluetoothDevice.ACTION_ACL_DISCONNECTED:
			onBTDeviceDisConnected(intent);
		default:
			break;
		}
	}

	private void onBTDeviceDisConnected(Intent intent) {
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		Log.d(TAG, device.getName() + " ACTION_ACL_DISCONNECTED");

		String spDeviceAddress = sp.getString(AppConstants.DEVICE_ADDRESS, "none");
		if (spDeviceAddress.equals(device.getAddress())) {
			sp.edit().putBoolean(AppConstants.IS_BTD_CONNECTED, false).commit();
			Controller.getInstance(context).check();
		}
	}

	private void onBTDeviceConnected(Intent intent) {
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		Log.d(TAG, device.getName() + " ACTION_ACL_CONNECTED");

		String spDeviceAddress = sp.getString(AppConstants.DEVICE_ADDRESS, "none");
		if (spDeviceAddress.equals(device.getAddress())) {
			sp.edit().putBoolean(AppConstants.IS_BTD_CONNECTED, true).commit();
			Controller.getInstance(context).check();
		}
	}

	private void onBTBondStateChange(Intent intent) {
		BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
		String name = device.getName();
		Log.d(TAG, "device name: " + name);
		int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, -1);
		switch (state) {
		case BluetoothDevice.BOND_NONE:
			Log.d(TAG, "BOND_NONE 删除配对");
			break;
		case BluetoothDevice.BOND_BONDING:
			Log.d(TAG, "BOND_BONDING 正在配对");
			break;
		case BluetoothDevice.BOND_BONDED:
			Log.d(TAG, "BOND_BONDED 配对成功");
			break;
		}
	}

	private void onBTStateChange(Intent intent) {
		int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
		switch (state) {
		case BluetoothAdapter.STATE_OFF:
			Log.d(TAG, "STATE_OFF 手机蓝牙关闭");
			break;
		case BluetoothAdapter.STATE_TURNING_OFF:
			Log.d(TAG, "STATE_TURNING_OFF 手机蓝牙正在关闭");
			break;
		case BluetoothAdapter.STATE_ON:
			Log.d(TAG, "STATE_ON 手机蓝牙开启");
			break;
		case BluetoothAdapter.STATE_TURNING_ON:
			Log.d(TAG, "STATE_TURNING_ON 手机蓝牙正在开启");
			break;
		}
	}

}
