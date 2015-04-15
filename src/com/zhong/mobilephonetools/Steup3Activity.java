package com.zhong.mobilephonetools;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * �ֻ������������򵼵ĵ�һ��activity
 * 
 * @author zhong
 *
 */
public class Steup3Activity extends BaseSetupActivity {

	private static final String TAG = "Steup3Activity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steup3);

		Log.i(TAG, "Steup3Activity�û��ɼ�");
	}

	@Override
	public void doNext() {
		Intent intent = new Intent(this, Steup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.translation_next_in, R.anim.translation_next_out);

	}

	@Override
	public void doPrevious() {
		Intent intent = new Intent(this, Steup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.translation_pre_in, R.anim.translation_pre_out);

	}

	protected void onDestroy() {
		Log.i(TAG, "Steup3Activity����");
		super.onDestroy();
	}

}
