package com.zhong.mobilephonetools;

import com.zhong.mobilephonetools.ui.SettingItemView;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * 设置界面
 * @author zhong
 *
 */
public class SettingActivity extends BottomItemActivity {
	private static final String TAG = "SettingActivity";
	private SettingItemView siv_update;
	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		// 设置是否启动更新
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		setting(sp.getBoolean("update", false), siv_update, "update");

		Log.i(TAG, "SettingActivity用户可见");
	}

	/**
	 * 设置中每项的操作抽取
	 * 
	 * @param isUpdate
	 *            保存到xml配置文件的字段的值,用于回显
	 * @param siv
	 *            操作的自定义控件
	 * @param filed
	 *            保存到xml配置文件的字段
	 */
	private void setting(boolean isUpdate, final SettingItemView siv, final String filed) {

		Log.i(TAG, "SettingActivity.setting()----------->被调用了");

		if (isUpdate) {
			siv.setCheck(true);
		} else {
			siv.setCheck(false);
		}

		siv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv.isChecked()) {// 已经选了,将其去掉
					siv.setCheck(false);
					editor.putBoolean(filed, false);

				} else {// 没选,将其选上
					siv.setCheck(true);
					editor.putBoolean(filed, true);
				}
				editor.commit();
			}
		});
	}

	protected void onDestroy() {
		Log.i(TAG, "SettingActivity被销毁");
		super.onDestroy();
	}
}
