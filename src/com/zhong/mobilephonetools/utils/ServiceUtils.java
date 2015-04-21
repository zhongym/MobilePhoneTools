package com.zhong.mobilephonetools.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

public class ServiceUtils {

	/**
	 * 检查服务是否开启
	 * @param context Context上下文
	 * @param serviceName 服务的全名
	 * @return 返回是前服务是否开启
	 */
	public static boolean isServiceRunnign(Context context, String serviceName) {

		if (context != null && serviceName != null) {
			ActivityManager mn = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningServiceInfo> infos = mn.getRunningServices(100);

			for (RunningServiceInfo info : infos) {
				if (serviceName.equals(info.service.getClassName())) {
					return true;
				}
			}
		}
		return false;
	}
}
