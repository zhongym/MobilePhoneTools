package com.zhong.mobilephonetools;

import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * 手机防盗的设置向导的第一个activity
 * 
 * @author zhong
 *
 */
public class Steup3Activity extends BaseSetupActivity {

	private static final String TAG = "Steup3Activity";
	private EditText et_safeNumber;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steup3);
		Log.i(TAG, "Steup3Activity用户可见");
		et_safeNumber = (EditText) findViewById(R.id.et_safeNumber_steup3);
		et_safeNumber.setText(sp.getString("safeNumber", ""));
	}

	/**
	 * 选择联系人的按钮,打开一个新的界面。点击想要的联系人后返回。
	 * 
	 * @param view
	 */
	public void selectContacts(View view) {
		Intent intent = new Intent(this, selectContactsActivity.class);
		startActivityForResult(intent, 0);

	}

	/**
	 * 处理选择联系人界面 返回数据
	 */
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		String phone = data.getStringExtra("phone");
		if (TextUtils.isEmpty(phone)) {
			return;
		}
		et_safeNumber.setText(phone);
	}

	public void doNext() {
		String safeNumber = et_safeNumber.getText().toString().trim();
		if (TextUtils.isEmpty(safeNumber)) {
			Toast.makeText(this, "请选择安全号码", 0).show();
			return;
		}

		Editor editor = sp.edit();
		editor.putString("safeNumber", safeNumber);
		editor.commit();

		Intent intent = new Intent(this, Steup4Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.translation_next_in, R.anim.translation_next_out);

	}

	public void doPrevious() {
		String safeNumber = et_safeNumber.getText().toString().trim();
		Editor editor = sp.edit();
		editor.putString("safeNumber", safeNumber);
		editor.commit();

		Intent intent = new Intent(this, Steup2Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.translation_pre_in, R.anim.translation_pre_out);

	}

	protected void onDestroy() {
		Log.i(TAG, "Steup3Activity销毁");
		super.onDestroy();
	}

}
