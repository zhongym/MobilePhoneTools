package com.zhong.mobilephonetools.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;

public class SystemInfoUtils {

	/**
	 * ��ȡ�������еĽ��̸���
	 * 
	 * @param context
	 *            ������
	 * @return �������еĽ�������
	 */
	public static int getRunningProcessCount(Context context) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		return list.size();
	}

	/**
	 * ��ȡ���õ��ڴ�
	 * 
	 * @param context
	 *            ������
	 * @return ���ؿ����ڴ�Ĵ�С long byte
	 */
	public static long getAvaiMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		// outInfo.availMem;//�����ڴ�
		// outInfo.totalMem;//���ڴ�,���ֻ����4.0���ϲſ���
		return outInfo.availMem;
	}

	/**
	 * ��ȡ�ܵ��ڴ�
	 * 
	 * @param context
	 *            ������
	 * @return �������ڴ�Ĵ�С long byte
	 */
	public static long getTotalMemory(Context context) {
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		MemoryInfo outInfo = new MemoryInfo();
//		am.getMemoryInfo(outInfo);
//		// outInfo.availMem;//�����ڴ�
//		 outInfo.totalMem;//���ڴ�,���ֻ����4.1���ϲſ���
		
		//����ǵͰ汾���á�/procĿ¼�´���˺ö�Ӳ����Ϣ�������ļ�����cpuinfo,meminfo�ȡ�
		try {
			File file = new File("/proc/meminfo");
			FileInputStream fis = new FileInputStream(file);
			BufferedReader br = new BufferedReader(new InputStreamReader(fis));
			String line = br.readLine();
			//MemTotal:         513000 kB
			StringBuilder sb = new StringBuilder();
			for(char c: line.toCharArray()){
				if(c>='0'&&c<='9'){
					sb.append(c);
				}
			}
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
