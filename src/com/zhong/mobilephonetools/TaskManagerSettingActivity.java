package com.zhong.mobilephonetools;

import com.zhong.mobilephonetools.service.AutoClearProcessService;
import com.zhong.mobilephonetools.utils.ServiceUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class TaskManagerSettingActivity extends Activity {

	private CheckBox cb_showSystem;

	private CheckBox cb_autoClear;

	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task_manager_setting);
		cb_showSystem = (CheckBox) findViewById(R.id.cb_show_system_process);
		cb_autoClear = (CheckBox) findViewById(R.id.cb_screen_off_auth_clear);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		// Êý¾Ý»ØÏÔ
		cb_showSystem.setChecked(sp.getBoolean("showSystemProcess", false));

		cb_autoClear.setChecked(ServiceUtils.isServiceRunnign(this,
				"com.zhong.mobilephonetools.service.AutoClearProcessService"));

		cb_showSystem.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Editor editor = sp.edit();
				editor.putBoolean("showSystemProcess", isChecked);
				editor.commit();
			}
		});

		cb_autoClear.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				Intent intent = new Intent(TaskManagerSettingActivity.this, AutoClearProcessService.class);
				if (isChecked) {
					startService(intent);
				} else {
					stopService(intent);
				}
			}
		});

	}
}
