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
 * �����Զ������̨���̵ķ��� ����һ�������㲥�����ߣ����յ������㲥�������̨����
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
		Log.i(TAG, "AutoClearProcessService������");
		am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		// ע�������㲥�����ߣ������㲥��������һ������Ĺ㲥��ֻ���ڴ���ע�����Ч
		receiver = new ScreenOffReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		registerReceiver(receiver, filter);
	}

	/**
	 * ���������߹㲥
	 *
	 */
	private class ScreenOffReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			// �����̨����
			List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
			for (RunningAppProcessInfo info : infos) {
				am.killBackgroundProcesses(info.processName);
			}
		}

	}

	public void onDestroy() {
		super.onDestroy();
		unregisterReceiver(receiver);// ȡ��ע�������㲥
	}
}
