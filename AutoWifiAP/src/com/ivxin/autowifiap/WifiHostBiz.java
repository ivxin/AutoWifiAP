package com.ivxin.autowifiap;

import java.lang.reflect.Method;

import android.content.Context;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;

/**
 * WIFI热点业务类
 * 
 * @author wlh
 *
 */
public class WifiHostBiz {

	private final String TAG = "WifiHostBiz";
	private WifiManager wifiManager;
	private String WIFI_HOST_SSID = "AndroidAP";
	private String WIFI_HOST_PRESHARED_KEY = "12345678";// 密码必须大于8位数

	public WifiHostBiz(Context context) {
		// 获取wifi管理服务
		wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
	}

	public void setWIFI_HOST_SSID(String wIFI_HOST_SSID) {
		WIFI_HOST_SSID = wIFI_HOST_SSID;
	}

	public void setWIFI_HOST_PRESHARED_KEY(String wIFI_HOST_PRESHARED_KEY) {
		WIFI_HOST_PRESHARED_KEY = wIFI_HOST_PRESHARED_KEY;
	}

	/** 判断热点开启状态 */
	public boolean isWifiApEnabled() {
		return getWifiApState() == WIFI_AP_STATE.WIFI_AP_STATE_ENABLED;
	}

	private WIFI_AP_STATE getWifiApState() {
		int tmp;
		try {
			Method method = wifiManager.getClass().getMethod("getWifiApState");
			tmp = ((Integer) method.invoke(wifiManager));
			// Fix for Android 4
			if (tmp > 10) {
				tmp = tmp - 10;
			}
			return WIFI_AP_STATE.class.getEnumConstants()[tmp];
		} catch (Exception e) {
			e.printStackTrace();
			return WIFI_AP_STATE.WIFI_AP_STATE_FAILED;
		}
	}

	public enum WIFI_AP_STATE {
		WIFI_AP_STATE_DISABLING, WIFI_AP_STATE_DISABLED, WIFI_AP_STATE_ENABLING, WIFI_AP_STATE_ENABLED, WIFI_AP_STATE_FAILED
	}

	/**
	 * wifi热点开关
	 * 
	 * @param enabled
	 *            true：打开 false：关闭
	 * @return true：成功 false：失败
	 */
	public boolean setWifiApEnabled(boolean enabled) {
		// disable WiFi in any case
		// wifi和热点不能同时打开，所以打开热点的时候需要关闭wifi
		wifiManager.setWifiEnabled(!enabled);
		System.out.println(TAG + ":关闭wifi");
		System.out.println(TAG + ":开启热点");
		// 热点的配置类
		WifiConfiguration apConfig = new WifiConfiguration();
		// 配置热点的名称(可以在名字后面加点随机数什么的)
		apConfig.SSID = WIFI_HOST_SSID;
		// 配置热点的密码
		apConfig.preSharedKey = WIFI_HOST_PRESHARED_KEY;
		// 安全：WPA2_PSK
		apConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);

		try {
			// 通过反射调用设置热点
			Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
			// 返回热点打开状态
			boolean flag = (Boolean) method.invoke(wifiManager, apConfig, enabled);
			if (!enabled)
				wifiManager.setWifiEnabled(true);
			return flag;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}
}
