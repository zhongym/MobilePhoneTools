package com.zhong.mobilephonetools;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * <b>手机防盗的主界面：<br>
 * 1:从config中取出lostFindConfiged值，如果是trun代表已经进行过了设置向导。直接进入主界面<br>
 * 2:如果是flase，代表还没有进行过手机防盗设置向导。进入手机防盗向导界面。<br>
 *
 */
public class LostFindActivity extends Activity {

	private static final String TAG = "LostFindActivity";

	private SharedPreferences sp;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.i(TAG, "LostFindActivity用户可见");

		sp = getSharedPreferences("config", MODE_PRIVATE);
		boolean isLostFindConfiged = sp.getBoolean("lostFindConfiged", false);
		if (isLostFindConfiged) {
			// trun代表已经进行过了设置向导。直接进入主界面
			setContentView(R.layout.activity_lost_find);
			ImageView iv_lock_lostfind = (ImageView) findViewById(R.id.iv_lock_lostfind);

			boolean lostFindStartUp = sp.getBoolean("lostFindStartUp", false);
			if (lostFindStartUp) {
				iv_lock_lostfind.setImageResource(R.drawable.lock);
			}else{
				iv_lock_lostfind.setImageResource(R.drawable.unlock);
			}

		} else {
			// false没有进行过手机防盗设置向导。进入手机防盗向导界面。
			Intent intent = new Intent(this, Steup1Activity.class);
			startActivity(intent);
			finish();
		}
	}

	/**
	 * 重新进入设置向导
	 * 
	 * @param view
	 */
	public void reEnterSteup(View view) {
		Intent intent = new Intent(this, Steup1Activity.class);
		startActivity(intent);
		finish();
	}

	protected void onDestroy() {
		Log.i(TAG, "LostFindActivity销毁");
		super.onDestroy();
	}
}
