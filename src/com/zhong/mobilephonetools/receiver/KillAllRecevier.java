package com.zhong.mobilephonetools.receiver;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * 接收桌面小控件一键清理发送过来的广播，进行处理
 *
 */
public class KillAllRecevier extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		System.out.println("自定义的广播消息接收到了..");
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();
		for (RunningAppProcessInfo info : infos) {
			am.killBackgroundProcesses(info.processName);
		}
	}
}
