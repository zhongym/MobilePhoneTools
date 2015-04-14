package com.zhong.mobilephonetools;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

/**
 * 手机防盗的设置向导的第一个activity
 * @author zhong
 *
 */
public class Steup1Activity extends Activity {

	private static final String TAG = "Steup1Activity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steup1);
		
		Log.i(TAG, "Steup1Activity用户可见");
	}
	
	@Override
	protected void onDestroy() {
		Log.i(TAG, "Steup1Activity销毁");
		super.onDestroy();
	}
	
}
