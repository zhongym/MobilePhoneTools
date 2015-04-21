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
 * 设置界面
 * 
 * @author zhong
 *
 */
public class SettingActivity extends BottomItemActivity {
	private static final String TAG = "SettingActivity";
	/** 是否自动更新软件 **/
	private SettingItemView siv_update;

	/** 是否开启号码归属地 **/
	private SettingItemView siv_numberlocation;

	/** 归属地样式 **/
	private SettingClickView scv_numberloactionstype;
	
	/**黑名单拦截**/
	private SettingItemView siv_callsmssafe;

	/** numberLocationService服务的全名 **/
	private static String numberLocationServiceName = "com.zhong.mobilephonetools.service.NumberLocationService";
	
	/** SmsCallSafeService服务的全名 **/
	private static String CallSmsSafeServiceName = "com.zhong.mobilephonetools.service.SmsCallSafeService";

	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		sp = getSharedPreferences("config", MODE_PRIVATE);

		// 设置是否启动更新
		siv_update = (SettingItemView) findViewById(R.id.siv_update);
		updateSetting(sp.getBoolean("update", false));

		// 是否开启号码归属地
		siv_numberlocation = (SettingItemView) findViewById(R.id.siv_numberlocation);
		numberlocationSetting(ServiceUtils.isServiceRunnign(this, numberLocationServiceName));

		// 归属地样式
		scv_numberloactionstype = (SettingClickView) findViewById(R.id.scv_numberloactionstype);
		numberloactionStypeSetting();
		
		//黑名单拦截
		siv_callsmssafe=(SettingItemView) findViewById(R.id.siv_callsmssafe);
		callSmsSafeSetting(ServiceUtils.isServiceRunnign(this, CallSmsSafeServiceName));
		Log.i(TAG, "SettingActivity用户可见");
	}
	
	// onPause（获得焦点）
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
	 * 黑名单拦截设置
	 */
	private void callSmsSafeSetting(boolean isServiceRuntting) {
		Log.i(TAG, "SettingActivity.callSmsSafeSetting()----------->被调用了");

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
	 * 归属地样式设置
	 */
	private void numberloactionStypeSetting() {
		scv_numberloactionstype.setTitle("归属地提示框风格");
		final String[] items = { "半透明", "活力橙", "卫士蓝", "金属灰", "苹果绿" };

		scv_numberloactionstype.setDescription(items[sp.getInt("numberloactionstype", 0)]);
		scv_numberloactionstype.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				AlertDialog.Builder builder = new Builder(SettingActivity.this);
				builder.setTitle("归属地提示框风格");
				builder.setSingleChoiceItems(items, sp.getInt("numberloactionstype", 0),
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int which) {
								Editor editor = sp.edit();
								editor.putInt("numberloactionstype", which);
								editor.commit();

								scv_numberloactionstype.setDescription(items[sp.getInt("numberloactionstype", 0)]);
								dialog.dismiss();// 关闭
							}
						});

				builder.setNegativeButton("取消", null);
				builder.show();
			}
		});

	}


	/**
	 * 设置是否开启号码归属地显示
	 * 
	 * @param isServiceRuntting
	 */
	private void numberlocationSetting(boolean isServiceRuntting) {
		Log.i(TAG, "SettingActivity.numberlocationSetting()----------->被调用了");

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
	 * 设置自动更新
	 * 
	 * @param isUpdate
	 */
	private void updateSetting(boolean isUpdate) {

		Log.i(TAG, "SettingActivity.updateSetting()----------->被调用了");

		if (isUpdate) {
			siv_update.setCheck(true);
		} else {
			siv_update.setCheck(false);
		}

		siv_update.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Editor editor = sp.edit();
				if (siv_update.isChecked()) {// 已经选了,将其去掉
					siv_update.setCheck(false);
					editor.putBoolean("update", false);

				} else {// 没选,将其选上
					siv_update.setCheck(true);
					editor.putBoolean("update", true);
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
