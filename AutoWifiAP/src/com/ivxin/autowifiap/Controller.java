package com.ivxin.autowifiap;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

public class Controller {
	private static Controller instance;

	private Context context;
	private SharedPreferences sp;

	private String ssid;

	private String pwd;

	private boolean isCharging;

	private boolean isBTConnected;

	private Controller(Context context) {
		this.context = context;
		sp = context.getSharedPreferences(AppConstants.SP_FILE_NAME, Context.MODE_PRIVATE);
	}

	public static Controller getInstance(Context context) {
		if (instance == null)
			instance = new Controller(context);
		return instance;
	}

	public void check() {
		boolean isOn = sp.getBoolean(AppConstants.SWITCHER, false);
		isCharging = sp.getBoolean(AppConstants.IS_CHARGING, false);
		isBTConnected = sp.getBoolean(AppConstants.IS_BTD_CONNECTED, false);
		ssid = sp.getString(AppConstants.AP_NAME, "AndroidAP");
		pwd = sp.getString(AppConstants.AP_PASSWORD, "12345678");
		boolean checkCharging = sp.getBoolean(AppConstants.CHECK_CHARGING, false);
		boolean checkBTDevice = sp.getBoolean(AppConstants.CHECK_BTDevice, false);
		if(!checkBTDevice)isBTConnected=true;if(!checkCharging)isCharging=true;
		if (isOn) {
			changeWifiAP();
		}

	}
	private void changeWifiAP() {
		WifiHostBiz biz = new WifiHostBiz(context);
		if (isCharging && isBTConnected) {
			biz.setWIFI_HOST_SSID(ssid);
			biz.setWIFI_HOST_PRESHARED_KEY(pwd);
			biz.setWifiApEnabled(true);
		} else {
			biz.setWifiApEnabled(false);
		}
	}
}
