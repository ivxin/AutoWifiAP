package com.ivxin.autowifiap;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

public class BatteryListener extends BroadcastReceiver {
	private SharedPreferences sp;
	public static final String TAG = BatteryListener.class.getSimpleName();

	@Override
	public void onReceive(Context context, Intent batteryStatus) {
		sp = context.getSharedPreferences(AppConstants.SP_FILE_NAME, Context.MODE_PRIVATE);
		String action = batteryStatus.getAction();
		boolean isCharging = false;
		switch (action) {
		case Intent.ACTION_POWER_CONNECTED:
			isCharging=true;
			break;
		case Intent.ACTION_POWER_DISCONNECTED:
			isCharging=false;
			break;
		default:
			break;
		}
		sp.edit().putBoolean(AppConstants.IS_CHARGING, isCharging).commit();
		Controller.getInstance(context).check();
	}

}
