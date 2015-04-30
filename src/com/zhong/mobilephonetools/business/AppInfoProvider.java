package com.zhong.mobilephonetools.business;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;

import com.zhong.mobilephonetools.domain.AppInfo;

public class AppInfoProvider {

	// private long codeSize;
	// private long cacheSize;

	/**
	 * 获取所有的安装的应用程序信息。不包含程序的大小
	 * 
	 * @param context
	 *            上下文
	 * @return
	 */
	public static List<AppInfo> getAppInfosNoSize(Context context) {

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

	/**
	 * 获得程序的信息，包含程序大小
	 * 
	 * @param context
	 * @return
	 */
	public List<AppInfo> getAppInfos(Context context) {

		PackageManager manager = context.getPackageManager();
		List<PackageInfo> packageInfos = manager.getInstalledPackages(0);
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager();
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
			
			//获得应用程序的apk大小
			Method[] methods = PackageManager.class.getMethods();
			Method getPackageSizeInfo = null;
			for (Method method : methods) {
				if ("getPackageSizeInfo".equals(method.getName())) {
					getPackageSizeInfo = method;
				}
			}
			try {
				getPackageSizeInfo.invoke(pm, appInfo.getPackageName(), new CodeSize(appInfo));
			} catch (Exception e) {
				e.printStackTrace();
			}

			appInfos.add(appInfo);
		}
		return appInfos;
	}

	/**
	 * 实现IPackageStatsObserver.Stub，获取程序的大小
	 * 
	 * @author zhong
	 *
	 */
	private class CodeSize extends IPackageStatsObserver.Stub {
		AppInfo info;

		public CodeSize(AppInfo info) {
			super();
			this.info = info;
		}

		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded) throws RemoteException {
			info.setSize(pStats.codeSize);
			// pStats.codeSize //apk的大小
			// pStats.cacheSize;///data/data/包名/cache缓存的大小
			// pStats.dataSize;///data/data/包名/
		}
	}
}
