package com.zhong.mobilephonetools;

import com.zhong.mobilephonetools.ui.SettingItemView;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 手机防盗的设置向导的第一个activity
 * 
 * @author zhong
 *
 */
public class Steup2Activity extends BaseSetupActivity {

	private static final String TAG = "Steup2Activity";

	private SettingItemView siv_sim;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_steup2);
		Log.i(TAG, "Steup2Activity用户可见");

		siv_sim = (SettingItemView) findViewById(R.id.siv_sim);

		// 多选框的状态回显
		String simSerialNumber = sp.getString("simSerialNumber", null);
		if (TextUtils.isEmpty(simSerialNumber)) {
			siv_sim.setCheck(false);
		} else {
			siv_sim.setCheck(true);
		}

		siv_sim.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Editor editor = sp.edit();

				if (siv_sim.isChecked()) {
					siv_sim.setCheck(false);

					editor.putString("simSerialNumber", null);
				} else {
					siv_sim.setCheck(true);

					TelephonyManager manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
					// 得到sim卡的序列号（有一些里面没有保存本卡的手机号码）
					String simSerialNumber = manager.getSimSerialNumber();
					editor.putString("simSerialNumber", simSerialNumber);
				}
				editor.commit();
			}
		});

	}

	@Override
	public void doNext() {
		if (!siv_sim.isChecked()) {
			Log.i(TAG, "没有点击 绑定sim卡，不能下一步");
			Toast.makeText(this, "请点击绑定SIM卡", 0).show();
			
			return;
		}

		Intent intent = new Intent(this, Steup3Activity.class);
		startActivity(intent);
		finish();
		overridePendingTransition(R.anim.translation_next_in, R.anim.translation_next_out);

	}

	public void doPrevious() {
		Intent intent = new Intent(this, Steup1Activity.class);
		startActivity(intent);
		finish();
		// 平移退出动画
		overridePendingTransition(R.anim.translation_pre_in, R.anim.translation_pre_out);

	}

	protected void onDestroy() {
		Log.i(TAG, "Steup2Activity销毁");
		super.onDestroy();
	}
}
