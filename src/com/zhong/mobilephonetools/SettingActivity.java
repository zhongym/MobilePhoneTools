package com.zhong.mobilephonetools;

import com.zhong.mobilephonetools.service.NumberLocationService;
import com.zhong.mobilephonetools.service.SmsCallSafeService;
import com.zhong.mobilephonetools.ui.SettingClickView;
import com.zhong.mobilephonetools.ui.SettingItemView;
import com.zhong.mobilephonetools.utils.ServiceUtils;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

/**
 * ���ý���
 * 
 * @author zhong
 *
 */
public class SettingActivity extends BottomItemActivity {
	private static final String TAG = "SettingActivity";
	/** �Ƿ��Զ�������� **/
	private SettingItemView siv_update;

	/** �Ƿ������������ **/
	private SettingItemView siv_numberlocation;

	/** ��������ʽ **/
	private SettingClickView scv_numberloactionstype;
	
	/**����������**/
	private SettingItemView siv_callsmssafe;

	/** numberLocationService�����ȫ�� **/
	private static String numberLocationServiceName = "com.zhong.mobilephonetools.service.NumberLocationService";
	
	/** SmsCallSafeService�����ȫ�� **/
	private static String CallSmsSafeServiceName = "com.zhong.mobilephonetools.service.SmsCallSafeService";

	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		// �����Ƿ���������
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		updateSetting(sp.getBoolean("update", false));

		// �Ƿ������������
		siv_numberlocation = (SettingItemView) findViewById(R.id.siv_numberlocation);
		numberlocationSetting(ServiceUtils.isServiceRunnign(this, numberLocationServiceName));

		// ��������ʽ
		scv_numberloactionstype = (SettingClickView) findViewById(R.id.scv_numberloactionstype);
		numberloactionStypeSetting();
		
		//����������
		siv_callsmssafe=(SettingItemView) findViewById(R.id.siv_callsmssafe);
		callSmsSafeSetting(ServiceUtils.isServiceRunnign(this, CallSmsSafeServiceName));
		Log.i(TAG, "SettingActivity�û��ɼ�");
	}
	
	// onPause����ý��㣩
	protected void onPause() {
		super.onPause();

		if (ServiceUtils.isServiceRunnign(this, numberLocationServiceName)) {
			siv_numberlocation.setCheck(true);
		} else {
			siv_numberlocation.setCheck(false);
		}
		
		if (ServiceUtils.isServiceRunnign(this,CallSmsSafeServiceName)) {
			siv_callsmssafe.setCheck(true);
		} else {
			siv_callsmssafe.setCheck(false);
		}
	}

	
	/**
	 * ��������������
	 */
	private void callSmsSafeSetting(boolean isServiceRuntting) {
		Log.i(TAG, "SettingActivity.callSmsSafeSetting()----------->��������");

		if (isServiceRuntting) {
			siv_callsmssafe.setCheck(true);
		} else {
			siv_callsmssafe.setCheck(false);
		}

		siv_callsmssafe.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent service = new Intent(SettingActivity.this,SmsCallSafeService.class);

				if (siv_callsmssafe.isChecked()) {
					stopService(service);
					siv_callsmssafe.setCheck(false);
				} else {
					startService(service);
					siv_callsmssafe.setCheck(true);
				}
			}
		});
		
	}

	/**
	 * ��������ʽ����
	 */
	private void numberloactionStypeSetting() {
		scv_numberloactionstype.setTitle("��������ʾ����");
		final String[] items = { "��͸��", "������", "��ʿ��", "������", "ƻ����" };

		scv_numberloactionstype.setDescription(items[sp.getInt("numberloactionstype", 0)]);
		scv_numberloactionstype.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("��������ʾ����");
				builder.setSingleChoiceItems(items, sp.getInt("numberloactionstype", 0),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Editor editor = sp.edit();
								editor.putInt("numberloactionstype", which);
								editor.commit();

								scv_numberloactionstype.setDescription(items[sp.getInt("numberloactionstype", 0)]);
								dialog.dismiss();// �ر�
							}
						});

				builder.setNegativeButton("ȡ��", null);
				builder.show();
			}
		});

	}


	/**
	 * �����Ƿ��������������ʾ
	 * 
	 * @param isServiceRuntting
	 */
	private void numberlocationSetting(boolean isServiceRuntting) {
		Log.i(TAG, "SettingActivity.numberlocationSetting()----------->��������");

		if (isServiceRuntting) {
			siv_numberlocation.setCheck(true);
		} else {
			siv_numberlocation.setCheck(false);
		}

		siv_numberlocation.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				Intent service = new Intent(SettingActivity.this, NumberLocationService.class);

				if (siv_numberlocation.isChecked()) {
					stopService(service);
					siv_numberlocation.setCheck(false);
				} else {
					startService(service);
					siv_numberlocation.setCheck(true);
				}
			}
		});
	}

	/**
	 * �����Զ�����
	 * 
	 * @param isUpdate
	 */
	private void updateSetting(boolean isUpdate) {

		Log.i(TAG, "SettingActivity.updateSetting()----------->��������");

		if (isUpdate) {
			siv_update.setCheck(true);
		} else {
			siv_update.setCheck(false);
		}

		siv_update.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv_update.isChecked()) {// �Ѿ�ѡ��,����ȥ��
					siv_update.setCheck(false);
					editor.putBoolean("update", false);

				} else {// ûѡ,����ѡ��
					siv_update.setCheck(true);
					editor.putBoolean("update", true);
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
