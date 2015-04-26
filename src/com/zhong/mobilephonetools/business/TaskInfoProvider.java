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
	 * ��������������еĽ�����Ϣ
	 * 
	 * @param context
	 *            ������
	 * @return �������еĽ�����Ϣ�ļ���
	 */
	public static List<TaskInfo> getAllRunningTaskInfo(Context context) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		PackageManager pm = context.getPackageManager();

		List<RunningAppProcessInfo> infos = am.getRunningAppProcesses();// ����������еĽ���
		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo info : infos) {

			TaskInfo taskInfo = new TaskInfo();

			String packageName = info.processName;// ����
			taskInfo.setPackageName(packageName);
			
			try {
				ApplicationInfo ainfo = pm.getApplicationInfo(packageName, 0);// ���ݰ�����ȡӦ����Ϣ
				Drawable icon = ainfo.loadIcon(pm);// ͼ��
				taskInfo.setIcon(icon);

				String name = (String) ainfo.loadLabel(pm);// Ӧ����
				taskInfo.setName(name);
				if ((ainfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
					// �û�Ӧ��
					taskInfo.setUserApp(true);
				} else {
					// ϵͳӦ��
					taskInfo.setUserApp(false);
				}
				long totalMem = am.getProcessMemoryInfo(new int[] { info.pid })[0].getTotalPrivateDirty() * 1024l;// ռ���ڴ�
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
