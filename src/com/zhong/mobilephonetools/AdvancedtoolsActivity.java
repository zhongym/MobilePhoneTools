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
		Log.i(TAG, "AdvancedtoolsActivity用户可见");
	}

	/**
	 * 号码归属地查询
	 * 
	 * @param view
	 */
	public void numberLactionQuery(View view) {
		Intent intent = new Intent(this, NumberLocationActivity.class);
		startActivity(intent);
	}

	/**
	 * 短信还原
	 */
	public void smsRestore(View view) {
		
		
	}

	/**
	 * 短信备份
	 */
	public void smsBackup(View view) {
		
		final ProgressDialog pDialog = new ProgressDialog(AdvancedtoolsActivity.this);
		pDialog.setMessage("正在备份");
		pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		pDialog.show();
		
		
		AlertDialog dialog;
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle("备份短信");
		int count = SmsUtils.smsCount(this);
		builder.setMessage("短信（不含彩信）：" + count + "条");
		builder.setNegativeButton("取消", new OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.setPositiveButton("备份", new OnClickListener() {
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

					Toast.makeText(AdvancedtoolsActivity.this, "备份成功", 0).show();
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(AdvancedtoolsActivity.this, "备份失败", 0).show();
				}finally{
					pDialog.dismiss();
				}
			}
		});

		dialog = builder.show();
	}
}
