package com.zhong.mobilephonetools;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.zhong.mobilephonetools.receiver.MyAdmin;

/**
 * 手机防盗的设置向导的第一个activity
 * 
 * @author zhong
 *
 */
public class Steup4Activity extends BaseSetupActivity {

	private static final String TAG = "Steup3Activity";
	private EditText et_lockScreenPwd;
	private DevicePolicyManager dpm;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steup4);
		Log.i(TAG, "Steup4Activity用户可见");
		et_lockScreenPwd = (EditText) findViewById(R.id.et_lockScreenPwd_steup4);
		et_lockScreenPwd.setText(sp.getString("lockScreenPwd", ""));
	    dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
	    

		ComponentName  mDeviceAdminSample = new ComponentName(this,MyAdmin.class);
		if(!dpm.isAdminActive(mDeviceAdminSample)){
			Intent intent1 = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
			//我要激活谁
			
			intent1.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mDeviceAdminSample);
		   //劝说用户开启管理员权限
			intent1.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,"激活权限方可使用");
			startActivity(intent1);
		}
	}

	public void doNext() {
		String lockScreenPwd = et_lockScreenPwd.getText().toString().trim();
		if (TextUtils.isEmpty(lockScreenPwd)) {
			Toast.makeText(this, "请输入锁屏密码", 0).show();
			return;
		}
		
		Editor editor = sp.edit();
		editor.putString("lockScreenPwd", lockScreenPwd);
		editor.commit();

		Intent intent = new Intent(this, Steup5Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.translation_next_in, R.anim.translation_next_out);

	}

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
