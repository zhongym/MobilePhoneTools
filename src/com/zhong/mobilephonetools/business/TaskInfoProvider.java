package com.zhong.mobilephonetools.business;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

import com.zhong.mobilephonetools.R;
import com.zhong.mobilephonetools.domain.TaskInfo;

public class TaskInfoProvider {

	/**
	 * 获得所有正在运行的进程信息
	 * 
	 * @param context
	 *            上下文
	 * @return 所有运行的进程信息的集合
	 */
	public static List<TaskInfo> getAllRunningTaskInfo(Context context) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();

		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();// 获得所有运行的进程
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo info : infos) {

			TaskInfo taskInfo = new TaskInfo();

			String packageName = info.processName;// 包名
			taskInfo.setPackageName(packageName);
			
			try {
				ApplicationInfo ainfo = pm.getApplicationInfo(packageName, 0);// 根据包名获取应用信息
				Drawable icon = ainfo.loadIcon(pm);// 图标
				taskInfo.setIcon(icon);

				String name = (String) ainfo.loadLabel(pm);// 应用名
				taskInfo.setName(name);
				if ((ainfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					// 用户应用
					taskInfo.setUserApp(true);
				} else {
					// 系统应用
					taskInfo.setUserApp(false);
				}
				long totalMem = am.getProcessMemoryInfo(new int[] { info.pid })[0].getTotalPrivateDirty() * 1024l;// 占用内存
				taskInfo.setTotalMem(totalMem);

			} catch (Exception e) {
				e.printStackTrace();
				taskInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_default));
				taskInfo.setName(packageName);
			}
			taskInfos.add(taskInfo);
		}
		return taskInfos;
	}
}
