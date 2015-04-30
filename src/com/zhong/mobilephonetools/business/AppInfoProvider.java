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
	 * ��ȡ���еİ�װ��Ӧ�ó�����Ϣ������������Ĵ�С
	 * 
	 * @param context
	 *            ������
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

			int flags = packageInfo.applicationInfo.flags;// Ӧ�ó�����Ϣ�ı��
			// �൱���û��ύ�Ĵ��
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// �û�����
				appInfo.setUserApp(true);
			} else {
				// ϵͳ����
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// �ֻ����ڴ�
				appInfo.setInRom(true);
			} else {
				// �ֻ���洢�豸
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
	 * ��ó������Ϣ�����������С
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

			int flags = packageInfo.applicationInfo.flags;// Ӧ�ó�����Ϣ�ı��
															// �൱���û��ύ�Ĵ��
			if ((flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
				// �û�����
				appInfo.setUserApp(true);
			} else {
				// ϵͳ����
				appInfo.setUserApp(false);
			}
			if ((flags & ApplicationInfo.FLAG_EXTERNAL_STORAGE) == 0) {
				// �ֻ����ڴ�
				appInfo.setInRom(true);
			} else {
				// �ֻ���洢�豸
				appInfo.setInRom(false);
			}

			appInfo.setName(name);
			appInfo.setPackageName(packageName);
			appInfo.setIcon(icon);
			appInfo.setVersionCode(versionName);
			
			//���Ӧ�ó����apk��С
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
	 * ʵ��IPackageStatsObserver.Stub����ȡ����Ĵ�С
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
			// pStats.codeSize //apk�Ĵ�С
			// pStats.cacheSize;///data/data/����/cache����Ĵ�С
			// pStats.dataSize;///data/data/����/
		}
	}
}
