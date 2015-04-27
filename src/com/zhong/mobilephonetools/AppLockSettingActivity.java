package com.zhong.mobilephonetools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.zhong.mobilephonetools.service.AppLockService;
import com.zhong.mobilephonetools.ui.SettingItemView;
import com.zhong.mobilephonetools.utils.ServiceUtils;

/**
 * 程序锁的设置界面
 */
public class AppLockSettingActivity extends Activity {

	private SettingItemView siv_starup;
	private TextView tv_pwd;
	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock_setting);

		tv_pwd = (TextView) findViewById(R.id.tv_pwd);
		siv_starup = (SettingItemView) findViewById(R.id.siv_starup);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		appLockStarupSetting(ServiceUtils.isServiceRunnign(this, "com.zhong.mobilephonetools.service.AppLockService"));

		tv_pwd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AppLockSettingActivity.this, AppLockPwdSettingActivity.class);
				startActivity(intent);
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (ServiceUtils.isServiceRunnign(this, "com.zhong.mobilephonetools.service.AppLockService")) {
			siv_starup.setCheck(true);
		} else {
			siv_starup.setCheck(false);
		}
	}
	
	/**
	 * 设置程序锁开启和关闭条目的点击事件
	 * 
	 * @param isRunning
	 */
	private void appLockStarupSetting(boolean isRunning) {
		if (isRunning) {
			siv_starup.setCheck(true);
		} else {
			siv_starup.setCheck(false);
		}

		siv_starup.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AppLockSettingActivity.this, AppLockService.class);
				if (siv_starup.isChecked()) {
					stopService(intent);
					siv_starup.setCheck(false);
				} else {
					if (sp.getBoolean("applockpwd", false)) {
						startService(intent);
						siv_starup.setCheck(true);
					} else {
						Toast.makeText(AppLockSettingActivity.this, "请选设置图案密码再开启服务", 0).show();
					}
				}
			}
		});
	}

}
