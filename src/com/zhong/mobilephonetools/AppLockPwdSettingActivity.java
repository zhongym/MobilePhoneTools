package com.zhong.mobilephonetools;

import com.zhong.mobilephonetools.service.AppLockService;
import com.zhong.mobilephonetools.utils.ServiceUtils;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

public class AppLockPwdSettingActivity extends Activity {

	private TextView tv_edit_pwd;
	private TextView tv_pwd_question;
	private TextView tv_clear_pwd;
	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_app_lock_pwd_setting);

		tv_edit_pwd = (TextView) findViewById(R.id.tv_edit_pwd);
		tv_pwd_question = (TextView) findViewById(R.id.tv_pwd_question);
		tv_clear_pwd = (TextView) findViewById(R.id.tv_clear_pwd);

		sp = getSharedPreferences("config", MODE_PRIVATE);
		tv_edit_pwd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AppLockPwdSettingActivity.this, GuideGesturePasswordActivity.class);
				startActivity(intent);
			}
		});

		tv_clear_pwd.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Editor editor = sp.edit();
				editor.putBoolean("applockpwd", false);
				editor.commit();

				if (ServiceUtils.isServiceRunnign(AppLockPwdSettingActivity.this,
						"com.zhong.mobilephonetools.service.AppLockService")) {
					Intent intent = new Intent(AppLockPwdSettingActivity.this, AppLockService.class);
					stopService(intent);
				}
				App.getInstance().getLockPatternUtils().clearLock();
				Toast.makeText(AppLockPwdSettingActivity.this, "密码已经清空，程序锁服务已关闭", 0).show();
			}
		});
		
		/**
		 * 密保问题界面
		 */
		tv_pwd_question.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(AppLockPwdSettingActivity.this, PwdQuestionActivity.class);
				startActivity(intent);
			}
		});

	}
}
