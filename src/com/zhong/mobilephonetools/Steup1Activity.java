package com.zhong.mobilephonetools;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * �ֻ������������򵼵ĵ�һ��activity
 * 
 * @author zhong
 *
 */
public class Steup1Activity extends BaseSetupActivity {

	private static final String TAG = "Steup1Activity";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steup1);

		Log.i(TAG, "Steup1Activity�û��ɼ�");
	}

	public void doNext() {
		Intent intent = new Intent(this, Steup2Activity.class);
		startActivity(intent);
		finish();
		// ���������startActivity(intent);����finish();ִ�к����
		// overridePendingTransition(int enterAnim, int exitAnim);
		overridePendingTransition(R.anim.translation_next_in, R.anim.translation_next_out);
	}

	@Override
	public void doPrevious() {

	}

	protected void onDestroy() {
		Log.i(TAG, "Steup1Activity����");
		super.onDestroy();
	}

}
