package com.zhong.mobilephonetools.service;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.util.Log;

/**
 * 锁屏自动清理后台进程的服务 创建一个锁屏广播接收者，接收到锁屏广播后清理后台进程
 */
public class AutoClearProcessService extends Service {

	private static final String TAG = "AutoClearProcessService";
	
	private ScreenOffReceiver receiver;
	
	private ActivityManager am;

	public IBinder onBind(Intent intent) {
		return null;
	}

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "AutoClearProcessService开启了");
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// 注册锁屏广播接收者，锁屏广播接收者是一个特殊的广播。只能在代码注册才生效
		receiver = new ScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
	}

	/**
	 * 锁屏接收者广播
	 *
	 */
	private class ScreenOffReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			// 清理后台进程
			List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
			for (RunningAppProcessInfo info : infos) {
				am.killBackgroundProcesses(info.processName);
			}
		}

	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);// 取消注册锁屏广播
	}
}
