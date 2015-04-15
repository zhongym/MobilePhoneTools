package com.zhong.mobilephonetools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * <b>�ֻ������������棺<br>
 * 1:��config��ȡ��lostFindConfigedֵ�������trun�����Ѿ����й��������򵼡�ֱ�ӽ���������<br>
 * 2:�����flase������û�н��й��ֻ����������򵼡������ֻ������򵼽��档<br>
 *
 */
public class LostFindActivity extends Activity {

	private static final String TAG = "LostFindActivity";

	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "LostFindActivity�û��ɼ�");

		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean isLostFindConfiged = sp.getBoolean("lostFindConfiged", false);
		if (isLostFindConfiged) {
			// trun�����Ѿ����й��������򵼡�ֱ�ӽ���������

			setContentView(R.layout.activity_lost_find);

		} else {
			// falseû�н��й��ֻ����������򵼡������ֻ������򵼽��档
			Intent intent = new Intent(this, Steup1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * ���½���������
	 * 
	 * @param view
	 */
	public void reEnterSteup(View view) {
		Intent intent = new Intent(this, Steup1Activity.class);
		startActivity(intent);
	}

	protected void onDestroy() {
		Log.i(TAG, "LostFindActivity����");
		super.onDestroy();
	}
}
