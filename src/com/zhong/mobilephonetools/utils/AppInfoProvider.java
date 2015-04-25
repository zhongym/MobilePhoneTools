package com.zhong.mobilephonetools.utils;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.zhong.mobilephonetools.domain.AppInfo;

public class AppInfoProvider {
	/**
	 * 获取所有的安装的应用程序信息。
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfos(Context context) {

		PackageManager manager = context.getPackageManager();
		List<PackageInfo> packageInfos = manager.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();

		for (PackageInfo packageInfo : packageInfos) {

			AppInfo appInfo = new AppInfo();

			Drawable icon = packageInfo.applicationInfo.loadIcon(manager);
			String name = (String) packageInfo.applicationInfo.loadLabel(manager);
			String versionName = packageInfo.versionName;
			String packageName = packageInfo.packageName;

			int flags = packageInfo.applicationInfo.flags;// 应用程序信息的标记
															// 相当于用户提交的答卷
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// 用户程序
				appInfo.setUserApp(true);
			} else {
				// 系统程序
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// 手机的内存
				appInfo.setInRom(true);
			} else {
				// 手机外存储设备
				appInfo.setInRom(false);
			}

			appInfo.setName(name);
			appInfo.setPackageName(packageName);
			appInfo.setIcon(icon);
			appInfo.setVersionCode(versionName);
			appInfos.add(appInfo);
		}
		return appInfos;
	}
}
