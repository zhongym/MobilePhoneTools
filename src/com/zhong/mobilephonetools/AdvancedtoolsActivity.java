package com.zhong.mobilephonetools;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class AdvancedtoolsActivity extends BottomItemActivity {

	private static final String TAG = "AdvancedtoolsActivity";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advancedtools);
		Log.i(TAG, "AdvancedtoolsActivity用户可见");
	}
	/**
	 * 号码归属地查询 
	 * @param view
	 */
	public void numberLactionQuery(View view) {
		Intent intent=new Intent(this,NumberLocationActivity.class);
		startActivity(intent);
	}
}
