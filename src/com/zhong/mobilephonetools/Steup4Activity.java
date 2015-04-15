package com.zhong.mobilephonetools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * 手机防盗的设置向导的第一个activity
 * 
 * @author zhong
 *
 */
public class Steup4Activity extends BaseSetupActivity {

	private static final String TAG = "Steup4Activity";

	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steup4);
		Log.i(TAG, "Steup4Activity用户可见");

		sp = getSharedPreferences("config", MODE_PRIVATE);
	}

	public void doNext() {
		// 完成设置向导，保存纪录
		Editor editor = sp.edit();
		editor.putBoolean("lostFindConfiged", true);
		editor.commit();

		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.translation_next_in, R.anim.translation_next_out);
	}

	@Override
	public void doPrevious() {
		Intent intent = new Intent(this, Steup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.translation_pre_in, R.anim.translation_pre_out);
	}

	protected void onDestroy() {
		Log.i(TAG, "Steup4Activity销毁");
		super.onDestroy();
	}
}
