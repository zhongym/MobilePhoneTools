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
	 * 获取正在运行的进程个数
	 * 
	 * @param context
	 *            上下文
	 * @return 返回运行的进程总数
	 */
	public static int getRunningProcessCount(Context context) {

		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
		return list.size();
	}

	/**
	 * 获取可用的内存
	 * 
	 * @param context
	 *            上下文
	 * @return 返回可用内存的大小 long byte
	 */
	public static long getAvaiMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new MemoryInfo();
		am.getMemoryInfo(outInfo);
		// outInfo.availMem;//可用内存
		// outInfo.totalMem;//总内存,这个只有在4.0以上才可用
		return outInfo.availMem;
	}

	/**
	 * 获取总的内存
	 * 
	 * @param context
	 *            上下文
	 * @return 返回总内存的大小 long byte
	 */
	public static long getTotalMemory(Context context) {
//		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//		MemoryInfo outInfo = new MemoryInfo();
//		am.getMemoryInfo(outInfo);
//		// outInfo.availMem;//可用内存
//		 outInfo.totalMem;//总内存,这个只有在4.1以上才可用
		
		//这个是低版本可用。/proc目录下存放了好多硬件信息的配置文件，如cpuinfo,meminfo等。
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
			br.close();
			return Long.parseLong(sb.toString())*1024;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
}
