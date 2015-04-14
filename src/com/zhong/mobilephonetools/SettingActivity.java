package com.zhong.mobilephonetools;

import com.zhong.mobilephonetools.ui.SettingItemView;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

/**
 * ���ý���
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

		// �����Ƿ���������
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		setting(sp.getBoolean("update", false), siv_update, "update");

		Log.i(TAG, "SettingActivity�û��ɼ�");
	}

	/**
	 * ������ÿ��Ĳ�����ȡ
	 * 
	 * @param isUpdate
	 *            ���浽xml�����ļ����ֶε�ֵ,���ڻ���
	 * @param siv
	 *            �������Զ���ؼ�
	 * @param filed
	 *            ���浽xml�����ļ����ֶ�
	 */
	private void setting(boolean isUpdate, final SettingItemView siv, final String filed) {

		Log.i(TAG, "SettingActivity.setting()----------->��������");

		if (isUpdate) {
			siv.setCheck(true);
		} else {
			siv.setCheck(false);
		}

		siv.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv.isChecked()) {// �Ѿ�ѡ��,����ȥ��
					siv.setCheck(false);
					editor.putBoolean(filed, false);

				} else {// ûѡ,����ѡ��
					siv.setCheck(true);
					editor.putBoolean(filed, true);
				}
				editor.commit();
			}
		});
	}

	protected void onDestroy() {
		Log.i(TAG, "SettingActivity������");
		super.onDestroy();
	}
}
