package com.zhong.mobilephonetools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;

/**
 * 手机防盗的设置向导的第一个activity
 * 
 * @author zhong
 *
 */
public class Steup5Activity extends BaseSetupActivity {

	private static final String TAG = "Steup4Activity";

	private SharedPreferences sp;
	private CheckBox cb_startuplostfind;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steup5);
		Log.i(TAG, "Steup5Activity用户可见");
		cb_startuplostfind = (CheckBox) findViewById(R.id.cb_startuplostfind);
		
		sp = getSharedPreferences("config", MODE_PRIVATE);
		
		boolean lostFindStartUp=sp.getBoolean("lostFindStartUp", false);
		if(lostFindStartUp){
			cb_startuplostfind.setChecked(true);
		}else{
			cb_startuplostfind.setChecked(false);
		}
	}

	public void doNext() {
		
		Editor editor = sp.edit();
		if(cb_startuplostfind.isChecked()){
			editor.putBoolean("lostFindStartUp", true);
		}else{
			editor.putBoolean("lostFindStartUp", false);
		}
		// 完成设置向导，保存纪录
		editor.putBoolean("lostFindConfiged", true);
		editor.commit();

		Intent intent = new Intent(this, LostFindActivity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.translation_next_in, R.anim.translation_next_out);
	}

	public void doPrevious() {
		Intent intent = new Intent(this, Steup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.translation_pre_in, R.anim.translation_pre_out);
	}

	protected void onDestroy() {
		Log.i(TAG, "Steup5Activity销毁");
		super.onDestroy();
	}
}
