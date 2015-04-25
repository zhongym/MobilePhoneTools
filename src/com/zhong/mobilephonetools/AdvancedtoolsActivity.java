package com.zhong.mobilephonetools;

import com.zhong.mobilephonetools.utils.SmsUtils;
import com.zhong.mobilephonetools.utils.SmsUtils.BackUpCallBack;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class AdvancedtoolsActivity extends BottomItemActivity {

	private static final String TAG = "AdvancedtoolsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_advancedtools);
		Log.i(TAG, "AdvancedtoolsActivity�û��ɼ�");
	}

	/**
	 * ��������ز�ѯ
	 * 
	 * @param view
	 */
	public void numberLactionQuery(View view) {
		Intent intent = new Intent(this, NumberLocationActivity.class);
		startActivity(intent);
	}

	/**
	 * ���Ż�ԭ
	 */
	public void smsRestore(View view) {
		
		
	}

	/**
	 * ���ű���
	 */
	public void smsBackup(View view) {
		
		final ProgressDialog pDialog = new ProgressDialog(AdvancedtoolsActivity.this);
		pDialog.setMessage("���ڱ���");
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.show();
		
		
		AlertDialog dialog;
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("���ݶ���");
		int count = SmsUtils.smsCount(this);
		builder.setMessage("���ţ��������ţ���" + count + "��");
		builder.setNegativeButton("ȡ��", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setPositiveButton("����", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				try {
					SmsUtils.backupSms(AdvancedtoolsActivity.this, new BackUpCallBack() {

						public void onSmsBackup(int progress) {
							pDialog.setProgress(progress);
						}

						public void beforeBackup(int max) {
							pDialog.setMax(max);
						}
					});

					Toast.makeText(AdvancedtoolsActivity.this, "���ݳɹ�", 0).show();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(AdvancedtoolsActivity.this, "����ʧ��", 0).show();
				}finally{
					pDialog.dismiss();
				}
			}
		});

		dialog = builder.show();
	}
}
