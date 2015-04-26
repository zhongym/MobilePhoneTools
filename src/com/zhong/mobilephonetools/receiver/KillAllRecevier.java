package com.zhong.mobilephonetools.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * ��������С�ؼ�һ�������͹����Ĺ㲥�����д���
 *
 */
public class KillAllRecevier extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		System.out.println("�Զ���Ĺ㲥��Ϣ���յ���..");
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : infos) {
			am.killBackgroundProcesses(info.processName);
		}
	}
}
