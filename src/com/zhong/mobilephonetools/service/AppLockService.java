package com.zhong.mobilephonetools.service;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.Service;
import android.content.BroadcastReceiver;
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
	 * ���ս���activity����������Ӧ�ð��������� com.zhong.unlockApp
	 */
	private class UnlockAppReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			if (intent != null) {
				System.out.println("���յ�����ʱֹͣ�����Ĺ㲥�¼�");
				temUnlockAppPackgeNames.add(intent.getStringExtra("packname"));
			}
		}

	}

	/**
	 * ע�������㲥�����ߣ��������ˣ���մ򿪳������ʷ��¼
	 */
	private class ScreenOffReceiver extends BroadcastReceiver {
		public void onReceive(Context context, Intent intent) {
			temUnlockAppPackgeNames.clear();
		}
	}

	public void onCreate() {
		super.onCreate();
		Log.i(TAG, "AppLockService������");
		// װ������ǰ�򿪹�����������
		temUnlockAppPackgeNames = new ArrayList<String>();

		// ע������󷢹����Ĺ㲥���� ��
		ulaReceiver = new UnlockAppReceiver();
		IntentFilter filter = new IntentFilter("com.zhong.unlockApp");
		registerReceiver(ulaReceiver, filter);

		// ע�������㲥�����ߣ��������ˣ���մ򿪳������ʷ��¼
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
					List<RunningTaskInfo> infos = am.getRunningTasks(1);
					String packname = infos.get(0).topActivity.getPackageName();
					Log.i(TAG, packname);
					if (dao.find(packname)) {
						Log.i(TAG, "������packname" + packname);
						if (temUnlockAppPackgeNames.contains(packname)) {// �����Ѿ�ʹ�ù�������������

						} else {
							intent.putExtra("packname", packname);
							startActivity(intent);
						}
					}

					try {
						Thread.sleep(20);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			};
		}.start();

	}

	public void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "AppLockServiceֹͣ��");

		flag = false;

		unregisterReceiver(ulaReceiver);
		unregisterReceiver(screenoffReceiver);

	}
}
