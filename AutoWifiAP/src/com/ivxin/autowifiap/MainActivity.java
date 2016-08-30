package com.ivxin.autowifiap;

import java.util.ArrayList;
import java.util.Set;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements OnCheckedChangeListener, OnItemClickListener {

	private SharedPreferences sp;
	private ToggleButton tb_switcher;
	private CheckBox cb_charging;
	private CheckBox cb_btd_name;
	private EditText et_ap_name;
	private EditText et_ap_pwd;
	private ListView lv_bt_decices;
	private ArrayList<String> deviceNameList = new ArrayList<>();
	private ArrayList<BluetoothDevice> deviceInfoList = new ArrayList<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sp = getSharedPreferences(AppConstants.SP_FILE_NAME, MODE_PRIVATE);
		setContentView(R.layout.activity_main);
		initView();
		getBTDeviceInfo();
	}

	private void getBTDeviceInfo() {
		BluetoothAdapter mBluetoothAdapter = ((BluetoothManager) getSystemService(BLUETOOTH_SERVICE)).getAdapter();
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		// If there are paired devices
		if (pairedDevices.size() > 0) {
			deviceInfoList.addAll(pairedDevices);
			// Loop through paired devices
			for (BluetoothDevice device : pairedDevices) {
				// Add the name and address to an array adapter to show in a
				// ListView
				deviceNameList.add(device.getName() + "\n" + device.getAddress());
			}
		}
		lv_bt_decices.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, deviceNameList));
	}

	private void initView() {
		tb_switcher = (ToggleButton) findViewById(R.id.tb_switcher);
		cb_charging = (CheckBox) findViewById(R.id.cb_charging);
		cb_btd_name = (CheckBox) findViewById(R.id.cb_btd_name);
		et_ap_name = (EditText) findViewById(R.id.et_ap_name);
		et_ap_pwd = (EditText) findViewById(R.id.et_ap_pwd);
		lv_bt_decices = (ListView) findViewById(R.id.lv_bt_decices);
		boolean isOn = sp.getBoolean(AppConstants.SWITCHER, false);
		if (isOn)
			startedUI();
		else
			stopedUI();
		tb_switcher.setChecked(isOn);
		cb_charging.setChecked(sp.getBoolean(AppConstants.CHECK_CHARGING, true));
		cb_btd_name.setChecked(sp.getBoolean(AppConstants.CHECK_BTDevice, true));
		cb_btd_name.setText(sp.getString(AppConstants.DEVICE_NAME, "BT device"));
		et_ap_name.setText(sp.getString(AppConstants.AP_NAME, "AndroidAP"));
		et_ap_pwd.setText(sp.getString(AppConstants.AP_PASSWORD, "00000001"));

		tb_switcher.setOnCheckedChangeListener(this);
		cb_charging.setOnCheckedChangeListener(this);
		cb_btd_name.setOnCheckedChangeListener(this);
		lv_bt_decices.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		BluetoothDevice device = deviceInfoList.get(position);
		cb_btd_name.setText(device.getName());
		sp.edit().putString(AppConstants.DEVICE_ADDRESS, device.getAddress())
				.putString(AppConstants.DEVICE_NAME, device.getName()).putBoolean(AppConstants.IS_BTD_CONNECTED, false)
				.commit();
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		switch (buttonView.getId()) {
		case R.id.tb_switcher:
			String apName = et_ap_name.getText().toString();
			String apPwd = et_ap_pwd.getText().toString();
			if (apPwd.length() >= 8) {
				if (isChecked) {
					startedUI();
					sp.edit().putString(AppConstants.AP_NAME, apName).putString(AppConstants.AP_PASSWORD, apPwd)
							.putBoolean(AppConstants.SWITCHER, true).commit();
				} else {
					stopedUI();
					sp.edit().putBoolean(AppConstants.SWITCHER, false).commit();
				}
			} else {
				toast("密码最短8位");
				tb_switcher.setChecked(false);
			}
			break;
		case R.id.cb_charging:
			sp.edit().putBoolean(AppConstants.CHECK_CHARGING, isChecked).commit();
			if (!isChecked && !cb_btd_name.isChecked()){
				cb_btd_name.setChecked(true);
				cb_btd_name.callOnClick();
			}
			
			break;
		case R.id.cb_btd_name:
			sp.edit().putBoolean(AppConstants.CHECK_BTDevice, isChecked).commit();
			if (!isChecked && !cb_charging.isChecked()){
				cb_charging.setChecked(true);
				cb_charging.callOnClick();
			}
			break;
		default:
			break;
		}

	}

	private void toast(String string) {
		Toast.makeText(this, string, Toast.LENGTH_SHORT).show();
	}

	private void startedUI() {
		cb_btd_name.setEnabled(false);
		cb_charging.setEnabled(false);
		et_ap_name.setEnabled(false);
		et_ap_pwd.setEnabled(false);
		lv_bt_decices.setOnItemClickListener(null);
	}

	private void stopedUI() {
		cb_btd_name.setEnabled(true);
		cb_charging.setEnabled(true);
		et_ap_name.setEnabled(true);
		et_ap_pwd.setEnabled(true);
		lv_bt_decices.setOnItemClickListener(this);
	}
}
