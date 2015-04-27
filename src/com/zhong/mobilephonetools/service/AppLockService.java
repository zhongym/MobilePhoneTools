package com.zhong.mobilephonetools.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

import com.zhong.mobilephonetools.UnlockGesturePasswordActivity;
import com.zhong.mobilephonetools.dao.AppLockDao;

public class AppLockService extends Service {

	private static final String TAG = "AppLockService";
	private ActivityManager am;
	private AppLockDao dao;
	private boolean flag;
	private List<String> temUnlockAppPackgeNames;
	private UnlockAppReceiver ulaReceiver;
	private ScreenOffReceiver screenoffReceiver;
	private Intent intent;

	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 接收解锁activity解锁后将请求应用包名发过来 com.zhong.unlockApp
	 */
	private class UnlockAppReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				System.out.println("接收到了临时停止保护的广播事件");
				temUnlockAppPackgeNames.add(intent.getStringExtra("packname"));
			}
		}

	}

	/**
	 * 注册锁屏广播接收者，当锁屏了，清空打开程序的历史纪录
	 */
	private class ScreenOffReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			temUnlockAppPackgeNames.clear();
		}
	}

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "AppLockService开启了");
		// 装好锁屏前打开过的锁定程序
		temUnlockAppPackgeNames = new ArrayList<String>();

		// 注册解锁后发过来的广播接收 者
		ulaReceiver = new UnlockAppReceiver();
		IntentFilter filter = new IntentFilter("com.zhong.unlockApp");
		registerReceiver(ulaReceiver, filter);

		// 注册锁屏广播接收者，当锁屏了，清空打开程序的历史纪录
		screenoffReceiver = new ScreenOffReceiver();
		registerReceiver(screenoffReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));

		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		dao = new AppLockDao(this);
		flag = true;

		intent = new Intent(getApplicationContext(), UnlockGesturePasswordActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

		new Thread() {
			public void run() {
				while (flag) {
					List<RunningTaskInfo> infos = am.getRunningTasks(100);
					RunningTaskInfo info = infos.get(0);
					ComponentName componentName = info.topActivity;
					String packname = componentName.getPackageName();
					if (dao.find(packname)) {
						Log.i(TAG, "锁定：packname" + packname);
						if (temUnlockAppPackgeNames.contains(packname)) {// 近其已经使用过，不用锁定了

						} else {
							intent.putExtra("packname", packname);
							startActivity(intent);
						}
					}

					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

	}

	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "AppLockService停止了");

		flag = false;

		unregisterReceiver(ulaReceiver);
		unregisterReceiver(screenoffReceiver);

	}
}
