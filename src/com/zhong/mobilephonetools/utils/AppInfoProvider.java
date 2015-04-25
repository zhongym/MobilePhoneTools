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
	 * ��ȡ���еİ�װ��Ӧ�ó�����Ϣ��
	 * 
	 * @param context
	 *            ������
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
}
